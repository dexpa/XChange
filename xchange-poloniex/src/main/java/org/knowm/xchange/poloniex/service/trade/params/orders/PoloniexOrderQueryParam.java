package org.knowm.xchange.poloniex.service.trade.params.orders;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParamCurrencyPair;

public class PoloniexOrderQueryParam implements OrderQueryParamCurrencyPair {

    private CurrencyPair currencyPair = null;
    private String orderId = null;

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

    public PoloniexOrderQueryParam() {}

    public PoloniexOrderQueryParam(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    public PoloniexOrderQueryParam(String orderId, CurrencyPair currencyPair) {
        this.orderId = orderId;
        this.currencyPair = currencyPair;
    }

}
