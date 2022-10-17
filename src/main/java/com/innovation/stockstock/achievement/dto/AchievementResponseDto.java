package com.innovation.stockstock.achievement.dto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AchievementResponseDto {
    private Long id;
    private String name;
    private String date;
}
