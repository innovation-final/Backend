package com.innovation.stockstock.controller;

import com.innovation.stockstock.dto.CommentDto;
import com.innovation.stockstock.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService; // final

    @PostMapping(value = "/api/auth/comment/{postId}")
    public ResponseEntity<Object> postComment(@PathVariable int postId,@RequestBody CommentDto commentDto) {
        return commentService.postComment(postId,commentDto);
    }

    @PutMapping(value = "/api/auth/comment/{commentId}")
    public ResponseEntity<Object> putComment(@PathVariable int commentId, @RequestBody CommentDto commentDto) {
        return commentService.putComment(commentId,commentDto);
    }

    @DeleteMapping(value = "/api/auth/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable int commentId) {
        return commentService.deleteComment(commentId);
    }
}
