package com.innovation.stockstock.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderRequestDto {
    private String orderCategory;
    private int amount;
    private int price;
}
