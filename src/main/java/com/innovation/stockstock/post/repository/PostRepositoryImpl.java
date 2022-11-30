package com.innovation.stockstock.post.repository;

import com.innovation.stockstock.member.domain.Member;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static com.innovation.stockstock.comment.domain.QComment.comment;
import static com.innovation.stockstock.post.domain.QDislikePost.dislikePost;
import static com.innovation.stockstock.post.domain.QLikePost.likePost;
import static com.innovation.stockstock.post.domain.QPost.post;

public class PostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public void updateCommentNum(Member member) {
        queryFactory
                .update(post)
                .set(post.commentNum, post.commentNum.add(-1))
                .where(post.id.in(
                        JPAExpressions
                                .select(comment.post.id)
                                .from(comment)
                                .where(comment.member.eq(member))
                ))
                .execute();
    }

    @Override
    public void updateLikeNum(Member member) {
        queryFactory
                .update(post)
                .set(post.likes, post.likes.add(-1))
                .where(post.id.in(
                        JPAExpressions
                                .select(likePost.post.id)
                                .from(likePost)
                                .where(likePost.member.eq(member))
                ))
                .execute();
    }

    @Override
    public void updateDislikeNum(Member member) {
        queryFactory
                .update(post)
                .set(post.dislikes, post.dislikes.add(-1))
                .where(post.id.in(
                        JPAExpressions
                                .select(dislikePost.post.id)
                                .from(dislikePost)
                                .where(dislikePost.member.eq(member))
                ))
                .execute();
    }
}
