package com.innovation.stockstock.order.domain;

import com.innovation.stockstock.account.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LimitPriceOrder {

    @Id @GeneratedValue
    private Long id;
    private String stockCode;
    private String category;
    private int buyAmount;
    private int buyPrice;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
