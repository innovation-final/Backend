package com.innovation.stockstock.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockHolding {

    @Id
    @GeneratedValue
    @Column(name = "stockholding_id")
    private Long id;
    private String stockCode;
    private float targetReturnRate;
    private float returnRate;
    private Long profit;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
