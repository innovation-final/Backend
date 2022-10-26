package com.innovation.stockstock.comment.controller;

import com.innovation.stockstock.comment.service.CommentService;
import com.innovation.stockstock.comment.dto.CommentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping(value = "/api/auth/comment/{postId}")
    public ResponseEntity<?> postComment(@PathVariable Long postId, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return commentService.postComment(postId, commentRequestDto, request);
    }

    @PutMapping(value = "/api/auth/comment/{commentId}")
    public ResponseEntity<?> putComment(@PathVariable Long commentId, @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.putComment(commentId, commentRequestDto);
    }

    @DeleteMapping(value = "/api/auth/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }
}
