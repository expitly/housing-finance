package com.kakaopay.housingfinance.service;

import com.kakaopay.housingfinance.common.type.AggregationType;
import com.kakaopay.housingfinance.domain.bank.Bank;
import com.kakaopay.housingfinance.domain.bank.BankRepository;
import com.kakaopay.housingfinance.domain.supply.SupplyRepository;
import com.kakaopay.housingfinance.dto.bank.AvgAmountPerYear;
import com.kakaopay.housingfinance.dto.bank.BanksResponse;
import com.kakaopay.housingfinance.dto.bank.MinMaxGroupByYearResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankServiceTest {

    @InjectMocks
    private BankService bankService;

    @Mock
    BankRepository bankRepository;

    @Mock
    SupplyRepository supplyRepository;

    @Test(expected = IllegalArgumentException.class)
    public void 없는_코드로_연도별_최대_최소_공급량_조회시_예외발생(){
       //when
        bankService.getAvgMinMaxGroupByYear("존재하지않아요");
    }

    @Test
    public void 연도별_최대_최소_공급량_조회시_DTO_확인(){
        //given
        String bankName = "카카오";
        String bankCode = "kakao";
        Bank bank = new Bank(bankName, bankCode);
        given(bankRepository.findByCode(bankCode)).willReturn(Optional.of(bank));
        given(supplyRepository.getAvgGroupByYear(bank, AggregationType.MIN)).willReturn(new AvgAmountPerYear(2015, 30L));
        given(supplyRepository.getAvgGroupByYear(bank, AggregationType.MAX)).willReturn(new AvgAmountPerYear(2017, 1250L));

        //when
        MinMaxGroupByYearResponse result = bankService.getAvgMinMaxGroupByYear(bankCode);

        //then
        assertThat(result.getBank(), is(bankName));
        assertThat(result.getSupportAmount().get(0).getYear(), is(2015));
        assertThat(result.getSupportAmount().get(0).getAmount(), is(30L));
        assertThat(result.getSupportAmount().get(1).getYear(), is(2017));
        assertThat(result.getSupportAmount().get(1).getAmount(), is(1250L));
    }
}
