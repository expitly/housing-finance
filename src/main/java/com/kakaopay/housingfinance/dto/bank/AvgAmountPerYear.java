package com.kakaopay.housingfinance.dto.bank;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AvgAmountPerYear {
    private Integer year;
    private Long amount;

    public AvgAmountPerYear(Integer year, Long amount){
        this.year = year;
        this.amount = amount;
    }
}
