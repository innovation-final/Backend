package com.innovation.stockstock.stock.repository;

import com.innovation.stockstock.stock.document.Index;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockIndexRepository extends MongoRepository<Index,String> {
    Index findByName(String name);
}
