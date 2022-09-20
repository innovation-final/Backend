package com.innovation.stockstock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String nickname;

    public Member(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}