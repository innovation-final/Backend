package com.innovation.stockstock.stock.repository;

import com.innovation.stockstock.stock.document.StockList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockListRepository extends MongoRepository<StockList, String> {
    StockList findByCode(String code);
    List<StockList> findAll();
}
