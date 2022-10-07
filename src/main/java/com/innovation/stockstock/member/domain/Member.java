package com.innovation.stockstock.member.domain;

import com.innovation.stockstock.stock.like.LikeStock;
import com.innovation.stockstock.post.domain.Post;
import com.innovation.stockstock.comment.domain.Comment;
import com.innovation.stockstock.like.domain.DislikePost;
import com.innovation.stockstock.like.domain.LikePost;
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
    private String profileImg;
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