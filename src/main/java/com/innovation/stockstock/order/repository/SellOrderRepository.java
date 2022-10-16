package com.innovation.stockstock.order.repository;

import com.innovation.stockstock.order.domain.SellOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellOrderRepository extends JpaRepository<SellOrder, Long> {
    List<SellOrder> findAllByAccountId(Long id);
}
