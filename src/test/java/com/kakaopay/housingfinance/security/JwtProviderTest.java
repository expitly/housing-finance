package com.kakaopay.housingfinance.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClock;
import org.assertj.core.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtProviderTest {
    @InjectMocks
    private JwtProvider jwtProvider;

    @Mock
    private Clock clockMock;

    @Before
    public void init() {
        ReflectionTestUtils.setField(jwtProvider, "jwtSecret", "kakao");
        ReflectionTestUtils.setField(jwtProvider, "jwtExpiration", 60000); //60초
    }

    @Test
    public void JWT_생성_확인(){
        given(clockMock.now()).willReturn(DateUtil.now());
        CustomUserDetails user = CustomUserDetails.builder()
                                    .id(1L)
                                    .username("nana")
                                    .password("1234")
                                    .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_API")))
                                    .build();


        String token = jwtProvider.generateToken(user);

        assertThat(token.split("\\.").length, is(3));
    }

    @Test
    public void JWT_클레임_데이터_추출_확인_비밀번호는_저장안함(){
        given(clockMock.now()).willReturn(DateUtil.now());
        CustomUserDetails user = CustomUserDetails.builder()
                                    .id(1L)
                                    .username("nana")
                                    .password("1234")
                                    .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_API")))
                                    .build();

        String token = jwtProvider.generateToken(user);
        Jws<Claims> claims = jwtProvider.getAllClaims(token);

        assertThat(claims.getBody().get("id"), is(1));
        assertThat(claims.getBody().get("username"), is("nana"));
        assertNull(claims.getBody().get("password"));
    }

    @Test(expected = SignatureException.class)
    public void JWT_싸인_변조_예외발생_확인(){
        given(clockMock.now()).willReturn(DateUtil.now());
        CustomUserDetails user = CustomUserDetails.builder()
                .id(1L)
                .username("nana")
                .password("1234")
                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_API")))
                .build();

        String token = jwtProvider.generateToken(user) + "aa";
        when(clockMock.now())
                .thenReturn(DateUtil.yesterday());

        jwtProvider.getAllClaims(token);
    }

    @Test(expected = ExpiredJwtException.class)
    public void 유효기간_지나면_예외발생(){
        given(clockMock.now()).willReturn(DateUtil.yesterday());

        CustomUserDetails user = CustomUserDetails.builder()
                .id(1L)
                .username("nana")
                .password("1234")
                .authorities(Arrays.asList(new SimpleGrantedAuthority("ROLE_API")))
                .build();

        String token = jwtProvider.generateToken(user);

        jwtProvider.getAllClaims(token);
    }
}
