package com.kakaopay.housingfinance.service;

import com.kakaopay.housingfinance.common.type.AggregationType;
import com.kakaopay.housingfinance.domain.bank.Bank;
import com.kakaopay.housingfinance.domain.bank.BankRepository;
import com.kakaopay.housingfinance.domain.supply.SupplyRepository;
import com.kakaopay.housingfinance.dto.bank.*;
import com.kakaopay.housingfinance.dto.bank.MinMaxGroupByYearResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class BankService {

    private final BankRepository bankRepository;
    private final SupplyRepository supplyRepository;

    @Transactional(readOnly = true)
    public List<BanksResponse> findAll() {
        return bankRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(BanksResponse::new)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public MinMaxGroupByYearResponse getAvgMinMaxGroupByYear(String bankCode){
        Bank bank = bankRepository.findByCode(bankCode).orElseThrow(IllegalArgumentException::new);

        return MinMaxGroupByYearResponse.builder()
                .bank(bank.getName())
                .supportAmount(
                        Arrays.asList(
                                supplyRepository.getAvgGroupByYear(bank, AggregationType.MIN),
                                supplyRepository.getAvgGroupByYear(bank, AggregationType.MAX)
                        ))
                .build();
    }

    @Transactional(readOnly = true)
    public List<Bank> getBanksFromNames(List<String> bankNames) {

        return bankNames.stream()
                .filter(StringUtils::isNotEmpty)
                .map((name) -> bankRepository.findByName(name).orElseThrow(IllegalArgumentException::new))
                .collect(toList());

    }
}
