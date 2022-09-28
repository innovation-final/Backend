package com.innovation.stockstock.service;

import com.innovation.stockstock.dto.ResponseDto;
import com.innovation.stockstock.dto.StockRankResponseDto;
import com.innovation.stockstock.entity.StockRank;
import com.innovation.stockstock.repository.StockRankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRankRepository stockRankRepository;

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
