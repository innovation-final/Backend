package com.innovation.stockstock.post.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innovation.stockstock.common.dto.Timestamped;
import com.innovation.stockstock.comment.domain.Comment;
import com.innovation.stockstock.post.dto.PostRequestDto;
import com.innovation.stockstock.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post extends Timestamped {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;
    private String stockName;
    private int likes;
    private int dislikes;
    private int commentNum;
    private int views;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LikePost> likePosts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DislikePost> dislikePosts = new ArrayList<>();

    public Post(PostRequestDto requestDto, Member member) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.stockName = requestDto.getStockName();
        this.likes = 0;
        this.dislikes = 0;
        this.commentNum = 0;
        this.views = 0;
        this.member = member;
    }

    public void updatePost(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.stockName = requestDto.getStockName();
    }

    public void updateLikes(Boolean isAdded) {
        if (isAdded) {
            this.likes++;
        } else {
            this.likes--;
        }
    }
    public void updateDislikes(Boolean isAdded) {
        if (isAdded) {
            this.dislikes++;
        } else {
            this.dislikes--;
        }
    }

    public void updateCommentNum(Boolean isAdded) {
        if (isAdded) {
            this.commentNum++;
        } else {
            this.commentNum--;
        }
    }

    public void addViews() {
        this.views++;
    }
}
