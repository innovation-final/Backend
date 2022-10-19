package com.innovation.stockstock.account.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class AccountResponseDto {

    private Long id;
    private Long accountNumber;
    private int seedMoney;
    private Long balance;
    private float targetReturnRate;
    private float totalReturnRate;
    private Long totalProfit;
    private String expireAt;
    private List<StockHoldingResponseDto> stockHoldingsList;
    private String createdAt;

}
