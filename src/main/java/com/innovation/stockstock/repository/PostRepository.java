package com.innovation.stockstock.repository;

import com.innovation.stockstock.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
