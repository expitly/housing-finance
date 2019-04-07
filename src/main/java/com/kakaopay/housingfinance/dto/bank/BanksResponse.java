package com.kakaopay.housingfinance.dto.bank;

import com.kakaopay.housingfinance.domain.bank.Bank;
import lombok.Getter;

@Getter
public class BanksResponse {
    private String name;
    private String code;

    public BanksResponse(Bank entity) {
        name = entity.getName();
        code = entity.getCode();
    }

    public BanksResponse(String name, String code){
        this.name = name;
        this.code = code;
    }
}
