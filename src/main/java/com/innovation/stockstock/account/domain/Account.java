package com.innovation.stockstock.account.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.innovation.stockstock.common.dto.Timestamped;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.order.domain.BuyOrder;
import com.innovation.stockstock.order.domain.LimitPriceOrder;
import com.innovation.stockstock.order.domain.SellOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account extends Timestamped {

    @Id
    @GeneratedValue
    @Column(name = "account_id")
    private Long id;

    private Long accountNumber;

    private int seedMoney;

    private Long balance;

    private float targetReturnRate;

    private float totalReturnRate;

    private Long totalProfit;

    private Long totalRealizedProfit;

    private Long totalUnrealizedProfit;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Seoul")
    private LocalDateTime expireAt;

    @JoinColumn(name = "member_id", nullable = false)
    @OneToOne
    private Member member;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockHolding> stockHoldingsList = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuyOrder> buyOrders = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SellOrder> sellOrders = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LimitPriceOrder> limitPriceOrders = new ArrayList<>();

    public void updateBalance(boolean isBuying, int price) {
        if (isBuying) {
            this.balance -= price;
        } else {
            this.balance += price;
        }
    }
    public void setTotalReturnRate(float totalReturnRate){
        this.totalReturnRate=totalReturnRate;
    }
    public void setTotalProfit(Long totalProfit){
        this.totalProfit=totalProfit;
    }
    public void setTotalUnrealizedProfit(Long totalUnrealizedProfit){
        this.totalUnrealizedProfit=totalUnrealizedProfit;
    }
    public void updateTotalRealizedProfit(Long totalRealizedProfit) {
        this.totalRealizedProfit += totalRealizedProfit;
    }
    public void setTotalRealizedProfit(Long totalRealizedProfit){
        this.totalRealizedProfit=totalRealizedProfit;
    }
    public void updateExpiredAt(int days) {
        this.expireAt = this.expireAt.plusDays(days);
    }
}
