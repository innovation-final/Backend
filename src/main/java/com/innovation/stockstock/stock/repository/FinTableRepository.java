package com.innovation.stockstock.stock.repository;

import com.innovation.stockstock.stock.document.FinTable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FinTableRepository extends MongoRepository<FinTable, String> {
    FinTable findByCode(String stockCode);
}
