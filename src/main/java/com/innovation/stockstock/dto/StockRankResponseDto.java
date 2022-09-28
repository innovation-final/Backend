package com.innovation.stockstock.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockRankResponseDto {
    private int rank;
    private String stockCode;
    private String stockName;
    private int firstPrice;
    private int highPrice;
    private int lowPrice;
    private int lastPrice;
    private Long volume;
    private Long tradingValue;
    private float fluctuationRate;
}
