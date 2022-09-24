package com.innovation.stockstock.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockResponseDto {
    Long id;
    String code;
    int current_price;
    int volumn;
    int traded_volume;
    String date;
    int start_price;
    int higher_price;
    int lower_price;
}
