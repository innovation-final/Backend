package com.innovation.stockstock.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private String orderCategory;
    private int amount;
    private int price;
    private String stockName;
}
