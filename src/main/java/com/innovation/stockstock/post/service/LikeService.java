package com.innovation.stockstock.post.service;

import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.achievement.domain.MemberAchievement;
import com.innovation.stockstock.achievement.repository.AchievementRepository;
import com.innovation.stockstock.achievement.repository.MemberAchievementRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.notification.domain.Event;
import com.innovation.stockstock.notification.dto.NotificationRequestDto;
import com.innovation.stockstock.notification.service.NotificationService;
import com.innovation.stockstock.post.domain.DislikePost;
import com.innovation.stockstock.post.domain.LikePost;
import com.innovation.stockstock.post.repository.DislikeRepository;
import com.innovation.stockstock.post.repository.LikeRepository;
import com.innovation.stockstock.post.domain.Post;
import com.innovation.stockstock.post.repository.PostRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final DislikeRepository dislikeRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final AchievementRepository achievementRepository;
    private final NotificationService notificationService;

    @Transactional
    public ResponseEntity<Object> doDisLike(Long postId) {
        Member member = getMember();
        Post post = isPresentPost(postId);
        if (post == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        Member postWriter = post.getMember();
        Optional<DislikePost> dislike = dislikeRepository.findByPostAndMember(post, member);
        Optional<LikePost> like = likeRepository.findByPostAndMember(post, member);

        if (like.isPresent()) { // 기존에 추천한 경우 추천 취소 및 비추천 등록
            likeRepository.delete(like.get());
            post.updateLikes(false);
            postWriter.updateLikeNum(false);

            DislikePost dislikePost = DislikePost.builder().post(post).member(member).build();
            post.updateDislikes(true);
            postWriter.updateDislikeNum(true);
            dislikeRepository.save(dislikePost);
        } else if (dislike.isEmpty()) { // 과거 비추천하지 않은 경우에만 비추천 등록
            DislikePost dislikePost = DislikePost.builder().post(post).member(member).build();
            dislikeRepository.save(dislikePost);
            postWriter.updateDislikeNum(true);
            post.updateDislikes(true);
        } else { // 과거 비추천한 경우에는 비추천 취소
            dislikeRepository.delete(dislike.get());
            post.updateDislikes(false);
            postWriter.updateDislikeNum(false);
        }

        if (postWriter.getDislikeNum() == 20) {
            Achievement achievement = achievementRepository.findByName("DISLIKE");
            boolean hasAchieved = memberAchievementRepository.existsByMemberAndAchievement(postWriter, achievement);
            if (!hasAchieved) {
                memberAchievementRepository.save(new MemberAchievement(postWriter, achievement));
                NotificationRequestDto forPostWriter = new NotificationRequestDto(Event.뱃지취득, "음 무슨글을 썼길래 뱃지를 얻었습니다. 상호 배려하는 글을 작성해주세요.",null);
                try {
                    notificationService.send(postWriter.getId(), forPostWriter);
                }catch (Exception e){
                    e.getMessage();
                }
            }
        }
        return ResponseEntity.ok().body(ResponseDto.success("Success"));
    }

    @Transactional
    public ResponseEntity<Object> doLike(Long postId) {
        Member member = getMember();
        Post post = isPresentPost(postId);
        if (post == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        Member postWriter = post.getMember();
        Optional<DislikePost> dislike = dislikeRepository.findByPostAndMember(post, member);
        Optional<LikePost> like = likeRepository.findByPostAndMember(post, member);
        // 과거 비추천한 경우 비추천 취소 및 추천 등록
        if (dislike.isPresent()) {
            dislikeRepository.delete(dislike.get());
            post.updateDislikes(false);
            postWriter.updateDislikeNum(false);

            LikePost likePost = LikePost.builder().post(post).member(member).build();
            post.updateLikes(true);
            postWriter.updateLikeNum(true);
            likeRepository.save(likePost);
            // 과거 추천하지 않은 경우에만 추천 등록
        } else if (like.isEmpty()) {
            LikePost likePost = LikePost.builder().post(post).member(member).build();
            likeRepository.save(likePost);
            post.updateLikes(true);
            postWriter.updateLikeNum(true);
            // 과거 추천한 경우에는 추천 삭제
        } else {
            likeRepository.delete(like.get());
            post.updateLikes(false);
            postWriter.updateLikeNum(false);
        }

        if (postWriter.getLikeNum() == 10) {
            Achievement achievement = achievementRepository.findByName("LIKE");
            boolean hasAchieved = memberAchievementRepository.existsByMemberAndAchievement(postWriter, achievement);
            if (!hasAchieved) {
                memberAchievementRepository.save(new MemberAchievement(postWriter, achievement));
                NotificationRequestDto forPostWriter = new NotificationRequestDto(Event.뱃지취득, "러블리한 인플루언서 뱃지를 얻었습니다.",null);
                try {
                    notificationService.send(postWriter.getId(), forPostWriter);
                }catch (Exception e){
                    e.getMessage();
                }
            }
        }
        return ResponseEntity.ok().body(ResponseDto.success("Success"));
    }


    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    public Member getMember() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }

}
