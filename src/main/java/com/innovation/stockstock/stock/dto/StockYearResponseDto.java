package com.innovation.stockstock.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockYearResponseDto {
    private String date;
    private int price;
}
