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
    private int amount;
    private float returnRate;
    private Long profit;
    private int avgBuying;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public void updateAmount(boolean isBuying, int amount) {
        if (isBuying) {
            this.amount += amount;
        } else {
            this.amount -= amount;
        }
    }
}
