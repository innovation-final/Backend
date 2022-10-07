package com.innovation.stockstock.member.dto;
import com.innovation.stockstock.member.domain.Achievements;
import com.innovation.stockstock.member.domain.Member;
import lombok.Builder;

@Builder
public class AchievementsResponseDto {
    private Long id;
    private Achievements.AchievementCode AchievementCode;
    private Member member;
}
