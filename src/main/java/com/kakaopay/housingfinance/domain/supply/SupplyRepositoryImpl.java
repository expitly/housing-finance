package com.kakaopay.housingfinance.domain.supply;

import com.kakaopay.housingfinance.common.type.AggregationType;
import com.kakaopay.housingfinance.domain.bank.Bank;
import com.kakaopay.housingfinance.dto.bank.AvgAmountPerYear;
import com.kakaopay.housingfinance.dto.supply.GroupByYearBank;
import com.kakaopay.housingfinance.dto.supply.GroupByYearBankMaxResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SupplyRepositoryImpl implements SupplyRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public GroupByYearBankMaxResponse getMaxGroupByYearBank() {
        QSupply supply = QSupply.supply;

        return queryFactory
                .select(Projections.fields(GroupByYearBankMaxResponse.class,
                        supply.bank.name.as("bankName"),
                        supply.year.as("year")
                ))
                .from(supply)
                .groupBy(supply.bank, supply.year)
                .orderBy(supply.amount.sum().desc())
                .limit(1)
                .fetchFirst();
    }

    public AvgAmountPerYear getAvgGroupByYear(Bank bank, AggregationType type) {
        QSupply supply = QSupply.supply;

        NumberPath<Long> aliasQuantity = Expressions.numberPath(Long.class, "amount");

        OrderSpecifier order = null;
        if(type.equals(AggregationType.MAX)){
            order = aliasQuantity.desc();
        }
        if(type.equals(AggregationType.MIN)){
            order = aliasQuantity.asc();
        }

        return queryFactory
                .select(Projections.fields(AvgAmountPerYear.class,
                        supply.year.as("year"),
                        supply.amount.sum().divide(supply.id.count()).round().as(aliasQuantity)
                ))
                .from(supply)
                .where(supply.bank.eq(bank))
                .groupBy(supply.year)
                .having(supply.id.count().eq(12L))
                .orderBy(order)
                .limit(1)
                .fetchFirst();
    }

    public List<GroupByYearBank> findAllGroupByBankYear(){
        QSupply supply = QSupply.supply;

        return queryFactory
                .select(Projections.fields(GroupByYearBank.class,
                        supply.year.as("year"),
                        supply.bank.name.as("name"),
                        supply.amount.sum().as("amount")
                ))
                .from(supply)
                .groupBy(supply.bank, supply.year)
                .orderBy(supply.year.asc())
                .fetch();
    }
}
