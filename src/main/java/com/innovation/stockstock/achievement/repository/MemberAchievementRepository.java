package com.innovation.stockstock.achievement.repository;

import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.achievement.domain.MemberAchievement;
import com.innovation.stockstock.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAchievementRepository extends JpaRepository<MemberAchievement, Long> {
    boolean existsByMemberAndAchievement(Member member, Achievement achievement);
}
