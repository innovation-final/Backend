package com.innovation.stockstock.controller;

import com.innovation.stockstock.dto.CommentRequestDto;
import com.innovation.stockstock.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService; // final

    @PostMapping(value = "/api/auth/comment/{postId}")
    public ResponseEntity<?> postComment(@PathVariable Long postId,@RequestBody CommentRequestDto commentRequestDto) {
        return commentService.postComment(postId, commentRequestDto);
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
