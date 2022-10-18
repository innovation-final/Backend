package com.innovation.stockstock.ranking;

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
    float returnRate;
    Long profit;
}
