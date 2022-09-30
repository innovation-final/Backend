package com.innovation.stockstock.service;

import com.innovation.stockstock.document.Stock;
import com.innovation.stockstock.dto.response.ResponseDto;
import com.innovation.stockstock.dto.response.StockRankResponseDto;
import com.innovation.stockstock.document.StockRank;
import com.innovation.stockstock.dto.response.StockResponseDto;
import com.innovation.stockstock.repository.StockRankRepository;
import com.innovation.stockstock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockRankRepository stockRankRepository;

    public ResponseDto<?> getStock(String stockCode) {
        ArrayList<Object> result = new ArrayList<>();
        Stock stock = stockRepository.findByCode(stockCode);
        result.add(stock.getCode());
        result.add(stock.getName());
        result.add(stock.getMarket());
        result.add(stock.getMarcap());
        List<List<String>> datas = stock.getData();
        for (List<String> data : datas) {
            StockResponseDto res = StockResponseDto.builder()
                    .date(data.get(0))
                    .open(Integer.parseInt(data.get(1)))
                    .low(Integer.parseInt(data.get(2)))
                    .high(Integer.parseInt(data.get(3)))
                    .close(Integer.parseInt(data.get(4)))
                    .volume(Long.valueOf(data.get(5)))
                    .change(Float.parseFloat(data.get(6)))
                    .build();
            result.add(res);
        }
        Map<String, String> current = stock.getCurrent();
        result.add(StockResponseDto.builder()
                .date("current")
                .open(Integer.parseInt(current.get("first_price")))
                .low(Integer.parseInt(current.get("low_price")))
                .high(Integer.parseInt(current.get("high_price")))
                .close(Integer.parseInt(current.get("last_price")))
                .volume(Long.valueOf(current.get("volume")))
                .tradingValue(Long.valueOf(current.get("trading_value")))
                .change(Float.parseFloat(current.get("fluctuation_rate")))
                .build());

        return ResponseDto.success(result);
    }

    public ResponseDto<?> getRank(String criteria) {
        List<StockRankResponseDto> responseDtoList = new ArrayList<>();
        StockRank stockRank = stockRankRepository.findByCriteria(criteria);
        List<Map<String, String>> datas = stockRank.getData();
        for (Map<String, String> data : datas) {
            StockRankResponseDto rankResponseDto = StockRankResponseDto.builder()
                    .rank(Integer.parseInt(data.get("rank")))
                    .stockCode(data.get("stock_code"))
                    .stockName(data.get("stock_name"))
                    .firstPrice(Integer.parseInt(data.get("first_price")))
                    .highPrice(Integer.parseInt(data.get("high_price")))
                    .lowPrice(Integer.parseInt(data.get("low_price")))
                    .lastPrice(Integer.parseInt(data.get("last_price")))
                    .volume(Long.valueOf(data.get("volume")))
                    .tradingValue(Long.valueOf(data.get("trading_value")))
                    .fluctuationRate(Float.parseFloat(data.get("fluctuation_rate")))
                    .build();
            responseDtoList.add(rankResponseDto);
        }
        return ResponseDto.success(responseDtoList);
    }
}
