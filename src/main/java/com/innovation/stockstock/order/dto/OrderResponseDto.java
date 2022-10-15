package com.innovation.stockstock.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDto {

    private Long id;
    private String orderCategory;
    private String date;
    private int amount;
    private int price;
}
