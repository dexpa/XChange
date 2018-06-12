package org.knowm.xchange.huobi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.*;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.huobi.HuobiAdapters;
import org.knowm.xchange.huobi.dto.trade.HuobiOrder;
import org.knowm.xchange.huobi.service.trade.params.orders.HuobiOrderQueryParam;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.*;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParams;

public class HuobiTradeService extends HuobiTradeServiceRaw implements TradeService {

  public HuobiTradeService(Exchange exchange) {
    super(exchange);
  }

  @Override
  public UserTrades getTradeHistory(TradeHistoryParams tradeHistoryParams) throws IOException {
    CurrencyPair currencyPair = null;
    if(tradeHistoryParams instanceof TradeHistoryParamCurrencyPair) {
      currencyPair = ((TradeHistoryParamCurrencyPair) tradeHistoryParams).getCurrencyPair();
    }
    if(currencyPair == null) {
      throw new ExchangeException("tradeHistoryParams must implements TradeHistoryParamCurrencyPair");
    }
    Date startDate = null;
    Date endDate = null;
    if(tradeHistoryParams instanceof TradeHistoryParamsTimeSpan) {
      startDate = ((TradeHistoryParamsTimeSpan) tradeHistoryParams).getStartTime();
      endDate = ((TradeHistoryParamsTimeSpan) tradeHistoryParams).getEndTime();
    }
    Integer size = null;
    if(tradeHistoryParams instanceof TradeHistoryParamLimit) {
      size = ((TradeHistoryParamLimit) tradeHistoryParams).getLimit();
    }
    return HuobiAdapters.adaptTrades(getHuobiTrades(
            currencyPair,
            null,
            startDate,
            endDate,
            null,
            null,
            size
    ));
  }

  @Override
  public Collection<Order> getOrder(String... orderIds) throws IOException {
    return HuobiAdapters.adaptOrders(getHuobiOrder(orderIds));
  }

  @Override
  public Collection<Order> getOrder(OrderQueryParams... orderQueryParams) throws IOException {

      List<Order> orders = new ArrayList<>();
      for(OrderQueryParams param : orderQueryParams) {
          if(param.getOrderId() != null) {
              orders.addAll(getOrder(param.getOrderId()));
          } else {
              CurrencyPair currencyPair = null;
              Date startDate = null;
              Date endDate = null;
              Integer size = null;

              if(param instanceof HuobiOrderQueryParam) {
                  HuobiOrderQueryParam huobiParam = (HuobiOrderQueryParam) param;
                  currencyPair = huobiParam.getCurrencyPair();
                  startDate = huobiParam.getStartDate();
                  endDate = huobiParam.getEndDate();
                  size = huobiParam.getSize();
              } else if (param instanceof OrderQueryParamCurrencyPair) {
                  currencyPair = ((OrderQueryParamCurrencyPair) param).getCurrencyPair();
              }
              if(currencyPair == null) {
                  throw new ExchangeException("orderQueryParams must implements OrderQueryParamCurrencyPair");
              }
              List<HuobiOrder> huobiOrders = getHuobiOrder(
                  currencyPair,
                  null,
                  startDate,
                  endDate,
                  null,
                  null,
                  size
              );
              orders.addAll(HuobiAdapters.adaptOrders(huobiOrders));
          }
      }

      return orders;
  }

  @Override
  public OpenOrdersParams createOpenOrdersParams() {
    return null;
  }

  @Override
  public TradeHistoryParams createTradeHistoryParams() {
    return null;
  }

  @Override
  public boolean cancelOrder(String orderId) throws IOException {
    return cancelHuobiOrder(orderId).length() > 0;
  }

  @Override
  public boolean cancelOrder(CancelOrderParams cancelOrderParams) throws IOException {
    return cancelOrderParams instanceof CancelOrderByIdParams
        && cancelOrder(((CancelOrderByIdParams) cancelOrderParams).getOrderId());
  }

  @Override
  public String placeMarketOrder(MarketOrder marketOrder) throws IOException {
    return placeHuobiMarketOrder(marketOrder);
  }

  @Override
  public OpenOrders getOpenOrders() throws IOException {
    return getOpenOrders(createOpenOrdersParams());
  }

  @Override
  public OpenOrders getOpenOrders(OpenOrdersParams openOrdersParams) throws IOException {
    HuobiOrder[] openOrders = getHuobiOpenOrders();
    return HuobiAdapters.adaptOpenOrders(openOrders);
  }

  @Override
  public String placeLimitOrder(LimitOrder limitOrder) throws IOException {
    return placeHuobiLimitOrder(limitOrder);
  }

  @Override
  public String placeStopOrder(StopOrder stopOrder) throws IOException {
    return null;
  }
}
