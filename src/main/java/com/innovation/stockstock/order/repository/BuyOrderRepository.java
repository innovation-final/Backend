package com.innovation.stockstock.order.repository;

import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.order.domain.BuyOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BuyOrderRepository extends JpaRepository<BuyOrder, Long> {
    @Query("select sum(b.buyPrice*b.buyAmount) from BuyOrder b where b.stockHolding = :stockHolding")
    Long sumBuyPrice(StockHolding stockHolding);
}
