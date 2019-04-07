package com.kakaopay.housingfinance.domain.supply;

import com.kakaopay.housingfinance.domain.bank.Bank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(indexes = {
        @Index(name="IDX_SUPPLY_UNOQUE_01", columnList = "year, month, bank_id", unique = true)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Supply {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 4, nullable = false)
    private Integer year;

    @Column(length = 2, nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Long amount;

    @ManyToOne
    @JoinColumn(name = "bank_id", referencedColumnName = "id", nullable = false)
    private Bank bank;

    @Builder
    public Supply(Integer year, Integer month, Long amount, Bank bank){
        this.year = year;
        this.month = month;
        this.amount = amount;
        this.bank = bank;
    }

}