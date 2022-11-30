package com.innovation.stockstock.post.repository;

import com.innovation.stockstock.member.domain.Member;

public interface CustomPostRepository {
    void updateCommentNum(Member member);
    void updateLikeNum(Member member);
    void updateDislikeNum(Member member);
}
