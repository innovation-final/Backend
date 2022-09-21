package com.innovation.stockstock.dto;

import com.innovation.stockstock.entity.Member;
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
    private Long likes;
    private Long dislikes;
    private Member member;
    private List<CommentResponseDto> comments;
    private String createdAt;
    private String modifiedAt;
}
