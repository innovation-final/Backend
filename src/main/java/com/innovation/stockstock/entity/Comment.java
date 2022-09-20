package com.innovation.stockstock.entity;

import com.innovation.stockstock.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor // 이게 있어야 Builder가 작동됨.
@NoArgsConstructor // Class 'Comment' should have [public, protected] no-arg constructor
public class Comment {


    @Id
    @GeneratedValue
    private Long id;

    private String content;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name="post_id",nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;
    public void update(CommentDto commentDto) {
        this.content = commentDto.getContent();
    }

}
