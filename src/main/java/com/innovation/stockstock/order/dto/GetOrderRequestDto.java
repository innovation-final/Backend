package com.innovation.stockstock.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetOrderRequestDto {
    private String orderCategory;
    private Boolean isSigned;
    private String startDate;
    private String endDate;
}
