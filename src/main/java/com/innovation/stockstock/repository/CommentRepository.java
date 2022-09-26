package com.innovation.stockstock.repository;

import com.innovation.stockstock.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Optional<Comment> findById(Long id);
}
