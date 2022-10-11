package com.innovation.stockstock.account.dto;

import com.innovation.stockstock.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class AccountResponseDto {

    private Long id;
    private Long accountNumber;
    private Long deposit;
    private float targetReturnRate;
    private float totalReturnRate;
    private Long totalProfit;
    private String expireAt;
    private Member member;
    private List<StockHoldingResponseDto> stockHoldingsList;
    private String createdAt;

}