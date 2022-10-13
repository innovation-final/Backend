package com.innovation.stockstock.order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class SellOrder {

    @Id
    @GeneratedValue
    private Long id;

    private String orderCategory;
    @CreatedDate
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Seoul")
    private LocalDateTime sellAt;
    private int sellAmount;
    private int sellPrice;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "stockholding_id")
    private StockHolding stockHolding;

    public SellOrder(String orderCategory, int sellAmount, int sellPrice) {
        this.orderCategory = orderCategory;
        this.sellAmount = sellAmount;
        this.sellPrice = sellPrice;
    }
}