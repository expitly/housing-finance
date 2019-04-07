package com.kakaopay.housingfinance.domain.supply;

import com.kakaopay.housingfinance.common.type.AggregationType;
import com.kakaopay.housingfinance.domain.bank.Bank;
import com.kakaopay.housingfinance.domain.bank.BankRepository;
import com.kakaopay.housingfinance.dto.bank.AvgAmountPerYear;
import com.kakaopay.housingfinance.dto.supply.GroupByYearBank;
import com.kakaopay.housingfinance.dto.supply.GroupByYearBankMaxResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SupplyRepositoryTest {

    @Autowired
    SupplyRepository supplyRepository;

    @Autowired
    BankRepository bankRepository;

    @Test
    @Transactional
    public void 은행_공급량_저장하고_불러오기() {
        //given
        Bank bank = bankRepository.save(Bank.builder()
                .name("마이은행")
                .code("bank1234")
                .build());
        supplyRepository.save(Supply.builder()
                .bank(bank)
                .year(2019)
                .month(4)
                .amount(1200L)
                .build());

        //when
        Supply supply = supplyRepository.findAll().get(0);

        //then
        assertThat(supply.getYear(), is(2019));
        assertThat(supply.getMonth(), is(4));
        assertThat(supply.getAmount(), is(1200L));
    }
    
    @Test
    @Transactional
    public void 연도_은행별_최대공급량(){
        //given
        Bank bank1 = bankRepository.save(new Bank("은행1", "code1"));
        Bank bank2 = bankRepository.save(new Bank("은행2", "code2"));
        supplyRepository.saveAll(
                Arrays.asList(
                        new Supply(2015, 1, 1000L, bank1),
                        new Supply(2016, 2, 30L, bank1),
                        new Supply(2015, 3, 1200L, bank2),
                        new Supply(2016, 4, 4000L, bank2)
                )
        );
        
        //when
        GroupByYearBankMaxResponse result = supplyRepository.getMaxGroupByYearBank();

        //then
        assertThat(result.getBankName(), is("은행2"));
        assertThat(result.getYear(), is(2016));
    }

    @Test
    @Transactional
    public void 특정은행의_연도별_평균_최대_공급량(){
        //given
        Bank bank = bankRepository.save(new Bank("은행1", "code1"));
        List<Supply> list = new ArrayList<Supply>();
        for(int i = 1; i <= 12; i++){
            list.add( new Supply(2015, i, 100L, bank) );
            list.add( new Supply(2016, i, 200L, bank) );
        }
        supplyRepository.saveAll(list);

        //when
        AvgAmountPerYear result = supplyRepository.getAvgGroupByYear(bank, AggregationType.MAX);

        //then
        assertThat(result.getYear(), is(2016));
        assertThat(result.getAmount(), is(200L));
    }

    @Test
    @Transactional
    public void 년도별_은행별_전체데이터_조회(){
        //given
        Bank bank1 = bankRepository.save(new Bank("은행1", "code1"));
        Bank bank2 = bankRepository.save(new Bank("은행2", "code2"));
        supplyRepository.saveAll(
                Arrays.asList(
                        new Supply(2015, 1, 1000L, bank1),
                        new Supply(2015, 2, 30L, bank1),
                        new Supply(2015, 3, 1200L, bank2),
                        new Supply(2016, 3, 1200L, bank2),
                        new Supply(2016, 4, 4000L, bank2),
                        new Supply(2016, 4, 4000L, bank1)
                )
        );

        //when
        List<GroupByYearBank> result = supplyRepository.findAllGroupByBankYear();

        //then
        assertThat(result.size(), is(4));
    }
}
