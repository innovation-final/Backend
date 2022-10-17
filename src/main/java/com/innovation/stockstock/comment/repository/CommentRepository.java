package com.innovation.stockstock.comment.repository;

import com.innovation.stockstock.comment.domain.Comment;
import com.innovation.stockstock.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Optional<Comment> findById(Long id);
    List<Comment> findAllByPostOrderByCreatedAt(Post post);
}
