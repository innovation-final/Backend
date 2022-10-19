package com.innovation.stockstock.achievement.controller;

import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.achievement.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AchievementSetter {
    private final AchievementRepository achievementRepository;

    //@PostMapping("/save")
    public void setAchievement() {
        achievementRepository.save(new Achievement("LIKE"));
        achievementRepository.save(new Achievement("VIEW"));
        achievementRepository.save(new Achievement("DISLIKE"));
        achievementRepository.save(new Achievement("POST"));
        achievementRepository.save(new Achievement("COMMENT"));
        achievementRepository.save(new Achievement("BUY"));
        achievementRepository.save(new Achievement("STOCKHOLD"));
        System.out.println("set complete");
    }
}
