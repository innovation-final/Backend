package com.innovation.stockstock.post.controller;


import com.innovation.stockstock.post.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/api/auth/post/like/{postId}")
    public ResponseEntity<?> doLike(@PathVariable Long postId) {
        return likeService.doLike(postId);
    }

    @PostMapping("/api/auth/post/dislike/{postId}")
    public ResponseEntity<?> disLike(@PathVariable Long postId) {
        return likeService.doDisLike(postId);
    }

}
