package com.kakaopay.housingfinance.domain.bank;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long>{
    Optional<Bank> findByName(String name);

    Optional<Bank> findByCode(String code);
}
