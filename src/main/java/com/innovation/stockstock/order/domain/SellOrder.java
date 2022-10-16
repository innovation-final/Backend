package com.innovation.stockstock.order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
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
}
