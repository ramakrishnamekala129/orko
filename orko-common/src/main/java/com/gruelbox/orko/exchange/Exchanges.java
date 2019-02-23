/**
 * Orko
 * Copyright © 2018-2019 Graham Crockford
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gruelbox.orko.exchange;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.Exchange;
import org.reflections.Reflections;

import com.google.common.base.Suppliers;

import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;

public final class Exchanges {

  private Exchanges() {
    // Not constructable
  }

  public static final String BINANCE = "binance";
  public static final String GDAX = "gdax";
  public static final String KUCOIN = "kucoin";
  public static final String BITTREX = "bittrex";
  public static final String BITFINEX = "bitfinex";
  public static final String BITMEX = "bitmex";
  public static final String KRAKEN = "kraken";

  static final Supplier<List<Class<? extends Exchange>>> EXCHANGE_TYPES = Suppliers.memoize(
      () -> new Reflections("org.knowm.xchange")
      .getSubTypesOf(Exchange.class)
      .stream()
      .filter(c -> !c.equals(BaseExchange.class))
      .collect(Collectors.toList()));

  static final Supplier<List<Class<? extends StreamingExchange>>> STREAMING_EXCHANGE_TYPES = Suppliers.memoize(
      () -> new Reflections("info.bitrich.xchangestream")
      .getSubTypesOf(StreamingExchange.class)
      .stream()
      .collect(Collectors.toList()));

  /**
   * Converts the friendly name for an exchange into the exchange class.
   *
   * @param friendlyName The friendly class name.
   * @return The exchange class
   */
  public static Class<? extends Exchange> friendlyNameToClass(String friendlyName) {

    if (friendlyName.equals(GDAX))
      return CoinbaseProStreamingExchange.class;

    Optional<Class<? extends StreamingExchange>> streamingResult = STREAMING_EXCHANGE_TYPES.get()
        .stream()
        .filter(c -> c.getSimpleName().replace("StreamingExchange", "").equalsIgnoreCase(friendlyName))
        .findFirst();
    if (streamingResult.isPresent())
      return streamingResult.get();

    Optional<Class<? extends Exchange>> result = EXCHANGE_TYPES.get()
        .stream()
        .filter(c -> c.getSimpleName().replace("Exchange", "").equalsIgnoreCase(friendlyName))
        .findFirst();
    if (!result.isPresent())
      throw new IllegalArgumentException("Unknown exchange [" + friendlyName + "]");
    return result.get();
  }


  /**
   * Friendly names for exchanges.
   *
   * @param exchange The exchange code.
   * @return The friendly name.
   */
  public static String name(String exchange) {
    switch(exchange) {
      case GDAX:
        return "Coinbase Pro";
      case BITMEX:
      case BITTREX:
      case KRAKEN:
        return StringUtils.capitalize(exchange) + " (beta)";
      default:
        return StringUtils.capitalize(exchange);
    }
  }


  /**
   * Hard coded reflinks for the exchanges. This is intended to support the project.
   * I don't expect much use but every little bit can help keep the project alive,
   * so please don't modify these.
   *
   * @param exchange The exchange name.
   * @return The reflink.
   */
  public static String refLink(String exchange) {
    switch(exchange) {
      case BINANCE : return "https://www.binance.com/?ref=11396297";
      case BITMEX : return "https://www.bitmex.com/register/vQIGWT";
      case GDAX : return "https://pro.coinbase.com";
      case KUCOIN : return "https://www.kucoin.com/#/?r=E649ku";
      case BITTREX : return "https://bittrex.com/";
      case BITFINEX : return "https://www.bitfinex.com/";
      case KRAKEN : return "https://www.kraken.com/";
      default : return "#";
    }
  }
}
