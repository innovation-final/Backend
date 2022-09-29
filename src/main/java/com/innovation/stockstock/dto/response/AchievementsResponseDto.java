package com.innovation.stockstock.dto.response;
import com.innovation.stockstock.entity.Achievements;
import com.innovation.stockstock.entity.Member;
import lombok.Builder;

@Builder
public class AchievementsResponseDto {
    private Long id;
    private Achievements.AchievementCode AchievementCode;
    private Member member;
}
