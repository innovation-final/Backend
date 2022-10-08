package com.innovation.stockstock.member.domain;

import com.innovation.stockstock.stock.like.LikeStock;
import com.innovation.stockstock.post.domain.Post;
import com.innovation.stockstock.comment.domain.Comment;
import com.innovation.stockstock.post.domain.DislikePost;
import com.innovation.stockstock.post.domain.LikePost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String nickname;
    private String profileImg = "https://stockstock.s3.ap-northeast-2.amazonaws.com/0b367fe8-bd78-4901-b54f-5e0d14a4a0b7-%EA%B0%9C%EB%AF%B8.jpg";
    private String profileMsg;
    private float totalReturnRate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Achievements> achievements= new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LikePost> likePosts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DislikePost> dislikePosts = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LikeStock> likeStocks = new ArrayList<>();


    public Member(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }


    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
    public void updateProfileMsg(String profileMsg) {
        this.profileMsg = profileMsg;
    }
}