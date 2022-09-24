package com.innovation.stockstock.repository;

import com.innovation.stockstock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock,Long> {
}
