package com.innovation.stockstock.member.mypage.dto;

import com.innovation.stockstock.account.dto.AccountResponseDto;
import com.innovation.stockstock.achievement.dto.AchievementResponseDto;
import lombok.*;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OtherProfileResponseDto {
    private Long id;
    private String nickname;
    private String profileImg;
    private String profileMsg;
    private String email;
    private AccountResponseDto account;
    private List<AchievementResponseDto> achievements;

}
