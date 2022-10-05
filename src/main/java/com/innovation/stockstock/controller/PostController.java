package com.innovation.stockstock.controller;

import com.innovation.stockstock.dto.request.PostRequestDto;
import com.innovation.stockstock.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/api/post/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId,HttpServletRequest request) {
        return postService.getPost(postId,request);
    }
    @GetMapping("/api/post/main")
    public ResponseEntity<?> getFivePosts() {
        return ResponseEntity.ok().body(postService.getFivePosts());
    }

    @GetMapping("/api/post/likes/main")
    public ResponseEntity<?> getFivePostsByLikes() {
        return ResponseEntity.ok().body(postService.getFivePostsByLikes());
    }

    @GetMapping("/api/post/old/main")
    public ResponseEntity<?> getFivePostsByOldTime() {
        return ResponseEntity.ok().body(postService.getFivePostsByOldTime());
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

    @GetMapping("/api/post")
    public ResponseEntity<?> getAllPostsByPages(@PageableDefault(page = 0,size=10,sort="createdAt",direction= Sort.Direction.DESC) Pageable pageable){
            return postService.getAllPostsByPages(pageable);
    }

    @GetMapping("/api/post/stock/{code}")
    public ResponseEntity<?> getStockPosts(@PathVariable String code){
        return ResponseEntity.ok(postService.getStockPosts(code));
    }
}
