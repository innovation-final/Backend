package com.innovation.stockstock.order.controller;

import com.innovation.stockstock.order.dto.OrderRequestDto;
import com.innovation.stockstock.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/api/auth/buy")
    public ResponseEntity<?> getBuyOrders() {
        return orderService.getBuyOrders();
    }

    @GetMapping("/api/auth/sell")
    public ResponseEntity<?> getSellOrders() {
        return orderService.getSellOrders();
    }

    @PostMapping("/api/auth/buy/{stockCode}")
    public ResponseEntity<?> buyStock(@PathVariable String stockCode, @RequestBody OrderRequestDto requestDto) {
        return orderService.buyStock(stockCode, requestDto);
    }

    @PostMapping("/api/auth/sell/{stockCode}")
    public ResponseEntity<?> sellStock(@PathVariable String stockCode, @RequestBody OrderRequestDto requestDto) {
        return orderService.sellStock(stockCode, requestDto);
    }
}
