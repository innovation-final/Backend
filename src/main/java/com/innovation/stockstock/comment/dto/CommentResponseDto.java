package com.innovation.stockstock.comment.dto;

import com.innovation.stockstock.member.domain.Member;
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
