package com.kakaopay.housingfinance.controller;

import com.kakaopay.housingfinance.dto.bank.AvgAmountPerYear;
import com.kakaopay.housingfinance.dto.bank.BanksResponse;
import com.kakaopay.housingfinance.dto.bank.MinMaxGroupByYearResponse;
import com.kakaopay.housingfinance.service.BankService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BankControllerTest {
    private MockMvc mvc;

    @MockBean
    private BankService bankService;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void 권한_없음_테스트() throws Exception{
        mvc.perform(get("/api/v1/banks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "API")
    public void 은행_리스트_호출시_JSON응답_확인() throws Exception {
        given(bankService.findAll()).willReturn(
                Arrays.asList(
                        new BanksResponse("은행명1", "은행코드1"),
                        new BanksResponse("은행명2", "은행코드2")
                )
        );

        mvc.perform(get("/api/v1/banks"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"name\":\"은행명1\",\"code\":\"은행코드1\"},{\"name\":\"은행명2\",\"code\":\"은행코드2\"}]"));
    }

    @Test
    @WithMockUser(roles = "API")
    public void 년도별_최대_최소_공급량_호출시_JSON응답_확인() throws Exception {
        given(bankService.getAvgMinMaxGroupByYear("bank8")).willReturn(
                MinMaxGroupByYearResponse.builder()
                        .bank("은행명")
                        .supportAmount(
                                Arrays.asList(
                                        new AvgAmountPerYear(2010, 300L),
                                        new AvgAmountPerYear(2011, 150L)
                                )
                        )
                        .build()
        );

        mvc.perform(get("/api/v1/banks/bank8/supplies/avg/min-max"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"bank\":\"은행명\",\"support_amount\":[{\"year\":2010,\"amount\":300},{\"year\":2011,\"amount\":150}]}"));
    }
}

