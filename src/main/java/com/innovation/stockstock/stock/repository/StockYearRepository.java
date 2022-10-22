package com.innovation.stockstock.stock.repository;

import com.innovation.stockstock.stock.document.StockYear;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockYearRepository extends MongoRepository<StockYear, String> {
    StockYear findByCode(String stockCode);
}
