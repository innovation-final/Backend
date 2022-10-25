package com.innovation.stockstock.ranking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRankingResponseDto {
    Long memberId;
    String nickname;
    String profileImg;
    float targetReturnRate;
//    AccountResponseDto account;
    float returnRate;
    Long profit;
    Long realizedProfit;
    Long unrealizedProfit;
}
