package com.innovation.stockstock.account.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockHoldingResponseDto {
    private Long id;
    private String stockCode;
    private float targetReturnRate;
}
