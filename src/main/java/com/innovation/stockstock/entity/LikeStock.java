package com.innovation.stockstock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class LikeStock {
    @Id @GeneratedValue
    @Column(name = "like_stock_id")
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String stockId;

    public LikeStock(Member member, String stockId) {
        this.member = member;
        this.stockId = stockId;
    }
}
