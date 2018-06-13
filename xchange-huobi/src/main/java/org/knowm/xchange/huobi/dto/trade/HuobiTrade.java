package org.knowm.xchange.huobi.dto.trade;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

public class HuobiTrade {

    private final long id;
    private final long orderId;
    private final long matchId;
    private final String symbol;
    private final String type;
    private final String source;
    private final BigDecimal price;
    private final BigDecimal filledAmount;
    private final BigDecimal filledFees;
    private final Date createdAt;

    public HuobiTrade(
        @JsonProperty("id") long id,
        @JsonProperty("order-id") long orderId,
        @JsonProperty("match-id") long matchId,
        @JsonProperty("symbol") String symbol,
        @JsonProperty("type") String type,
        @JsonProperty("source") String source,
        @JsonProperty("price") BigDecimal price,
        @JsonProperty("filled-amount") BigDecimal filledAmount,
        @JsonProperty("filled-fees") BigDecimal filledFees,
        @JsonProperty("created-at") Long createdAt
    ) {
        this.id = id;
        this.orderId = orderId;
        this.matchId = matchId;
        this.symbol = symbol;
        this.type = type;
        this.source = source;
        this.price = price;
        this.filledAmount = filledAmount;
        this.filledFees = filledFees;
        this.createdAt = createdAt != null ? new Date(createdAt) : null;
    }

    public long getId() {
        return id;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getMatchId() {
        return matchId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getFilledAmount() {
        return filledAmount;
    }

    public BigDecimal getFilledFees() {
        return filledFees;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public boolean isLimit() {
        return getType().equals("buy-limit") || getType().equals("sell-limit");
    }

    public boolean isMarket() {
        return getType().equals("buy-market") || getType().equals("sell-market");
    }

}
