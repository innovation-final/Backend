package com.innovation.stockstock.dto;

import com.innovation.stockstock.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private Member member;
    private String createdAt;
    private String modifiedAt;
}