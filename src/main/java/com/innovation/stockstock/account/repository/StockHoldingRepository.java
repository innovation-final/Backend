package com.innovation.stockstock.account.repository;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockHoldingRepository extends JpaRepository<StockHolding,Long> {
    StockHolding findByStockCodeAndAccountId(String stockCode, Long accountId);
    void deleteById(Long id);
    List<StockHolding> findByAccount(Account account);
}
