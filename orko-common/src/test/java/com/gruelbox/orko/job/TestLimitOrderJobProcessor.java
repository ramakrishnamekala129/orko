package com.gruelbox.orko.job;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.service.trade.TradeService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gruelbox.orko.exchange.ExchangeService;
import com.gruelbox.orko.exchange.TradeServiceFactory;
import com.gruelbox.orko.job.LimitOrderJob.Direction;
import com.gruelbox.orko.notification.NotificationService;
import com.gruelbox.orko.notification.Status;
import com.gruelbox.orko.notification.StatusUpdateService;
import com.gruelbox.orko.spi.JobControl;
import com.gruelbox.orko.spi.TickerSpec;

public class TestLimitOrderJobProcessor {

  private static final BigDecimal AMOUNT = new BigDecimal(1000);
  private static final BigDecimal PRICE = new BigDecimal(95);

  private static final String BASE = "FOO";
  private static final String COUNTER = "USDT";
  private static final CurrencyPair CURRENCY_PAIR = new CurrencyPair(BASE, COUNTER);
  private static final String EXCHANGE = "fooex";

  @Mock private StatusUpdateService statusUpdateService;
  @Mock private NotificationService notificationService;
  @Mock private ExchangeService exchangeService;

  @Mock private Exchange exchange;
  @Mock private TradeServiceFactory tradeServiceFactory;
  @Mock private TradeService tradeService;
  @Mock private JobControl jobControl;

  private final AtomicInteger xChangeOrderId = new AtomicInteger();

  @Before
  public void before() throws IOException {

    MockitoAnnotations.initMocks(this);

    when(tradeServiceFactory.getForExchange(EXCHANGE)).thenReturn(tradeService);
    when(tradeService.placeLimitOrder(Mockito.any(LimitOrder.class))).thenAnswer(args -> newTradeId());
  }

  /* -------------------------------------------------------------------------------------- */

  @Test
  public void testSell() throws Exception {
    TickerSpec ex = TickerSpec.builder()
        .base(BASE)
        .counter(COUNTER)
        .exchange(EXCHANGE)
        .build();
    LimitOrderJob job = LimitOrderJob.builder()
        .amount(AMOUNT)
        .limitPrice(PRICE)
        .tickTrigger(ex)
        .direction(Direction.SELL)
        .build();

    LimitOrderJobProcessor processor = newProcessor(job);
    Status result = processor.start();
    processor.stop();

    verifyLimitSell();
    verifySentMessage();
    verifyFinished(result);
    verifyDidNothingElse();
  }


  @Test
  public void testBuy() throws Exception {
    TickerSpec ex = TickerSpec.builder()
        .base(BASE)
        .counter(COUNTER)
        .exchange(EXCHANGE)
        .build();
    LimitOrderJob job = LimitOrderJob.builder()
        .amount(AMOUNT)
        .limitPrice(PRICE)
        .tickTrigger(ex)
        .direction(Direction.BUY)
        .build();

    LimitOrderJobProcessor processor = newProcessor(job);
    Status result = processor.start();
    processor.stop();

    verifyLimitBuy();
    verifySentMessage();
    verifyFinished(result);
    verifyDidNothingElse();
  }

  @Test
  public void testBuyNoTrack() throws Exception {
    TickerSpec ex = TickerSpec.builder()
        .base(BASE)
        .counter(COUNTER)
        .exchange(EXCHANGE)
        .build();
    LimitOrderJob job = LimitOrderJob.builder()
        .amount(AMOUNT)
        .limitPrice(PRICE)
        .tickTrigger(ex)
        .direction(Direction.BUY)
        .build();

    LimitOrderJobProcessor processor = newProcessor(job);
    Status result = processor.start();
    processor.stop();

    verifyLimitBuy();
    verifySentMessage();
    verifyFinished(result);
    verifyDidNothingElse();
  }

  @Test
  public void testSellFailed() throws Exception {
    TickerSpec ex = TickerSpec.builder()
        .base(BASE)
        .counter(COUNTER)
        .exchange(EXCHANGE)
        .build();
    LimitOrderJob job = LimitOrderJob.builder()
        .amount(AMOUNT)
        .limitPrice(PRICE)
        .tickTrigger(ex)
        .direction(Direction.SELL)
        .build();

    when(tradeService.placeLimitOrder(Mockito.any(LimitOrder.class)))
      .thenThrow(new RuntimeException());

    LimitOrderJobProcessor processor = newProcessor(job);
    Status result = processor.start();
    processor.stop();

    verifyLimitSell();
    verifySentError();
    verifyFinished(result);
    verifyDidNothingElse();
  }

  @Test
  public void testBuyFailed() throws Exception {
    TickerSpec ex = TickerSpec.builder()
        .base(BASE)
        .counter(COUNTER)
        .exchange(EXCHANGE)
        .build();
    LimitOrderJob job = LimitOrderJob.builder()
        .amount(AMOUNT)
        .limitPrice(PRICE)
        .tickTrigger(ex)
        .direction(Direction.BUY)
        .build();

    when(tradeService.placeLimitOrder(Mockito.any(LimitOrder.class)))
      .thenThrow(new RuntimeException());

    LimitOrderJobProcessor processor = newProcessor(job);
    Status result = processor.start();
    processor.stop();

    verifyLimitBuy();
    verifySentError();
    verifyFinished(result);
    verifyDidNothingElse();
  }

  private LimitOrderJobProcessor newProcessor(LimitOrderJob job) {
    return new LimitOrderJobProcessor(job, jobControl, statusUpdateService, notificationService, tradeServiceFactory);
  }


  /* ---------------------------------- Utility methods  ---------------------------------------------------- */

  private void verifyDidNothingElse() {
    verifyNoMoreInteractions(notificationService, tradeService, jobControl);
  }

  private void verifySentError() {
    verify(notificationService).error(Mockito.anyString(), Mockito.any(RuntimeException.class));
  }

  private void verifySentMessage() {
    verify(notificationService).alert(Mockito.anyString());
  }

  private void verifyLimitSell() throws IOException {
    ArgumentCaptor<LimitOrder> captor = ArgumentCaptor.forClass(LimitOrder.class);
    verify(tradeService).placeLimitOrder(captor.capture());
    Assert.assertEquals(PRICE, captor.getValue().getLimitPrice());
    Assert.assertEquals(AMOUNT, captor.getValue().getOriginalAmount());
    Assert.assertEquals(CURRENCY_PAIR, captor.getValue().getCurrencyPair());
    Assert.assertEquals(Order.OrderType.ASK, captor.getValue().getType());
  }

  private void verifyLimitBuy() throws IOException {
    ArgumentCaptor<LimitOrder> captor = ArgumentCaptor.forClass(LimitOrder.class);
    verify(tradeService).placeLimitOrder(captor.capture());
    Assert.assertEquals(PRICE, captor.getValue().getLimitPrice());
    Assert.assertEquals(AMOUNT, captor.getValue().getOriginalAmount());
    Assert.assertEquals(CURRENCY_PAIR, captor.getValue().getCurrencyPair());
    Assert.assertEquals(Order.OrderType.BID, captor.getValue().getType());
  }

  private void verifyFinished(Status status) {
    Assert.assertEquals(Status.SUCCESS, status);
  }

  private String newTradeId() {
    return Integer.toString(xChangeOrderId.incrementAndGet());
  }
}