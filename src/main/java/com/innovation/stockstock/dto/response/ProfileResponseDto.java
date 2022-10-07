package com.innovation.stockstock.dto.response;

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
    private String nickname;
    private String profileImg;
    private String profileMsg;
    private String email;
    private float totalReturnRate;
    private List<AchievementsResponseDto> achievements;
}
