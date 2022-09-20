package com.innovation.stockstock.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import com.innovation.stockstock.entity.Comment;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String nickname;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comment;

    public Member(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

}