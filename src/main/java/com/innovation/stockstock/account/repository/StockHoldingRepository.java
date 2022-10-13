package com.innovation.stockstock.account.repository;

import com.innovation.stockstock.account.domain.StockHolding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHoldingRepository extends JpaRepository<StockHolding,Long> {
    StockHolding findByStockCodeAndAccountId(String stockCode, Long accountId);
}
