package com.kakaopay.housingfinance.domain.bank;

import com.kakaopay.housingfinance.domain.supply.Supply;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bank {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String name;

    @Column(length = 10, nullable = false, unique = true)
    private String code;

    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL)
    private List<Supply> supplyList;

    @Builder
    public Bank(String name, String code){
        this.name = name;
        this.code = code;
    }
}