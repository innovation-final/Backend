package com.innovation.stockstock.ranking;

import com.innovation.stockstock.account.dto.AccountResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponseDto {
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
