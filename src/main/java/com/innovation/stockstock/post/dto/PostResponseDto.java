package com.innovation.stockstock.post.dto;

import com.innovation.stockstock.comment.dto.CommentResponseDto;
import com.innovation.stockstock.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String stockName;
    private boolean isDoneLike;
    private boolean isDoneDisLike;
    private int likes;
    private int dislikes;
    private int commentNum;
    private Member member;
    private List<CommentResponseDto> comments;
    private String createdAt;
    private String modifiedAt;
}
