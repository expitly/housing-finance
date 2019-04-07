package com.kakaopay.housingfinance.dto.bank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MinMaxGroupByYearResponse {
    private String bank;

    @JsonProperty("support_amount")
    private List<AvgAmountPerYear> supportAmount;

    @Builder
    public MinMaxGroupByYearResponse(String bank, List<AvgAmountPerYear> supportAmount) {
        this.bank = bank;
        this.supportAmount = supportAmount;
    }
}
