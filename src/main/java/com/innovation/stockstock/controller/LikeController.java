package com.innovation.stockstock.controller;


import com.innovation.stockstock.service.LikeService;
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
    public ResponseEntity<?> doLike(@PathVariable int postId) {
        return likeService.doLike(postId);
    }

    @PostMapping("/api/auth/post/dislike/{postId}")
    public ResponseEntity<?> disLike(@PathVariable int postId) {
        return likeService.disLike(postId);
    }

}
