package com.innovation.stockstock.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private String nickname;
}
