package com.innovation.stockstock.dto;

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
    private String nickname;
    private List<CommentResponseDto> comments;
}
