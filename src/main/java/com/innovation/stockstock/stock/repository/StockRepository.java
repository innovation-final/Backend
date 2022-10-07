package com.innovation.stockstock.stock.repository;

import com.innovation.stockstock.stock.document.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockRepository extends MongoRepository<Stock,String> {
    Stock findByCode(String stockCode);
}
