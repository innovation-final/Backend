package com.innovation.stockstock.post.service;

import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
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

    @Transactional
    public ResponseEntity<Object> disLike(Long postId) {
        Member member = getMember();
        Post post = isPresentPost(postId);

        Optional<DislikePost> isDoneDislike = dislikeRepository.findByPostAndMember(post,member);
        Optional<LikePost> isDoneLike = likeRepository.findByPostAndMember(post,member);

        if(post==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        // 기존에 추천한 경우 추천 취소 및 비추천 등록
        if(isDoneLike.isPresent()) {
            likeRepository.delete(isDoneLike.get());
            post.updateLikes(false);
            DislikePost dislikePost = DislikePost.builder().post(post).member(member).build();
            post.updateDislikes(true);
            dislikeRepository.save(dislikePost);
            return ResponseEntity.ok().body(ResponseDto.success("Like Cancel And Dislike Update Success"));
        // 과거 비추천하지 않은 경우에만 비추천 등록
        } else if(isDoneDislike.isEmpty()){
            DislikePost dislikePost = DislikePost.builder().post(post).member(member).build();
            dislikeRepository.save(dislikePost);
            post.updateDislikes(true);
            return ResponseEntity.ok().body(ResponseDto.success("Dislike Success"));
        // 과거 비추천한 경우에는 비추천 취소
        }else{
            dislikeRepository.delete(isDoneDislike.get());
            post.updateDislikes(false);
            return ResponseEntity.ok().body(ResponseDto.success("Dislike Cancel Success"));
        }
    }

    @Transactional
    public ResponseEntity<Object> doLike(Long postId) {
        Member member = getMember();
        Post post = isPresentPost(postId);
        // 유저의 과거 추천, 비추천 이력 불러오기
        Optional<DislikePost> isDoneDislike = dislikeRepository.findByPostAndMember(post,member);
        Optional<LikePost> isDoneLike = likeRepository.findByPostAndMember(post,member);
        // 추천과 비추천 동시 등록 불가
        if(post==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        // 과거 비추천한 경우 비추천 취소 및 추천 등록
        if(isDoneDislike.isPresent()){
            dislikeRepository.delete(isDoneDislike.get());
            post.updateDislikes(false);
            LikePost likePost = LikePost.builder().post(post).member(member).build();
            post.updateLikes(true);
            likeRepository.save(likePost);
            return ResponseEntity.ok().body(ResponseDto.success("Dislike Cancel And Like Update Success"));
            // 과거 추천하지 않은 경우에만 추천 등록
        }else if(isDoneLike.isEmpty()){
            LikePost likePost = LikePost.builder().post(post).member(member).build();
            likeRepository.save(likePost);
            post.updateLikes(true);
            return ResponseEntity.ok().body(ResponseDto.success("Like Success"));
            // 과거 추천한 경우에는 추천 삭제
        }else {
            likeRepository.delete(isDoneLike.get());
            post.updateLikes(false);
            return ResponseEntity.ok().body(ResponseDto.success("Like Cancel Success"));
        }
    }


    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }
    public Member getMember(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }

}