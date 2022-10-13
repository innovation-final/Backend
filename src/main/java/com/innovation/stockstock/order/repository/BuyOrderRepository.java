package com.innovation.stockstock.order.repository;

import com.innovation.stockstock.order.domain.BuyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyOrderRepository extends JpaRepository<BuyOrder, Long> {
}
