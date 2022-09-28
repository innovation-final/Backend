package com.innovation.stockstock.controller;

import com.innovation.stockstock.dto.StockResponseDto;
import com.innovation.stockstock.entity.Stock;
import com.innovation.stockstock.repository.StockRepository;
import com.innovation.stockstock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StockController {
    private final StockRepository stockRepository;
    private final StockService stockService;

    @GetMapping("/api/stock/get")
    public ResponseEntity<?> getInfo() {
        List<Stock> stocks = stockRepository.findAll();
        List<StockResponseDto> stockResponseDtoList = new ArrayList<>();
        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            StockResponseDto responseDto = StockResponseDto.builder()
                    .id(stock.getId())
                    .code(stock.getCode())
                    .date(stock.getDate())
                    .start_price(stock.getStart_price())
                    .higher_price(stock.getHigher_price())
                    .current_price(stock.getCurrent_price())
                    .volumn(stock.getVolumn())
                    .traded_volume(stock.getTraded_volume())
                    .build();
            stockResponseDtoList.add(responseDto);
        }
        return ResponseEntity.ok(stockResponseDtoList);
    }

    @GetMapping("/api/stock/rank/{criteria}")
    public ResponseEntity<?> getRank(@PathVariable String criteria) {
        return ResponseEntity.ok().body(stockService.getRank(criteria));
    }
}
