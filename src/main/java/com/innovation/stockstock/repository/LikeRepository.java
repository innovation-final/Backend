package com.innovation.stockstock.repository;

import com.innovation.stockstock.entity.LikePost;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikePost,Long> {
    Optional<LikePost> findByPostAndMember(Post post, Member member);
    boolean existsByPostAndMember(Post post,Member member);

}
