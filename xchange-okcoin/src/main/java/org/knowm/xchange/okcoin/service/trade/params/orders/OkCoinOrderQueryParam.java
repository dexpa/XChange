package org.knowm.xchange.okcoin.service.trade.params.orders;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParamCurrencyPair;

public class OkCoinOrderQueryParam implements OrderQueryParamCurrencyPair {

    private CurrencyPair currencyPair;
    private String orderId;
    private Integer pageNumber = null;
    private Integer pageLength = null;

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

    public OkCoinOrderQueryParam(CurrencyPair currencyPair, String orderId) {
        this.currencyPair = currencyPair;
        this.orderId = orderId;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageLength() {
        return pageLength;
    }

    public void setPageLength(Integer pageLength) {
        this.pageLength = pageLength;
    }
}
