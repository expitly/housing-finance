package com.kakaopay.housingfinance.dto.supply;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupByYearBankMaxResponse {

    private Integer year;

    @JsonProperty("bank")
    private String bankName;

    public GroupByYearBankMaxResponse(String bankName, Integer year){
        this.bankName = bankName;
        this.year = year;
    }
}
