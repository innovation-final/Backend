package com.innovation.stockstock.repository;

import com.innovation.stockstock.entity.Like;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {
    Optional<Like> findByPostAndMember(Post post, Member member);

}
