package com.kakaopay.housingfinance.service;

import com.kakaopay.housingfinance.domain.bank.Bank;
import com.kakaopay.housingfinance.domain.bank.BankRepository;
import com.kakaopay.housingfinance.domain.supply.Supply;
import com.kakaopay.housingfinance.domain.supply.SupplyRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SupplyCsvUploadServiceTest {
    @Autowired
    private SupplyCsvUploadService supplyCsvUploadService;

    @Autowired
    private SupplyRepository supplyRepository;
    @Autowired
    private BankRepository bankRepository;

    @Test
    @Transactional
    public void CSV_데이터_한_줄_저장_후_조회(){
        //given
        List<Bank> banks = bankRepository.findAll();
        String[] data = {"2013", "5", "1,300", "200", "100", "200", "300", "400", "500", "1200", "300"};

        //when
        supplyCsvUploadService.saveOneRow(banks, data);

        //then
        List<Supply> list = supplyRepository.findAll(Sort.by("amount"));
        assertThat(list.size(), is(9));
        assertThat(list.get(0).getYear(), is(2013));
        assertThat(list.get(0).getMonth(), is(5));
        assertThat(list.get(0).getAmount(), is(100L));
    }

    @Test
    @Transactional
    public void csv_전체데이터_저장_확인(){
        //given
        String[] headers = {"년도","월","주택도시기금","국민은행","우리은행","신한은행","한국시티은행","하나은행","농협은행/수협은행","외환은행","기타은행"};
        String[] data2013 = {"2013", "5", "1,300", "50", "100", "200", "300", "400", "500", "1200", "300"};
        String[] data2014 = {"2014", "5", "1,300", "200", "100", "200", "300", "400", "500", "1200", "300"};
        Iterator iterator = Arrays.asList(headers, data2013, data2014).iterator();

        //when
        supplyCsvUploadService.parseAndSave(iterator);

        //then
        List<Supply> list = supplyRepository.findAll(Sort.by("year", "amount"));
        assertThat(list.size(), is(18));
        assertThat(list.get(0).getYear(), is(2013));
        assertThat(list.get(0).getMonth(), is(5));
        assertThat(list.get(0).getAmount(), is(50L));
    }

}
