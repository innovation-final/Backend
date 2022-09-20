package com.innovation.stockstock.controller;

import com.innovation.stockstock.dto.PostRequestDto;
import com.innovation.stockstock.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/api/post")
    public ResponseEntity<?> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/api/post/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @PostMapping("/api/auth/post")
    public ResponseEntity<?> writePost(@RequestBody PostRequestDto requestDto, HttpServletRequest request) {
        return postService.writePost(requestDto, request);
    }

    @PutMapping("/api/auth/post/{postId}")
    public ResponseEntity<?> editPost(@PathVariable Long postId, @RequestBody PostRequestDto requestDto, HttpServletRequest request) {
        return postService.editPost(postId, requestDto, request);
    }

    @DeleteMapping("/api/auth/post/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        return postService.deletePost(postId, request);
    }
}
