package com.kakaopay.housingfinance.domain.supply;

import com.kakaopay.housingfinance.common.type.AggregationType;
import com.kakaopay.housingfinance.domain.bank.Bank;
import com.kakaopay.housingfinance.dto.bank.AvgAmountPerYear;
import com.kakaopay.housingfinance.dto.supply.GroupByYearBankMaxResponse;
import com.kakaopay.housingfinance.dto.supply.GroupByYearBank;

import java.util.List;

public interface SupplyRepositoryCustom {
    GroupByYearBankMaxResponse getMaxGroupByYearBank();

    AvgAmountPerYear getAvgGroupByYear(Bank bank, AggregationType type);

    List<GroupByYearBank> findAllGroupByBankYear();
}
