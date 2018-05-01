package org.knowm.xchange.binance.service.trade.params.orders;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParamCurrencyPair;

public class BinanceOrderQueryParam implements OrderQueryParamCurrencyPair {

    private CurrencyPair currencyPair = null;
    private String orderId = null;
    private Integer limit = null;
    private String offsetOrderId = null;

    @Override
    public CurrencyPair getCurrencyPair() {
        return this.currencyPair;
    }

    @Override
    public void setCurrencyPair(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    @Override
    public String getOrderId() {
        return this.orderId;
    }

    @Override
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getOffsetOrderId() {
        return offsetOrderId;
    }

    public void setOffsetOrderId(String offsetOrderId) {
        this.offsetOrderId = offsetOrderId;
    }
}
