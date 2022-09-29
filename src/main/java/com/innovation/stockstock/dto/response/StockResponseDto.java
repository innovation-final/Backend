package com.innovation.stockstock.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockResponseDto {
    private String date;
    private int open;
    private int high;
    private int low;
    private int close;
    private Long volume;
    private float change;
}
