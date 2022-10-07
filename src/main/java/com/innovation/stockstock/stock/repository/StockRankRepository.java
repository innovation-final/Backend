package com.innovation.stockstock.stock.repository;

import com.innovation.stockstock.stock.document.StockRank;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockRankRepository extends MongoRepository<StockRank, String> {
    StockRank findByCriteria(String criteria);
}
