package com.innovation.stockstock.order.repository;

import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.order.domain.BuyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface BuyOrderRepository extends JpaRepository<BuyOrder, Long> {
    @Query("select sum(b.buyPrice*b.buyAmount) from BuyOrder b where b.stockHolding = :stockHolding")
    Long sumBuyPrice(StockHolding stockHolding);
    @Query("select sum(b.buyAmount) from BuyOrder b where b.stockHolding = :stockHolding")
    int sumBuyAmount(StockHolding stockHolding);
    List<BuyOrder> findAllByAccountIdAndBuyAtBetween(Long id, LocalDateTime startDate, LocalDateTime endDate);

}
