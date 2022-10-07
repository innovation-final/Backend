package com.innovation.stockstock.post.repository;

import com.innovation.stockstock.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    List<Post> findFirst5ByOrderByCreatedAtDesc();

    List<Post> findFirst5ByOrderByLikesDesc();

    List<Post> findFirst5ByOrderByCreatedAt();

    List<Post> findByStockName(String name);

}
