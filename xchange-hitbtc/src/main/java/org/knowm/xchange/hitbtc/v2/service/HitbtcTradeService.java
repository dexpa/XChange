package org.knowm.xchange.hitbtc.v2.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.StopOrder;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.hitbtc.v2.HitbtcAdapters;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcOrder;
import org.knowm.xchange.hitbtc.v2.dto.HitbtcOwnTrade;
import org.knowm.xchange.hitbtc.v2.service.trade.params.orders.HitbtcOrderQueryParam;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.*;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParams;

public class HitbtcTradeService extends HitbtcTradeServiceRaw implements TradeService {

  public HitbtcTradeService(Exchange exchange) {
    super(exchange);
  }

  @Override
  public OpenOrders getOpenOrders() throws IOException {
    return getOpenOrders(createOpenOrdersParams());
  }

  @Override
  public OpenOrders getOpenOrders(OpenOrdersParams params) throws IOException {
    List<HitbtcOrder> openOrdersRaw = getOpenOrdersRaw();
    return HitbtcAdapters.adaptOpenOrders(openOrdersRaw);
  }

  @Override
  public String placeMarketOrder(MarketOrder marketOrder) throws IOException {
    return placeMarketOrderRaw(marketOrder).clientOrderId;
  }

  @Override
  public String placeLimitOrder(LimitOrder limitOrder) throws IOException {
    return placeLimitOrderRaw(limitOrder).clientOrderId;
  }

  @Override
  public String placeStopOrder(StopOrder stopOrder) throws IOException {
    throw new NotYetImplementedForExchangeException();
  }

  @Override
  public boolean cancelOrder(String orderId) throws IOException {
    HitbtcOrder cancelOrderRaw = cancelOrderRaw(orderId);
    return "canceled".equals(cancelOrderRaw.status);
  }

  @Override
  public boolean cancelOrder(CancelOrderParams orderParams) throws IOException {
    if (orderParams instanceof CancelOrderByIdParams) {
      return cancelOrder(((CancelOrderByIdParams) orderParams).getOrderId());
    } else {
      return false;
    }
  }

  /** Required parameters: {@link TradeHistoryParamPaging} {@link TradeHistoryParamCurrencyPair} */
  @Override
  public UserTrades getTradeHistory(TradeHistoryParams params) throws IOException {

    Integer limit = 1000;
    Long offset = 0L;

    if (params instanceof TradeHistoryParamLimit) {
      limit = ((TradeHistoryParamLimit) params).getLimit();
    }

    if (params instanceof TradeHistoryParamOffset) {
      TradeHistoryParamOffset tradeHistoryParamOffset = (TradeHistoryParamOffset) params;
      offset = tradeHistoryParamOffset.getOffset();
    }

    String symbol = null;
    if (params instanceof TradeHistoryParamCurrencyPair) {
      CurrencyPair pair = ((TradeHistoryParamCurrencyPair) params).getCurrencyPair();
      symbol = HitbtcAdapters.adaptCurrencyPair(pair);
    }

    String from = null;
    String till = null;

    if (params instanceof TradeHistoryParamsTimeSpan) {
      Date startTime = ((TradeHistoryParamsTimeSpan) params).getStartTime();
      Date endTime = ((TradeHistoryParamsTimeSpan) params).getEndTime();
      from = startTime == null ? null : HitbtcAdapters.adaptTimestamp(startTime.getTime());
      till = endTime == null ? null : HitbtcAdapters.adaptTimestamp(endTime.getTime());
    }

    List<HitbtcOwnTrade> tradeHistoryRaw = getTradeHistoryRaw(symbol, limit, offset, from, till);
    return HitbtcAdapters.adaptTradeHistory(tradeHistoryRaw, exchange.getExchangeMetaData());
  }

  @Override
  public TradeHistoryParams createTradeHistoryParams() {
    return new HitbtcTradeHistoryParams(null, 100, 0L, null, null);
  }

  @Override
  public OpenOrdersParams createOpenOrdersParams() {
    return null;
  }

  @Override
  public Collection<Order> getOrder(String... orderIds) throws IOException {
    if (orderIds == null || orderIds.length == 0) {
      return new ArrayList<>();
    }

    Collection<Order> orders = new ArrayList<>();
    for (String orderId : orderIds) {
      addOrderById(orders, orderId);
    }

    return orders;
  }

  @Override
  public Collection<Order> getOrder(OrderQueryParams... params) throws IOException {

    Collection<Order> orders = new ArrayList<>();
    for (OrderQueryParams param : params) {
      if(param instanceof HitbtcOrderQueryParam) {
        HitbtcOrderQueryParam hitbtcOrderQueryParam = (HitbtcOrderQueryParam) param;
        if(hitbtcOrderQueryParam.getOrderId() != null) {
          addOrderById(orders, hitbtcOrderQueryParam.getOrderId());
        } else {
          CurrencyPair pair = hitbtcOrderQueryParam.getCurrencyPair();
          String symbol =  pair == null ? null : HitbtcAdapters.adaptCurrencyPair(pair);
          Long fromTimestamp = hitbtcOrderQueryParam.getFrom();
          Long tillTimestamp = hitbtcOrderQueryParam.getTill();
          String from = hitbtcOrderQueryParam.getFrom() == null ? null : HitbtcAdapters.adaptTimestamp(fromTimestamp);
          String till = hitbtcOrderQueryParam.getTill() == null ? null : HitbtcAdapters.adaptTimestamp(tillTimestamp);
          Long limit = hitbtcOrderQueryParam.getLimit();
          Long offset = hitbtcOrderQueryParam.getOffset();
          List<HitbtcOrder> hitbtcOrders = getHitbtcOrder(
            symbol,
            null,
            from,
            till,
            limit,
            offset
          );
          hitbtcOrders.forEach(order -> orders.add(HitbtcAdapters.adaptOrder(order)));
        }
      } else if (param instanceof OrderQueryParamCurrencyPair) {
        OrderQueryParamCurrencyPair orderQueryParamCurrencyPair = (OrderQueryParamCurrencyPair) param;
        if(orderQueryParamCurrencyPair.getOrderId() != null) {
          addOrderById(orders, orderQueryParamCurrencyPair.getOrderId());
        } else {
          CurrencyPair pair = orderQueryParamCurrencyPair.getCurrencyPair();
          String symbol =  pair == null ? null : HitbtcAdapters.adaptCurrencyPair(pair);
          List<HitbtcOrder> hitbtcOrders = getHitbtcOrder(
            symbol,
            null,
            null,
            null,
            null,
            null
          );
          hitbtcOrders.forEach(order -> orders.add(HitbtcAdapters.adaptOrder(order)));
        }
      } else if(param.getOrderId() != null) {
          addOrderById(orders, param.getOrderId());
      } else {
        throw new ExchangeException(
          "Parameters must be an instance of " +
          "HitbtcOrderQueryParam or " +
          "OrderQueryParamCurrencyPair or " +
          "OrderQueryParams with not null order id"
        );
      }
    }

    return orders;
  }

  private void addOrderById(Collection<Order> buffer, String orderId) throws IOException {
    Collection<HitbtcOrder> rawOrder = getHitbtcOrder(null, orderId, null, null, null, null);
    if (rawOrder != null && rawOrder.size() != 0) buffer.add(HitbtcAdapters.adaptOrder(rawOrder.iterator().next()));
  }

  @Override
  public void verifyOrder(LimitOrder limitOrder) {

    throw new NotYetImplementedForExchangeException();
  }

  @Override
  public void verifyOrder(MarketOrder marketOrder) {

    throw new NotYetImplementedForExchangeException();
  }
}
