package com.innovation.stockstock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class StockStockApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockStockApplication.class, args);
    }

}
