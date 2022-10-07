package com.innovation.stockstock.stock.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class StockResponseDto {
    private String code;
    private String name;
    private String market;
    private Long marCap;
    private List<StockDetailDto> stockDetail;
    private StockDetailDto current;
    private boolean isDoneInterest;
}
