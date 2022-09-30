package com.innovation.stockstock.service;

import com.innovation.stockstock.ErrorCode;
import com.innovation.stockstock.dto.request.CommentRequestDto;
import com.innovation.stockstock.dto.response.ResponseDto;
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
    public ResponseEntity<Object> postComment(Long postId, CommentRequestDto commentRequestDto) {
        Member member = getMember();
        Post post = isPresentPost(postId);
        Comment comment = Comment.builder().post(post).content(commentRequestDto.getContent()).member(member).build();
        commentRepository.save(comment);

        post.updateCommentNum(true);

        return ResponseEntity.ok().body(ResponseDto.success("Write Comment Success"));
    }

    @Transactional
    public ResponseEntity<Object> putComment(Long commentId, CommentRequestDto commentRequestDto) {
        Member member = getMember();
        Comment comment = isPresentComment(commentId);
        if(comment==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        if(!comment.getMember().getId().equals(member.getId())){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_ALLOWED));
        }
        comment.update(commentRequestDto);
        return ResponseEntity.ok().body(ResponseDto.success("Edit Comment Success"));

    }

    public ResponseEntity<Object> deleteComment(Long commentId) {
        Member member = getMember();
        Comment comment = isPresentComment(commentId);
        if(comment==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        if(!comment.getMember().getId().equals(member.getId())){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_ALLOWED));
        }
        commentRepository.delete(comment);

        Post post = comment.getPost();
        post.updateCommentNum(false);

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
