package org.knowm.xchange.huobi.dto.trade.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.huobi.dto.HuobiResult;
import org.knowm.xchange.huobi.dto.trade.HuobiTrade;

public class HuobiTradesResult extends HuobiResult<HuobiTrade[]> {

    public HuobiTradesResult(
            @JsonProperty("status") String status,
            @JsonProperty("data") HuobiTrade[] result,
            @JsonProperty("err-code") String errCode,
            @JsonProperty("err-msg") String errMsg) {
        super(status, errCode, errMsg, result);
    }
}
