package com.innovation.stockstock.achievement.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Achievement {
    @Id @GeneratedValue
    private Long id;

    private String name;

    public Achievement(String name) {
        this.name = name;
    }
}
