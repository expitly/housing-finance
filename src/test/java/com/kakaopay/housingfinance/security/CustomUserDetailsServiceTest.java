package com.kakaopay.housingfinance.security;

import com.kakaopay.housingfinance.domain.auth.User;
import com.kakaopay.housingfinance.domain.auth.UserRepository;
import com.kakaopay.housingfinance.dto.auth.SignUpRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomUserDetailsServiceTest {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp(){
        userRepository.deleteAll();
    }

    @Test(expected = RuntimeException.class)
    @Transactional
    public void 유저이름_중복시_오류발생_확인() {
        //when
        customUserDetailsService.signUp(new SignUpRequest("개나리", "1234"));
        customUserDetailsService.signUp(new SignUpRequest("개나리", "1234"));
    }

    @Test
    @Transactional
    public void 유저_등록_후_이름으로_조회_확인() {
        //when
        customUserDetailsService.signUp(new SignUpRequest("유저", "비번"));
        User user = userRepository.findByUsername("유저").get();

        //then
        assertThat(user.getUsername(), is("유저"));
    }
}
