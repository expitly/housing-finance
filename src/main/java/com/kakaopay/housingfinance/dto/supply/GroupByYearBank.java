package com.kakaopay.housingfinance.dto.supply;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupByYearBank {
    private Integer year;
    private String name;
    private Long amount;

    public GroupByYearBank(Integer year, String name, Long amount){
        this.year = year;
        this.name = name;
        this.amount = amount;
    }

}
