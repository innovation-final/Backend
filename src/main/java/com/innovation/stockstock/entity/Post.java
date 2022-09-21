package com.innovation.stockstock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innovation.stockstock.dto.PostRequestDto;
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
    private Long likes;
    private Long dislikes;

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
        this.member = member;
    }

    public void updatePost(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.stockName = requestDto.getStockName();
    }

    public void updateLikes(Boolean isAdded) {
        if (isAdded) {
            if (likes == null) {
                this.likes = 1L;
            } else {
                this.likes++;
            }
        } else {
            if (likes != null && likes > 0L) {
                this.likes--;
            }
        }
    }
    public void updateDislikes(Boolean isAdded) {
        if (isAdded) {
            if (dislikes == null) {
                this.dislikes = 1L;
            } else {
                this.dislikes++;
            }
        } else {
            if (dislikes != null && dislikes > 0L) {
                this.dislikes--;
            }
        }
    }
}
