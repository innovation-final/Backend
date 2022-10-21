package com.innovation.stockstock.stock.like;

import com.innovation.stockstock.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikeStock {
    @Id @GeneratedValue
    @Column(name = "like_stock_id")
    private Long id;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String stockId;
    private String stockName; // 이거 추가했는데 괜찮은가..

    private int buyLimitPrice;
    private int sellLimitPrice;

    public LikeStock(Member member, String stockId, String stockName) {
        this.member = member;
        this.stockId = stockId;
        this.stockName = stockName;
    }
}
