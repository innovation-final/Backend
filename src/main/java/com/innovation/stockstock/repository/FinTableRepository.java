package com.innovation.stockstock.repository;

import com.innovation.stockstock.document.FinTable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FinTableRepository extends MongoRepository<FinTable, String> {
    FinTable findByCode(String stockCode);
}
