package com.kakaopay.housingfinance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.housingfinance.dto.auth.SignInRequest;
import com.kakaopay.housingfinance.dto.auth.SignUpRequest;
import com.kakaopay.housingfinance.security.CustomUserDetails;
import com.kakaopay.housingfinance.security.CustomUserDetailsService;
import com.kakaopay.housingfinance.security.JwtProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    ObjectMapper objectMapper;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithAnonymousUser
    public void 권한_없이_가입_요청시_토큰_발행_확인() throws Exception {
        SignUpRequest request = new SignUpRequest("개나리","1234");

        given(jwtProvider.generateToken(any())).willReturn("generatedTokenString");

        mvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"generatedTokenString\"}"));
    }

    @Test
    @WithAnonymousUser
    public void 권한_없이_로그인_요청시_토큰_발행_확인() throws Exception {
        SignInRequest request = new SignInRequest("개나리","1234");

        given(jwtProvider.generateToken(any())).willReturn("generatedTokenString");
        given(customUserDetailsService.loadUserByUsername("aaaa")).willReturn(new CustomUserDetails(1L,"개나리","1234", null));

        mvc.perform(post("/api/v1/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"generatedTokenString\"}"));
    }

    @Test
    @WithAnonymousUser
    public void 권한없는_유저가_refresh토큰_API_접근시_Unauthorized확인() throws Exception {
        mvc.perform(get("/api/v1/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }
}
