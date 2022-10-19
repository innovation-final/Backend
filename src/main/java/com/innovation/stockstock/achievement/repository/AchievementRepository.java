package com.innovation.stockstock.achievement.repository;

import com.innovation.stockstock.achievement.domain.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Achievement findByName(String name);
}
