package com.innovation.stockstock.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockResponseDto {
    Long id;
    int code;
    int current_price;
    int volumn;
    int volumn_amount;
    String date;
    int start_price;
    int final_price;
}
