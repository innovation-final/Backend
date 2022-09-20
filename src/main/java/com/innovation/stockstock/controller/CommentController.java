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

    @PostMapping(value = "/api/member/comment")
    public ResponseEntity<Object> postComment(@RequestBody CommentDto commentDto) {
        return commentService.postComment(commentDto);
    }

    @PutMapping(value = "/api/member/comment/{commentId}")
    public ResponseEntity<Object> putComment(@PathVariable int commentId, @RequestBody CommentDto commentDto) {
        return commentService.putComment(commentId,commentDto);
    }

    @DeleteMapping(value = "/api/member/comment/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable int commentId) {
        return commentService.deleteComment(commentId);
    }


}
