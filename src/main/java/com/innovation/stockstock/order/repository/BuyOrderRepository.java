package com.innovation.stockstock.order.repository;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.order.domain.BuyOrder;
import com.innovation.stockstock.stock.document.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface BuyOrderRepository extends JpaRepository<BuyOrder, Long> {
      List<BuyOrder> findAllByAccountIdAndBuyAtBetween(Long id, LocalDateTime startDate, LocalDateTime endDate);
}
