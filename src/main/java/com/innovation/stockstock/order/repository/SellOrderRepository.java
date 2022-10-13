package com.innovation.stockstock.order.repository;

import com.innovation.stockstock.order.domain.SellOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellOrderRepository extends JpaRepository<SellOrder, Long> {
}
