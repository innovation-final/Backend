package com.innovation.stockstock.comment.service;

import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.comment.dto.CommentRequestDto;
import com.innovation.stockstock.common.MemberUtil;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.comment.domain.Comment;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.post.domain.Post;
import com.innovation.stockstock.notification.domain.Event;
import com.innovation.stockstock.notification.dto.NotificationRequestDto;
import com.innovation.stockstock.notification.service.NotificationService;
import com.innovation.stockstock.comment.repository.CommentRepository;
import com.innovation.stockstock.post.repository.PostRepository;
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
    private final NotificationService notificationService;
    @Transactional
    public ResponseEntity<Object> postComment(Long postId, CommentRequestDto commentRequestDto) {
        Member member = MemberUtil.getMember();
        Post post = isPresentPost(postId);
        Comment comment = Comment.builder().post(post).content(commentRequestDto.getContent()).member(member).build();
        commentRepository.save(comment);

        post.updateCommentNum(true);
        if(!member.getId().equals(post.getMember().getId())){
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto(Event.댓글, member.getNickname()+"님이 댓글을 달았습니다.");
            notificationService.send(post.getMember().getId(), notificationRequestDto);
        }
        return ResponseEntity.ok().body(ResponseDto.success("Write Comment Success"));
    }

    @Transactional
    public ResponseEntity<Object> putComment(Long commentId, CommentRequestDto commentRequestDto) {
        Member member = MemberUtil.getMember();
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

    @Transactional
    public ResponseEntity<Object> deleteComment(Long commentId) {
        Member member = MemberUtil.getMember();
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

    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }
    public Comment isPresentComment(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }
}
