package com.innovation.stockstock.order.repository;

import com.innovation.stockstock.order.domain.BuyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuyOrderRepository extends JpaRepository<BuyOrder, Long> {
    List<BuyOrder> findAllByAccountId(Long id);
}
