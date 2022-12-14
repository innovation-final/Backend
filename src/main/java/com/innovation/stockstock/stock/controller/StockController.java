package com.innovation.stockstock.stock.controller;

import com.innovation.stockstock.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @GetMapping("/api/stock/{stockCode}")
    public ResponseEntity<?> getStock(@PathVariable String stockCode, HttpServletRequest request) {
        return stockService.getStock(stockCode, request);
    }

    @GetMapping("/api/stock/year/{stockCode}")
    public ResponseEntity<?> getStockYear(@PathVariable String stockCode) {
        return stockService.getStockYear(stockCode);
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
    public ResponseEntity<?> getRank(@PathVariable String criteria, HttpServletRequest request) {
        return ResponseEntity.ok().body(stockService.getRank(criteria, request));
    }

    @GetMapping("/api/stock/index/{name}")
    public ResponseEntity<?> getIndex(@PathVariable String name) {
        return stockService.getIndex(name);
    }

    @GetMapping("/api/stock/index/main/{name}")
    public ResponseEntity<?> getIndexToday(@PathVariable String name) {
        return stockService.getIndexToday(name);
    }

    @GetMapping("/api/auth/stock/like")
    public ResponseEntity<?> getLikeStock() {
        return ResponseEntity.ok().body(stockService.getLikeStock());
    }

    @PostMapping("/api/auth/stock/like/{stockCode}")
    public ResponseEntity<?> likeStock(@PathVariable String stockCode) {
        return ResponseEntity.ok().body(stockService.likeStock(stockCode));
    }

    @DeleteMapping("/api/auth/stock/like/{stockCode}")
    public ResponseEntity<?> cancelLikeStock(@PathVariable String stockCode) {
        return ResponseEntity.ok().body(stockService.cancelLikeStock(stockCode));
    }

    @GetMapping("/api/auth/stock/like/news/{stockCode}")
    public ResponseEntity<?> getLikeStockNews(@PathVariable String stockCode) {
        return stockService.getLikeStockNews(stockCode);
    }

    @GetMapping("/api/stock/list")
    public ResponseEntity<?> getList() {
        return ResponseEntity.ok().body(stockService.getList());
    }

}
