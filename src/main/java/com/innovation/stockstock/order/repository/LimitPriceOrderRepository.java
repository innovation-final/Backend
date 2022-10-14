package com.innovation.stockstock.order.repository;

import com.innovation.stockstock.order.domain.LimitPriceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LimitPriceOrderRepository extends JpaRepository<LimitPriceOrder, Long> {
}
