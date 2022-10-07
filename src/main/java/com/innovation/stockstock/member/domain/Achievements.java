package com.innovation.stockstock.member.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Achievements {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private AchievementCode AchievementCode;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    public enum AchievementCode {

        TODAY_TOP_RANKER,
        WEEKLY_TOP_RANKER,
        ALL_WEEKDAY_VISIT;

    }

}
