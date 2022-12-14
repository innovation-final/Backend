package com.innovation.stockstock.member.mypage.dto;

import com.innovation.stockstock.account.dto.AccountResponseDto;
import com.innovation.stockstock.achievement.dto.AchievementResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDto {
    private Long id;
    private String nickname;
    private String profileImg;
    private String profileMsg;
    private String email;
    private List<AchievementResponseDto> achievements;
    private AccountResponseDto account;
//    private Long profit;
//    private float totalReturnRate;
//    private Long realizedProfit;
//    private Long unrealizedProfit;
}
