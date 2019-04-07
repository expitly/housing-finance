package com.kakaopay.housingfinance.domain.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AuthorityRepositoryTest {

    @Autowired
    AuthorityRepository authorityRepository;

    @Test
    public void 권한_조회() {
        //when
        Authority authority = authorityRepository.findByName(AuthorityName.ROLE_API).get();

        //then
        assertThat(authority.getName(), is(AuthorityName.ROLE_API));
    }
}
