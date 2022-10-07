package com.innovation.stockstock.stock.repository;

import com.innovation.stockstock.stock.document.News;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewsRepository extends MongoRepository<News, String> {
    News findByCode(String stockCode);
}
