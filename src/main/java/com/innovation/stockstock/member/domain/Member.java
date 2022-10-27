package com.innovation.stockstock.member.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.achievement.domain.MemberAchievement;
import com.innovation.stockstock.notification.domain.Notification;
import com.innovation.stockstock.stock.like.LikeStock;
import com.innovation.stockstock.post.domain.Post;
import com.innovation.stockstock.comment.domain.Comment;
import com.innovation.stockstock.post.domain.DislikePost;
import com.innovation.stockstock.post.domain.LikePost;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.Size;

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
    @Size(max = 20)
    private String nickname;
    private String profileImg = "https://stockstock.s3.ap-northeast-2.amazonaws.com/e00a05fd-882b-448d-8b4f-9f3a541a5e2b-%EA%B0%9C%EB%AF%B8.jpg";
    @Size(max = 50)
    private String profileMsg;
    @JsonIgnore
    private int viewNum;
    @JsonIgnore
    private int likeNum;
    @JsonIgnore
    private int dislikeNum;
    @JsonIgnore
    private int postNum;
    @JsonIgnore
    private int commentNum;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<MemberAchievement> memberAchievements = new ArrayList<>();

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Notification> notification;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Account account;

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

    public void updatePostNum(Boolean isAdded) {
        if (isAdded) {
            this.postNum++;
        } else {
            this.postNum--;
        }
    }
    public void updateLikeNum(Boolean isAdded) {
        if (isAdded) {
            this.likeNum++;
        } else {
            this.likeNum--;
        }
    }
    public void updateDislikeNum(Boolean isAdded) {
        if (isAdded) {
            this.dislikeNum++;
        } else {
            this.dislikeNum--;
        }
    }
    public void updateCommentNum(Boolean isAdded) {
        if (isAdded) {
            this.commentNum++;
        } else {
            this.commentNum--;
        }
    }
    public void addViewNum() {
        this.viewNum++;
    }
}