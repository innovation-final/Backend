package com.innovation.stockstock.service;

import com.innovation.stockstock.document.FinTable;
import com.innovation.stockstock.document.News;
import com.innovation.stockstock.document.Stock;
import com.innovation.stockstock.dto.StockDetailDto;
import com.innovation.stockstock.dto.response.ResponseDto;
import com.innovation.stockstock.dto.response.StockRankResponseDto;
import com.innovation.stockstock.document.StockRank;
import com.innovation.stockstock.dto.response.StockResponseDto;
import com.innovation.stockstock.repository.FinTableRepository;
import com.innovation.stockstock.repository.NewsRepository;
import com.innovation.stockstock.repository.StockRankRepository;
import com.innovation.stockstock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockRankRepository stockRankRepository;
    private final NewsRepository newsRepository;
    private final FinTableRepository finTableRepository;

    public ResponseDto<?> getStock(String stockCode) {
        Stock stock = stockRepository.findByCode(stockCode);
        ArrayList<StockDetailDto> result = new ArrayList<>();
        List<List<String>> datas = stock.getData();
        for (List<String> data : datas) {
            Float flucRate;
            try {
                flucRate = Float.parseFloat(data.get(6));
            } catch (NumberFormatException e) {
                flucRate = null;
            }
            StockDetailDto res = StockDetailDto.builder()
                    .date(data.get(0))
                    .open(Integer.parseInt(data.get(1)))
                    .high(Integer.parseInt(data.get(2)))
                    .low(Integer.parseInt(data.get(3)))
                    .close(Integer.parseInt(data.get(4)))
                    .volume(Long.valueOf(data.get(5)))
                    .change(flucRate)
                    .build();
            result.add(res);
        }
        Map<String, String> current = stock.getCurrent();
        StockDetailDto now = StockDetailDto.builder()
                .date(String.valueOf(LocalDate.now()))
                .open(Integer.parseInt(current.get("first_price")))
                .high(Integer.parseInt(current.get("high_price")))
                .low(Integer.parseInt(current.get("low_price")))
                .close(Integer.parseInt(current.get("last_price")))
                .volume(Long.valueOf(current.get("volume")))
                .tradingValue(Long.valueOf(current.get("trading_value")))
                .change(Float.parseFloat(current.get("fluctuation_rate")) / 100)
                .build();

        return ResponseDto.success(StockResponseDto.builder()
                .code(stock.getCode())
                .name(stock.getName())
                .market(stock.getMarket())
                .marCap(stock.getMarcap())
                .stockDetail(result)
                .current(now)
                .build()
        );
    }

    public ResponseDto<?> getStockNews(String stockCode) {
        News news = newsRepository.findByCode(stockCode);
        return ResponseDto.success(news.getData());
    }

    public ResponseDto<?> getStockTable(String stockCode) {
        FinTable table = finTableRepository.findByCode(stockCode);
        return ResponseDto.success(table.getData());
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
