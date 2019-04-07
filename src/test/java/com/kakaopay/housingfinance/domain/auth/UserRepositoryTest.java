package com.kakaopay.housingfinance.domain.auth;

import com.kakaopay.housingfinance.domain.bank.Bank;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Test
    @Transactional
    public void 유저_권한_저장후_이름으로_조회() {
        //given
        Authority authority = authorityRepository.save(new Authority(AuthorityName.ROLE_API));
        userRepository.save(User.builder()
                .username("개나리")
                .password("1234")
                .authorities(Arrays.asList(authority))
                .build());

        //when
        User user = userRepository.findByUsername("개나리").get();

        //then
        assertThat(user.getUsername(), is("개나리"));
    }
}
