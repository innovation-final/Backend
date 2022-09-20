package com.innovation.stockstock.service;

import com.innovation.stockstock.ErrorCode;
import com.innovation.stockstock.dto.CommentDto;
import com.innovation.stockstock.dto.ResponseDto;
import com.innovation.stockstock.entity.Comment;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.entity.Post;
import com.innovation.stockstock.repository.CommentRepository;
import com.innovation.stockstock.repository.PostRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public ResponseEntity<Object> postComment(int postId,CommentDto commentDto) {
        Member member = getMember();
        Post post = isPresentPost(Long.valueOf(postId));
        if(member==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.INVALID_TOKEN));
        }
        Comment comment = Comment.builder().post(post).content(commentDto.getContent()).member(member).build();
        commentRepository.save(comment);
        return ResponseEntity.ok().body(ResponseDto.success("Write Comment Success"));
    }

    @Transactional
    public ResponseEntity<Object> putComment(int commentId, CommentDto commentDto) {
        Member member = getMember();
        Comment comment = isPresentComment(Long.valueOf(commentId));
        if(member==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.INVALID_TOKEN));
        }
        if(comment==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        if(!comment.getMember().getId().equals(member.getId())){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_ALLOWED));
        }
        comment.update(commentDto);
        return ResponseEntity.ok().body(ResponseDto.success("Edit Comment Success"));

    }

    @Transactional
    public ResponseEntity<Object> deleteComment(int commentId) {
        Member member = getMember();
        Comment comment = isPresentComment(Long.valueOf(commentId));
        if(member==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.INVALID_TOKEN));
        }
        if(comment==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        if(!comment.getMember().getId().equals(member.getId())){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_ALLOWED));
        }
        commentRepository.delete(comment);
        return ResponseEntity.ok().body(ResponseDto.success("Delete Comment Success"));
    }

    public Member getMember(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }
}
