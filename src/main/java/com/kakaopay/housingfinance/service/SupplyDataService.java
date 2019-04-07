package com.kakaopay.housingfinance.service;

import com.kakaopay.housingfinance.domain.bank.Bank;
import com.kakaopay.housingfinance.domain.bank.BankRepository;
import com.kakaopay.housingfinance.domain.supply.Supply;
import com.kakaopay.housingfinance.domain.supply.SupplyRepository;
import com.kakaopay.housingfinance.dto.supply.GroupByYearResponse;
import com.kakaopay.housingfinance.dto.supply.GroupByYearBankMaxResponse;
import com.kakaopay.housingfinance.dto.supply.GroupByYearBank;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class SupplyDataService {

    private final SupplyRepository supplyRepository;

    @Transactional(readOnly = true)
    public GroupByYearBankMaxResponse getMaxGroupByYearBank(){
        return supplyRepository.getMaxGroupByYearBank();
    }

    @Transactional(readOnly = true)
    public List<GroupByYearResponse> findAllGroupByYear(){
        List<GroupByYearBank> totalList = supplyRepository.findAllGroupByBankYear();

        return totalList
                .stream()
                .collect(Collectors.groupingBy(GroupByYearBank::getYear, LinkedHashMap::new, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> GroupByYearResponse.builder()
                                .year(entry.getKey())
                                .totalAmount(entry.getValue().stream().mapToLong(GroupByYearBank::getAmount).sum())
                                .detailAmount(entry.getValue().stream().collect(Collectors.toMap(GroupByYearBank::getName, GroupByYearBank::getAmount)))
                                .build())
                .collect(Collectors.toList());
    }

}
