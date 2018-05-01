package org.knowm.xchange.hitbtc.v2.service;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.trade.params.TradeHistoryParamCurrencyPair;
import org.knowm.xchange.service.trade.params.TradeHistoryParamLimit;
import org.knowm.xchange.service.trade.params.TradeHistoryParamOffset;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsTimeSpan;

import java.util.Date;

public class HitbtcTradeHistoryParams
    implements TradeHistoryParamLimit, TradeHistoryParamOffset, TradeHistoryParamCurrencyPair, TradeHistoryParamsTimeSpan {

  private CurrencyPair pair;
  private Integer limit;
  private Long offset;
  private Date startTime;
  private Date endTime;

  public HitbtcTradeHistoryParams(CurrencyPair pair, Integer limit, Long offset, Date startTime, Date endTime) {
    this.pair = pair;
    this.limit = limit;
    this.offset = offset;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  @Override
  public CurrencyPair getCurrencyPair() {
    return pair;
  }

  @Override
  public void setCurrencyPair(CurrencyPair pair) {
    this.pair = pair;
  }

  @Override
  public Integer getLimit() {
    return limit;
  }

  @Override
  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  @Override
  public Long getOffset() {
    return offset;
  }

  @Override
  public void setOffset(Long offset) {
    this.offset = offset;
  }

  @Override
  public Date getStartTime() {
    return this.startTime;
  }

  @Override
  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  @Override
  public Date getEndTime() {
    return endTime;
  }

  @Override
  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }
}
