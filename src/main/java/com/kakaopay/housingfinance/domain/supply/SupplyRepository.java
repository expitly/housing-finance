package com.kakaopay.housingfinance.domain.supply;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplyRepository extends JpaRepository<Supply, Long>, SupplyRepositoryCustom {
}
