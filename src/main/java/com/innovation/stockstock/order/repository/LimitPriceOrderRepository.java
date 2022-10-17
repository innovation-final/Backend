package com.innovation.stockstock.order.repository;

import com.innovation.stockstock.order.domain.LimitPriceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LimitPriceOrderRepository extends JpaRepository<LimitPriceOrder, Long> {
    List<LimitPriceOrder> findAllByAccountIdAndCategoryAndOrderAtBetween(Long id, String orderCategory, LocalDateTime startDate, LocalDateTime endDate);
}
