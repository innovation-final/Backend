package com.innovation.stockstock.account.repository;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockHoldingRepository extends JpaRepository<StockHolding,Long> {
    StockHolding findByStockCodeAndAccountId(String stockCode, Long accountId);
    void deleteById(Long id);
    List<StockHolding> findByAccount(Account account);
    @Query("select sum(s.amount*s.avgBuying) from StockHolding s where s.stockCode = :stockCode")
    Long sumHoldingBuyPrice(String stockCode);
}
