package com.kakaopay.housingfinance.controller;

import com.kakaopay.housingfinance.dto.bank.BanksResponse;
import com.kakaopay.housingfinance.dto.bank.MinMaxGroupByYearResponse;
import com.kakaopay.housingfinance.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banks")
@PreAuthorize("hasRole('ROLE_API')")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @GetMapping
    public List<BanksResponse> getBanks() {
        return bankService.findAll();
    }

    @GetMapping(value="/{code}/supplies/avg/min-max")
    public MinMaxGroupByYearResponse getAvgMinMax(@PathVariable String code) {
        return bankService.getAvgMinMaxGroupByYear(code);
    }

}
