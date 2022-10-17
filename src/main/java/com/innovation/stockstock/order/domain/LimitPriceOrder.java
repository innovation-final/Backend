package com.innovation.stockstock.order.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.innovation.stockstock.account.domain.Account;
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
public class LimitPriceOrder {

    @Id @GeneratedValue
    private Long id;
    private String stockCode;
    private String category;
    private int amount;
    private int price;

    @CreatedDate
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone = "Asia/Seoul")
    private LocalDateTime orderAt;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
