package com.kakaopay.housingfinance.controller;

import com.kakaopay.housingfinance.dto.supply.GroupByYearBankMaxResponse;
import com.kakaopay.housingfinance.dto.supply.GroupByYearResponse;
import com.kakaopay.housingfinance.service.SupplyDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SupplyControllerTest {
    private MockMvc mvc;

    @MockBean
    private SupplyDataService supplyDataService;

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
    @WithMockUser(roles = "API")
    public void CSV파일_저장시_응답_확인() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "data.csv", null, "dummy".getBytes());

        mvc.perform(MockMvcRequestBuilders.multipart("/api/v1/supplies/csv")
                .file(mockFile))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "API")
    public void 연도별_리스트_호출시_JSON응답_확인() throws Exception {
        Map<String, Long> list2015 = new HashMap();
        list2015.put("은행명1", 30L);
        list2015.put("은행명2", 70L);

        Map<String, Long> list2016 = new HashMap();
        list2016.put("은행명1", 20L);
        list2016.put("은행명2", 20L);

        given(supplyDataService.findAllGroupByYear()).willReturn(Arrays.asList(
                new GroupByYearResponse(2015, 100L, list2015),
                new GroupByYearResponse(2016, 40L, list2016)
        ));

        mvc.perform(get("/api/v1/supplies/groups/year"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"year\":2015,\"total_amount\":100,\"detail_amount\":{\"은행명2\":70,\"은행명1\":30}},{\"year\":2016,\"total_amount\":40,\"detail_amount\":{\"은행명2\":20,\"은행명1\":20}}]"));
    }

    @Test
    @WithMockUser(roles = "API")
    public void 연도별_최대_최소_공급량_호출시_JSON응답_확인() throws Exception {
        given(supplyDataService.getMaxGroupByYearBank()).willReturn(
                new GroupByYearBankMaxResponse("한국은행", 2013)
        );

        mvc.perform(get("/api/v1/supplies/groups/year-bank/max"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"year\":2013},\"bank\":\"한국은행\""));
    }
}

