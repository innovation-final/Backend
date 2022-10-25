package com.innovation.stockstock.ranking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LikeRankingResponseDto {
    Long memberId;
    String nickname;
    String profileImg;
    private int likeNum;
}
