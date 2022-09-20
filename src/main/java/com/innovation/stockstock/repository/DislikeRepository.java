package com.innovation.stockstock.repository;

import com.innovation.stockstock.entity.Dislike;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DislikeRepository extends JpaRepository<Dislike, Long> {
    Optional<Dislike> findByPostAndMember(Post post, Member member);

}