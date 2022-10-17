package com.innovation.stockstock.achievement.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Achievement {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "achievement")
    private List<MemberAchievement> memberAchievements = new ArrayList<>();

    public Achievement(String name) {
        this.name = name;
    }
}
