package com.kakaopay.housingfinance.controller;

import com.kakaopay.housingfinance.dto.supply.GroupByYearBankMaxResponse;
import com.kakaopay.housingfinance.dto.supply.GroupByYearResponse;
import com.kakaopay.housingfinance.service.SupplyDataService;
import com.kakaopay.housingfinance.service.SupplyCsvUploadService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/supplies")
@PreAuthorize("hasRole('ROLE_API')")
@RequiredArgsConstructor
public class SupplyController {

    private final SupplyDataService supplyDataService;
    private final SupplyCsvUploadService supplyCsvUploadService;

    @PostMapping("/csv")
    public void saveCsv(@RequestParam("file") MultipartFile file, @RequestParam(required = false, defaultValue = "x-windows-949") String charsetName) {
        checkFileEmpty(file);
        checkCsvFileExtention(file);

        try (
                InputStreamReader streamReader = new InputStreamReader(file.getInputStream(), charsetName);
                CSVReader reader = new CSVReaderBuilder(streamReader).build()
        ) {

            Iterator<String[]> csvDataIterator = reader.iterator();
            supplyCsvUploadService.parseAndSave(csvDataIterator);

        } catch(Exception e) {
            log.error(e.getMessage(), e);
            throw new IllegalArgumentException("csv file upload fail");
        }
    }

    private void checkFileEmpty(MultipartFile file) {
        if(file.isEmpty()) throw new IllegalArgumentException("file is empty");
    }

    private void checkCsvFileExtention(MultipartFile file) {
        if(!FilenameUtils.getExtension(file.getOriginalFilename()).toUpperCase().equals("CSV")){
            throw new IllegalArgumentException("You must csv file.");
        }
    }

    @GetMapping("/groups/year")
    public List<GroupByYearResponse> findAllGroupByYear() {
        return supplyDataService.findAllGroupByYear();
    }

    @GetMapping("/groups/year-bank/max")
    public GroupByYearBankMaxResponse getMaxGroupByYearBank() {
        return supplyDataService.getMaxGroupByYearBank();
    }
}
