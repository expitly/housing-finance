package com.kakaopay.housingfinance.dto.supply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class GroupByYearResponse {
    private Integer year;

    @JsonProperty("total_amount")
    private Long totalAmount;

    @JsonProperty("detail_amount")
    private Map<String, Long> detailAmount;

    @Builder
    public GroupByYearResponse(Integer year, Long totalAmount, Map<String, Long> detailAmount) {
        this.year = year;
        this.totalAmount = totalAmount;
        this.detailAmount = detailAmount;
    }
}
