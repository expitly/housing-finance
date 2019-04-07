package com.kakaopay.housingfinance.service;

import com.kakaopay.housingfinance.domain.bank.Bank;
import com.kakaopay.housingfinance.domain.bank.BankRepository;
import com.kakaopay.housingfinance.domain.supply.Supply;
import com.kakaopay.housingfinance.domain.supply.SupplyRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class SupplyCsvUploadService {

    private final BankService bankService;
    private final SupplyRepository supplyRepository;

    final static int YEAR_ELEMENT_INDEX = 0;
    final static int MONTH_ELEMENT_INDEX = 1;
    final static int BANK_ELEMENT_START_INDEX = 2;
    final static int BANK_ELEMENT_COUNT = 11;
    final static int PEREOD_ELEMENT_COUNT = 2;

    @Transactional
    public void parseAndSave(Iterator<String[]> csvDataIterator) {
        supplyRepository.deleteAllInBatch();

        String[] headers = csvDataIterator.next();
        List<Bank> banks = bankService.getBanksFromNames(parseBankNames(headers));

        while (csvDataIterator.hasNext()) {
            saveOneRow(banks, csvDataIterator.next());
        }
    }

    private List<String> parseBankNames(String[] headers){
        return Arrays.stream(headers)
                .limit(BANK_ELEMENT_COUNT)
                .skip(PEREOD_ELEMENT_COUNT)
                .map((bankName) -> bankName.replaceAll("\\(억원\\)", "").replaceAll("1\\)", "").trim())
                .collect(toList());
    }

    @Transactional
    public void saveOneRow(List<Bank> banks, String[] elements) {
        int year = Integer.parseInt(elements[YEAR_ELEMENT_INDEX]);
        int month = Integer.parseInt(elements[MONTH_ELEMENT_INDEX]);

        List<Supply> supplies = new ArrayList<>();

        IntStream.range(BANK_ELEMENT_START_INDEX, BANK_ELEMENT_COUNT)
                .forEach((index) -> supplies.add(Supply.builder()
                                        .year( year )
                                        .month( month )
                                        .amount( Long.parseLong(elements[index].trim().replaceAll(",", "")) )
                                        .bank( banks.get( index-PEREOD_ELEMENT_COUNT ) )
                                        .build()
                ));

        supplyRepository.saveAll(supplies);
    }
}
