package com.innovation.stockstock.entity;

import com.innovation.stockstock.dto.PostRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Post {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    private String stockName;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

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
}
