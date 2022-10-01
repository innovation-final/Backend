package com.innovation.stockstock.repository;

import com.innovation.stockstock.document.News;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewsRepository extends MongoRepository<News, String> {
    News findByCode(String stockCode);
}
