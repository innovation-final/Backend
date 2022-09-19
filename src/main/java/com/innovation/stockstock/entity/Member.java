package com.innovation.stockstock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
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
    private Long kakaoId;

    public Member(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public Member(String email, String nickname, Long kakaoId) {
        this.email = email;
        this.nickname = nickname;
        this.kakaoId = kakaoId;

    }
}