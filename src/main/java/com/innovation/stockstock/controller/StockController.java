package com.innovation.stockstock.controller;

import com.innovation.stockstock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/api/stock/{stockCode}")
    public ResponseEntity<?> getStock(@PathVariable String stockCode) {
        return stockService.getStock(stockCode);
    }

    @GetMapping("/api/stock/news/{stockCode}")
    public ResponseEntity<?> getStockNews(@PathVariable String stockCode) {
        return stockService.getStockNews(stockCode);
    }

    @GetMapping("/api/stock/table/{stockCode}")
    public ResponseEntity<?> getStockTable(@PathVariable String stockCode) {
        return stockService.getStockTable(stockCode);
    }

    @GetMapping("/api/stock/rank/{criteria}")
    public ResponseEntity<?> getRank(@PathVariable String criteria) {
        return ResponseEntity.ok().body(stockService.getRank(criteria));
    }

    @GetMapping("/api/stock/index/{name}")
    public ResponseEntity<?> getIndex(@PathVariable String name) {
        return ResponseEntity.ok().body(stockService.getIndex(name));
    }

    @GetMapping("/api/stock/list")
    public ResponseEntity<?> getList() {
        return ResponseEntity.ok().body(stockService.getList());
    }

}
