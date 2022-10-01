package com.innovation.stockstock.repository;

import com.innovation.stockstock.document.Index;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockIndexRepository extends MongoRepository<Index,String> {
    Index findByName(String name);
}
