package com.kakaopay.housingfinance.domain.bank;

import com.kakaopay.housingfinance.domain.auth.User;
import com.kakaopay.housingfinance.domain.auth.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BankRepositoryTest {

    @Autowired
    BankRepository bankRepository;

    @Test
    @Transactional
    public void 은행_저장_이름으로_불러오기() {
        //given
        bankRepository.save(Bank.builder()
                .name("마이은행")
                .code("bank1234")
                .build());

        //when
        Bank bank = bankRepository.findByName("마이은행").get();

        //then
        assertThat(bank.getName(), is("마이은행"));
    }
}
