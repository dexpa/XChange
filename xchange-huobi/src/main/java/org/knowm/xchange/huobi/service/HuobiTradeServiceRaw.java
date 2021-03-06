package org.knowm.xchange.huobi.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.huobi.HuobiAdapters;
import org.knowm.xchange.huobi.HuobiUtils;
import org.knowm.xchange.huobi.dto.trade.HuobiCreateOrderRequest;
import org.knowm.xchange.huobi.dto.trade.HuobiOrder;
import org.knowm.xchange.huobi.dto.trade.HuobiTrade;
import org.knowm.xchange.huobi.dto.trade.results.*;

class HuobiTradeServiceRaw extends HuobiBaseService {

  HuobiTradeServiceRaw(Exchange exchange) {
    super(exchange);
  }

  HuobiOrder[] getHuobiOpenOrders() throws IOException {
    String states = "pre-submitted,submitted,partial-filled";
    HuobiOrdersResult result =
        huobi.getOpenOrders(
            null,
            null,
            null,
            null,
            states,
            null,
            null,
            null,
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(result);
  }

  String cancelHuobiOrder(String orderId) throws IOException {
    HuobiCancelOrderResult result =
        huobi.cancelOrder(
            orderId,
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(result);
  }

  String placeHuobiLimitOrder(LimitOrder limitOrder) throws IOException {
    String type;
    if (limitOrder.getType() == OrderType.BID) {
      type = "buy-limit";
    } else if (limitOrder.getType() == OrderType.ASK) {
      type = "sell-limit";
    } else {
      throw new ExchangeException("Unsupported order type.");
    }

    HuobiOrderResult result =
        huobi.placeLimitOrder(
            new HuobiCreateOrderRequest(
                String.valueOf(
                    ((HuobiAccountServiceRaw) exchange.getAccountService())
                        .getAccounts()[0].getId()),
                limitOrder.getOriginalAmount().setScale(4, BigDecimal.ROUND_DOWN).toString(),
                limitOrder.getLimitPrice().toString(),
                HuobiUtils.createHuobiCurrencyPair(limitOrder.getCurrencyPair()),
                type),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);

    return checkResult(result);
  }

  String placeHuobiMarketOrder(MarketOrder limitOrder) throws IOException {
    String type;
    if (limitOrder.getType() == OrderType.BID) {
      type = "buy-market";
    } else if (limitOrder.getType() == OrderType.ASK) {
      type = "sell-market";
    } else {
      throw new ExchangeException("Unsupported order type.");
    }
    HuobiOrderResult result =
        huobi.placeMarketOrder(
            new HuobiCreateOrderRequest(
                String.valueOf(
                    ((HuobiAccountServiceRaw) exchange.getAccountService())
                        .getAccounts()[0].getId()),
                limitOrder.getOriginalAmount().setScale(4, BigDecimal.ROUND_DOWN).toString(),
                null,
                HuobiUtils.createHuobiCurrencyPair(limitOrder.getCurrencyPair()),
                type),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(result);
  }

  List<HuobiOrder> getHuobiOrder(String... orderIds) throws IOException {
    List<HuobiOrder> orders = new ArrayList<>();
    for (String orderId : orderIds) {
      HuobiOrderInfoResult orderInfoResult =
          huobi.getOrder(
              orderId,
              exchange.getExchangeSpecification().getApiKey(),
              HuobiDigest.HMAC_SHA_256,
              2,
              HuobiUtils.createUTCDate(exchange.getNonceFactory()),
              signatureCreator);
      orders.add(checkResult(orderInfoResult));
    }
    return orders;
  }

    List<HuobiOrder> getHuobiOrder(CurrencyPair currencyPair,
                                   String types,
                                   Date startDate,
                                   Date endDate,
                                   String from,
                                   String direct,
                                   Integer size) throws IOException {
        String states = "pre-submitted,submitted,partial-filled,partial-canceled,filled,canceled";
        String startDateString = null;
        if (startDate != null) {
            startDateString = tradesDateFormat.format(startDate);
        }
        String endDateString = null;
        if (endDate != null) {
            endDateString = tradesDateFormat.format(endDate);
        }
        String sizeString = null;
        if (size != null) {
            sizeString = size.toString();
        }

        HuobiOrdersResult result =
                huobi.getOpenOrders(
                        HuobiUtils.createHuobiCurrencyPair(currencyPair),
                        types,
                        startDateString,
                        endDateString,
                        states,
                        from,
                        direct,
                        sizeString,
                        exchange.getExchangeSpecification().getApiKey(),
                        HuobiDigest.HMAC_SHA_256,
                        2,
                        HuobiUtils.createUTCDate(exchange.getNonceFactory()),
                        signatureCreator);

        return new ArrayList<>(Arrays.asList((checkResult(result))));
    }

  private DateFormat tradesDateFormat = new SimpleDateFormat("yyyy-MM-dd");

  HuobiTrade[] getHuobiTrades(CurrencyPair currencyPair,
                              String types,
                              Date startDate,
                              Date endDate,
                              String from,
                              String direct,
                              Integer size) throws IOException {

    String startDateString = null;
    if (startDate != null) {
      startDateString = tradesDateFormat.format(startDate);
    }
    String endDateString = null;
    if (endDate != null) {
      endDateString = tradesDateFormat.format(endDate);
    }
    String sizeString = null;
    if (size != null) {
      sizeString = size.toString();
    }

    HuobiTradesResult result = huobi.getTrades(
            HuobiUtils.createHuobiCurrencyPair(currencyPair),
            types,
            startDateString,
            endDateString,
            from,
            direct,
            sizeString,
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator
    );

    return checkResult(result);
  }
}
