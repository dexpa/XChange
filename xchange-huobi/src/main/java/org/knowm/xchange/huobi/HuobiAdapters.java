package org.knowm.xchange.huobi;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderStatus;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.account.Balance;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.dto.meta.CurrencyMetaData;
import org.knowm.xchange.dto.meta.CurrencyPairMetaData;
import org.knowm.xchange.dto.meta.ExchangeMetaData;
import org.knowm.xchange.dto.trade.*;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.huobi.dto.account.HuobiBalanceRecord;
import org.knowm.xchange.huobi.dto.account.HuobiBalanceSum;
import org.knowm.xchange.huobi.dto.marketdata.HuobiAsset;
import org.knowm.xchange.huobi.dto.marketdata.HuobiAssetPair;
import org.knowm.xchange.huobi.dto.marketdata.HuobiTicker;
import org.knowm.xchange.huobi.dto.trade.HuobiOrder;
import org.knowm.xchange.huobi.dto.trade.HuobiTrade;

public class HuobiAdapters {

  public static Ticker adaptTicker(HuobiTicker huobiTicker, CurrencyPair currencyPair) {
    Ticker.Builder builder = new Ticker.Builder();
    builder.open(huobiTicker.getOpen());
    builder.ask(huobiTicker.getAsk().getPrice());
    builder.bid(huobiTicker.getBid().getPrice());
    builder.last(huobiTicker.getClose());
    builder.high(huobiTicker.getHigh());
    builder.low(huobiTicker.getLow());
    builder.volume(huobiTicker.getVol());
    builder.timestamp(huobiTicker.getTs());
    builder.currencyPair(currencyPair);
    return builder.build();
  }

  static ExchangeMetaData adaptToExchangeMetaData(
      HuobiAssetPair[] assetPairs, HuobiAsset[] assets) {
    HuobiUtils.setHuobiAssets(assets);
    HuobiUtils.setHuobiAssetPairs(assetPairs);

    Map<CurrencyPair, CurrencyPairMetaData> pairs = new HashMap<>();
    for (HuobiAssetPair pair : assetPairs) {
      pairs.put(adaptCurrencyPair(pair.getKey()), adaptPair(pair));
    }

    Map<Currency, CurrencyMetaData> currencies = new HashMap<>();
    for (HuobiAsset asset : assets) {
      Currency currency = adaptCurrency(asset.getAsset());
      currencies.put(currency, new CurrencyMetaData(0, null));
    }

    return new ExchangeMetaData(pairs, currencies, null, null, false);
  }

  private static CurrencyPair adaptCurrencyPair(String currencyPair) {
    return HuobiUtils.translateHuobiCurrencyPair(currencyPair);
  }

  private static CurrencyPairMetaData adaptPair(HuobiAssetPair pair) {
    return new CurrencyPairMetaData(null, null, null, new Integer(pair.getPricePrecision()));
  }

  private static Currency adaptCurrency(String currency) {
    return HuobiUtils.translateHuobiCurrencyCode(currency);
  }

  public static Wallet adaptWallet(Map<String, HuobiBalanceSum> huobiWallet) {
    List<Balance> balances = new ArrayList<>(huobiWallet.size());
    for (Map.Entry<String, HuobiBalanceSum> record : huobiWallet.entrySet()) {
      try {
        Currency currency = adaptCurrency(record.getKey());
        Balance balance =
                new Balance(
                        currency,
                        record.getValue().getTotal(),
                        record.getValue().getAvailable(),
                        record.getValue().getFrozen());
        balances.add(balance);
      } catch (ExchangeException ignored) { /* ignore cause some exchange pair break this code */ }
    }
    return new Wallet(balances);
  }

  public static Map<String, HuobiBalanceSum> adaptBalance(HuobiBalanceRecord[] huobiBalance) {
    Map<String, HuobiBalanceSum> map = new HashMap<>();
    for (HuobiBalanceRecord record : huobiBalance) {
      HuobiBalanceSum sum = map.get(record.getCurrency());
      if (sum == null) {
        sum = new HuobiBalanceSum();
        map.put(record.getCurrency(), sum);
      }
      if (record.getType().equals("trade")) {
        sum.setAvailable(record.getBalance());
      } else if (record.getType().equals("frozen")) {
        sum.setFrozen(record.getBalance());
      }
    }
    return map;
  }

  public static OpenOrders adaptOpenOrders(HuobiOrder[] openOrders) {
    List<LimitOrder> limitOrders = new ArrayList<>();
    for (HuobiOrder openOrder : openOrders) {
      if (openOrder.isLimit()) {
        limitOrders.add((LimitOrder) adaptOrder(openOrder));
      }
    }
    return new OpenOrders(limitOrders);
  }

  private static Order adaptOrder(HuobiOrder openOrder) {
    Order order = null;
    OrderType orderType = adaptOrderType(openOrder.getType());
    CurrencyPair currencyPair = adaptCurrencyPair(openOrder.getSymbol());
    if (openOrder.isMarket()) {
      order = new MarketOrder(
              orderType,
              openOrder.getAmount(),
              currencyPair,
              String.valueOf(openOrder.getId()),
              openOrder.getCreatedAt(),
              openOrder.getFieldCashAmount() != null ? openOrder.getFieldCashAmount().divide(openOrder.getAmount(), 8, RoundingMode.HALF_UP) : null,
              openOrder.getFieldAmount(),
              openOrder.getFieldFees(),
              adaptOrderStatus(openOrder.getState())
      );
    }
    if (openOrder.isLimit()) {
      order =
          new LimitOrder(
              orderType,
              openOrder.getAmount(),
              currencyPair,
              String.valueOf(openOrder.getId()),
              openOrder.getCreatedAt(),
              openOrder.getPrice(),
              openOrder.getFieldCashAmount() != null ? openOrder.getFieldCashAmount().divide(openOrder.getAmount(), 8, RoundingMode.HALF_UP) : null,
              openOrder.getFieldAmount(),
              openOrder.getFieldFees(),
              adaptOrderStatus(openOrder.getState())
          );
    }
    return order;
  }

  private static UserTrade adaptTrade(HuobiTrade huobiTrade) {

    CurrencyPair pair = adaptCurrencyPair(huobiTrade.getSymbol());

    Order.OrderType orderType = adaptOrderType(huobiTrade.getType());

    return new UserTrade(
      adaptOrderType(huobiTrade.getType()),
      huobiTrade.getFilledAmount(),
      pair,
      huobiTrade.getPrice(),
      huobiTrade.getCreatedAt(),
      Long.toString(huobiTrade.getId()),
      Long.toString(huobiTrade.getOrderId()),
      huobiTrade.getFilledFees(),
      orderType == OrderType.BID ? pair.base : pair.counter
    );
  }

  private static OrderStatus adaptOrderStatus(String huobiStatus) {
    OrderStatus result = OrderStatus.UNKNOWN;
    switch (huobiStatus) {
      case "pre-submitted":
        result = OrderStatus.PENDING_NEW;
        break;
      case "submitting":
        result = OrderStatus.PENDING_NEW;
        break;
      case "submitted":
        result = OrderStatus.NEW;
        break;
      case "partial-filled":
        result = OrderStatus.PARTIALLY_FILLED;
        break;
      case "partial-canceled":
        result = OrderStatus.PARTIALLY_CANCELED;
        break;
      case "filled":
        result = OrderStatus.FILLED;
        break;
      case "canceled":
        result = OrderStatus.CANCELED;
        break;
    }
    return result;
  }

  private static OrderType adaptOrderType(String orderType) {
    return orderType.substring(0, 3).equals("buy") ? OrderType.BID : OrderType.ASK;
  }

  public static List<Order> adaptOrders(List<HuobiOrder> huobiOrders) {
    List<Order> orders = new ArrayList<>();
    for (HuobiOrder order : huobiOrders) {
      orders.add(adaptOrder(order));
    }
    return orders;
  }

  public static UserTrades adaptTrades(HuobiTrade[] huobiTrades) {
    List<UserTrade> trades = new ArrayList<>();
    for(HuobiTrade huobiTrade : huobiTrades) {
      trades.add(adaptTrade(huobiTrade));
    }
    return new UserTrades(trades, Trades.TradeSortType.SortByTimestamp);
  }
}
