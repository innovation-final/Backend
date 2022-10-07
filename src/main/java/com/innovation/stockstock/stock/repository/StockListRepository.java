package com.innovation.stockstock.stock.repository;

import com.innovation.stockstock.stock.document.StockList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StockListRepository extends MongoRepository<StockList, String> {
    Optional<StockList> findByCode(String code);
    List<StockList> findAll();
}
