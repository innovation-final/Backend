package com.innovation.stockstock.stock.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockDetailDto {
    private String date;
    private int open;
    private int high;
    private int low;
    private int close;
    private Long volume;
    private Long tradingValue;
    private Float change;
}
