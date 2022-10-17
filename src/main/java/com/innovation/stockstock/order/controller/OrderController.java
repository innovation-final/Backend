package com.innovation.stockstock.order.controller;

import com.innovation.stockstock.order.dto.GetOrderRequestDto;
import com.innovation.stockstock.order.dto.OrderRequestDto;
import com.innovation.stockstock.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/api/auth/order")
    public ResponseEntity<?> getOrders(GetOrderRequestDto requestDto) {
        return orderService.getOrders(requestDto);
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
