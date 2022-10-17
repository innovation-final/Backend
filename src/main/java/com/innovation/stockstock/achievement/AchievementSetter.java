package com.innovation.stockstock.achievement;

import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.achievement.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AchievementSetter {
    private final AchievementRepository achievementRepository;

    public void setAchievement() {
        achievementRepository.save(new Achievement("RETURN_WEEKLY"));
        achievementRepository.save(new Achievement("RETURN_MONTHLY"));
        achievementRepository.save(new Achievement("RETURN_ALLTIME"));
        achievementRepository.save(new Achievement("LIKE_WEEKLY"));
        achievementRepository.save(new Achievement("LIKE_MONTHLY"));
        achievementRepository.save(new Achievement("LIKE_ALLTIME"));
        System.out.println("set complete");
    }
}
