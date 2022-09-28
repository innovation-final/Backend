package com.innovation.stockstock.repository;

import com.innovation.stockstock.entity.StockRank;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockRankRepository extends MongoRepository<StockRank, String> {
    StockRank findByCriteria(String criteria);
}
