package com.grahamcrockford.orko.marketdata;

import static com.grahamcrockford.orko.marketdata.MarketDataType.TICKER;
import static java.util.Collections.emptySet;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.grahamcrockford.orko.OrkoConfiguration;
import com.grahamcrockford.orko.exchange.AccountServiceFactory;
import com.grahamcrockford.orko.exchange.ExchangeServiceImpl;
import com.grahamcrockford.orko.exchange.TradeServiceFactory;
import com.grahamcrockford.orko.marketdata.ExchangeEventRegistry.ExchangeEventSubscription;
import com.grahamcrockford.orko.spi.TickerSpec;

import ch.qos.logback.classic.Level;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import jersey.repackaged.com.google.common.collect.Maps;

/**
 * Stack tests for {@link MarketDataSubscriptionManager}. Actually connects to exchanges.
 */
public class TestMarketDataIntegration {

  private static final TickerSpec binance = TickerSpec.builder().base("BTC").counter("USDT").exchange("binance").build();
  private static final TickerSpec binanceOddTicker = TickerSpec.builder().base("CLOAK").counter("BTC").exchange("binance").build();
  private static final TickerSpec bitfinex = TickerSpec.builder().base("BTC").counter("USD").exchange("bitfinex").build();
  private static final TickerSpec gdax = TickerSpec.builder().base("BTC").counter("USD").exchange("gdax").build();
  private static final TickerSpec bittrex = TickerSpec.builder().base("BTC").counter("USDT").exchange("bittrex").build();
  private static final Set<MarketDataSubscription> subscriptions = FluentIterable.of(binance, bitfinex, gdax, bittrex)
    .transformAndConcat(spec -> ImmutableSet.of(
      MarketDataSubscription.create(spec, MarketDataType.TICKER),
      MarketDataSubscription.create(spec, MarketDataType.ORDERBOOK)
    ))
    .toSet();

  private MarketDataSubscriptionManager marketDataSubscriptionManager;
  private ExchangeEventBus exchangeEventBus;


  @Before
  public void setup() throws TimeoutException {

    ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME)).setLevel(Level.INFO);

    OrkoConfiguration orkoConfiguration = new OrkoConfiguration();
    orkoConfiguration.setLoopSeconds(2);
    ExchangeServiceImpl exchangeServiceImpl = new ExchangeServiceImpl(orkoConfiguration);
    marketDataSubscriptionManager = new MarketDataSubscriptionManager(
      exchangeServiceImpl,
      orkoConfiguration,
      mock(TradeServiceFactory.class),
      mock(AccountServiceFactory.class),
      new EventBus()
    );
    exchangeEventBus = new ExchangeEventBus(marketDataSubscriptionManager);
    marketDataSubscriptionManager.startAsync().awaitRunning(20, SECONDS);
  }

  @After
  public void tearDown() throws TimeoutException {
    marketDataSubscriptionManager.stopAsync().awaitTerminated(20, SECONDS);
  }

  @Test
  public void testBase() throws InterruptedException {
    marketDataSubscriptionManager.updateSubscriptions(emptySet());
  }

  @Test
  public void testSubscribeUnsubscribe() throws InterruptedException {
    marketDataSubscriptionManager.updateSubscriptions(subscriptions);
    marketDataSubscriptionManager.updateSubscriptions(emptySet());
  }

  @Test
  public void testSubscribePauseAndUnsubscribe() throws InterruptedException {
    marketDataSubscriptionManager.updateSubscriptions(subscriptions);
    Thread.sleep(2500);
    marketDataSubscriptionManager.updateSubscriptions(emptySet());
  }

  @Test
  public void testSubscriptions() throws InterruptedException {
    marketDataSubscriptionManager.updateSubscriptions(subscriptions);
    try {
      ImmutableMap<MarketDataSubscription, List<CountDownLatch>> latchesBySubscriber = Maps.toMap(
        subscriptions,
        sub -> ImmutableList.of(new CountDownLatch(2), new CountDownLatch(2))
      );
      Set<Disposable> disposables = FluentIterable.from(subscriptions).transformAndConcat(sub -> ImmutableSet.<Disposable>of(
        getSubscription(marketDataSubscriptionManager, sub).subscribe(t -> {
          latchesBySubscriber.get(sub).get(0).countDown();
        }),
        getSubscription(marketDataSubscriptionManager, sub).subscribe(t -> {
          latchesBySubscriber.get(sub).get(1).countDown();
        })
      )).toSet();
      latchesBySubscriber.entrySet().stream().parallel().forEach(entry -> {
        MarketDataSubscription sub = entry.getKey();
        List<CountDownLatch> latches = entry.getValue();
        try {
          assertTrue("Missing two responses (A) for " + sub, latches.get(0).await(120, TimeUnit.SECONDS));
          System.out.println("Found responses (A) for " + sub);
          assertTrue("Missing two responses (B) for " + sub, latches.get(1).await(1, TimeUnit.SECONDS));
          System.out.println("Found responses (B) for " + sub);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      });
      disposables.forEach(Disposable::dispose);
    } finally {
      marketDataSubscriptionManager.updateSubscriptions(emptySet());
    }
  }

  @SuppressWarnings("unchecked")
  private <T> Flowable<T> getSubscription(MarketDataSubscriptionManager manager, MarketDataSubscription sub) {
    switch (sub.type()) {
      case OPEN_ORDERS:
        return (Flowable<T>) manager.getOpenOrders().filter(o -> o.spec().equals(sub.spec()));
      case ORDERBOOK:
        return (Flowable<T>) manager.getOrderBooks().filter(o -> o.spec().equals(sub.spec()));
      case TICKER:
        return (Flowable<T>) manager.getTickers().filter(o -> o.spec().equals(sub.spec()));
      case TRADES:
        return (Flowable<T>) manager.getTrades().filter(o -> o.spec().equals(sub.spec()));
      case USER_TRADE_HISTORY:
        return (Flowable<T>) manager.getUserTradeHistory().filter(o -> o.spec().equals(sub.spec()));
      case BALANCE:
        return (Flowable<T>) manager.getBalances().filter(b -> b.currency().equals(sub.spec().base()) || b.currency().equals(sub.spec().counter()));
      default:
        throw new IllegalArgumentException("Unknown market data type");
    }
  }


  @Test
  public void testEventBusSubscriptionDifferentSubscriberInner() throws InterruptedException {
    try (ExchangeEventSubscription otherSubscription = exchangeEventBus.subscribe()) {
      try (ExchangeEventSubscription subscription = exchangeEventBus.subscribe(subscriptions)) {
        AtomicBoolean called = new AtomicBoolean();
        Disposable disposable = otherSubscription.getTickers().subscribe(t -> called.set(true));
        Thread.sleep(10000);
        assertFalse(called.get());
        disposable.dispose();
      }
    }
  }


  @Test
  public void testEventBusSubscriptionDifferentSubscriberOuter() throws InterruptedException {
    try (ExchangeEventSubscription subscription = exchangeEventBus.subscribe(subscriptions)) {
      try (ExchangeEventSubscription otherSubscription = exchangeEventBus.subscribe()) {
        AtomicBoolean called = new AtomicBoolean();
        Disposable disposable = otherSubscription.getTickers().subscribe(t -> called.set(true));
        Thread.sleep(10000);
        assertFalse(called.get());
        disposable.dispose();
      }
    }
  }


  @Test
  public void testEventBusSubscriptionSameSubscriber() throws InterruptedException {
    try (ExchangeEventSubscription subscription = exchangeEventBus.subscribe(subscriptions)) {
      CountDownLatch called1 = new CountDownLatch(2);
      CountDownLatch called2 = new CountDownLatch(2);
      CountDownLatch called3 = new CountDownLatch(2);
      Disposable disposable1 = subscription.getTickers().throttleLast(200, TimeUnit.MILLISECONDS).subscribe(t -> {
        System.out.println(Thread.currentThread().getId() + " (A) received ticker: " + t);
        called1.countDown();
      });
      Disposable disposable2 = subscription.getTickers().throttleLast(200, TimeUnit.MILLISECONDS).subscribe(t -> {
        System.out.println(Thread.currentThread().getId() + " (B) received ticker: " + t);
        Thread.sleep(2000);
        called2.countDown();
      });
      Disposable disposable3 = subscription.getOrderBooks().throttleLast(1, TimeUnit.SECONDS).subscribe(t -> {
        System.out.println(Thread.currentThread().getId() + " (C) received order book: " + t.getClass().getSimpleName());
        called3.countDown();
      });
      assertTrue(called1.await(20, SECONDS));
      assertTrue(called2.await(20, SECONDS));
      assertTrue(called3.await(20, SECONDS));
      disposable1.dispose();
      disposable2.dispose();
      disposable3.dispose();
    }
  }


  @Test
  public void testEventBusMultipleSubscribersSameTicker() throws InterruptedException {
    try (ExchangeEventSubscription subscription1 = exchangeEventBus.subscribe(MarketDataSubscription.create(binance, TICKER))) {
      try (ExchangeEventSubscription subscription2 = exchangeEventBus.subscribe(MarketDataSubscription.create(binance, TICKER))) {
        CountDownLatch called1 = new CountDownLatch(2);
        CountDownLatch called2 = new CountDownLatch(2);
        Disposable disposable1 = subscription1.getTickers().subscribe(t -> {
          System.out.println(Thread.currentThread().getId() + " (A) received: " + t);
          called1.countDown();
        });
        Disposable disposable2 = subscription2.getTickers().subscribe(t -> {
          System.out.println(Thread.currentThread().getId() + " (B) received: " + t);
          called2.countDown();
        });
        assertTrue(called1.await(20, SECONDS));
        assertTrue(called2.await(20, SECONDS));
        disposable1.dispose();
        disposable2.dispose();
      }
    }
  }


  @Test
  public void test5CharacterTicker() throws InterruptedException {
    try (ExchangeEventSubscription subscription = exchangeEventBus.subscribe(MarketDataSubscription.create(binanceOddTicker, TICKER))) {
      CountDownLatch called = new CountDownLatch(2);
      Disposable disposable = subscription.getTickers().subscribe(t -> called.countDown());
      assertTrue(called.await(30, SECONDS));
      disposable.dispose();
    }
  }
}