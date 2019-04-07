package com.kakaopay.housingfinance.service;

import com.kakaopay.housingfinance.domain.supply.SupplyRepository;
import com.kakaopay.housingfinance.dto.supply.GroupByYearBank;
import com.kakaopay.housingfinance.dto.supply.GroupByYearResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SupplyDataServiceTest {
    @InjectMocks
    private SupplyDataService supplyDataService;

    @Mock
    SupplyRepository supplyRepository;

    @Test
    public void 은행_연도별_전체_데이터_조회시_DTO_확인(){
        //given
        given(supplyRepository.findAllGroupByBankYear()).willReturn(
                Arrays.asList(
                        new GroupByYearBank(2010, "A은행", 1200L),
                        new GroupByYearBank(2010, "B은행", 100L),
                        new GroupByYearBank(2011, "A은행", 300L),
                        new GroupByYearBank(2011, "B은행", 1500L)
                )
        );

        //when
        List<GroupByYearResponse> result = supplyDataService.findAllGroupByYear();

        //then
        assertThat(result.get(0).getYear(), is(2010));
        assertThat(result.get(0).getTotalAmount(), is(1300L));
        assertThat(result.get(0).getDetailAmount().size(), is(2));
        assertThat(result.get(1).getYear(), is(2011));
        assertThat(result.get(1).getTotalAmount(), is(1800L));
        assertThat(result.get(1).getDetailAmount().size(), is(2));
    }
}
