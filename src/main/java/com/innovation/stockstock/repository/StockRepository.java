package com.innovation.stockstock.repository;

import com.innovation.stockstock.document.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockRepository extends MongoRepository<Stock,String> {
    Stock findByCode(String stockCode);
}
