package com.innovation.stockstock.comment.service;

import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.achievement.domain.MemberAchievement;
import com.innovation.stockstock.achievement.repository.AchievementRepository;
import com.innovation.stockstock.achievement.repository.MemberAchievementRepository;
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
import com.innovation.stockstock.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final MemberAchievementRepository memberAchievementRepository;
    private final AchievementRepository achievementRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public ResponseEntity<Object> postComment(Long postId, CommentRequestDto commentRequestDto, HttpServletRequest request) {
        Authentication authentication = jwtProvider.getAuthentication(request.getHeader("Authorization").substring(7));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Member member = userDetails.getMember();
        Post post = isPresentPost(postId);
        if (post == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        Comment comment = Comment.builder().post(post).content(commentRequestDto.getContent()).member(member).build();
        commentRepository.save(comment);

        post.updateCommentNum(true);
        if(!member.getId().equals(post.getMember().getId())) {
            member.updateCommentNum(true);

            NotificationRequestDto forPostWriter = new NotificationRequestDto(Event.댓글, member.getNickname() + "님이 댓글을 달았습니다.");
            try {
                notificationService.send(post.getMember().getId(), forPostWriter);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        if (member.getCommentNum() == 10) {
            Achievement achievement = achievementRepository.findByName("COMMENT");
            boolean hasAchieved = memberAchievementRepository.existsByMemberAndAchievement(member, achievement);
            if (!hasAchieved) {
                memberAchievementRepository.save(new MemberAchievement(member, achievement));
                NotificationRequestDto forCommentWriter = new NotificationRequestDto(Event.뱃지취득, "조잘조잘 수다왕 뱃지를 얻었습니다.");
                try {
                    notificationService.send(member.getId(), forCommentWriter);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
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
        Post post = comment.getPost();
        post.updateCommentNum(false);
        if (!member.getId().equals(post.getMember().getId())) {
            member.updateCommentNum(false);
        }

        commentRepository.delete(comment);

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
