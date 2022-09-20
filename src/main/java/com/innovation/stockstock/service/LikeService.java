package com.innovation.stockstock.service;

import com.innovation.stockstock.ErrorCode;
import com.innovation.stockstock.dto.ResponseDto;
import com.innovation.stockstock.entity.*;
import com.innovation.stockstock.repository.DislikeRepository;
import com.innovation.stockstock.repository.LikeRepository;
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
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final DislikeRepository dislikeRepository;

    @Transactional
    public ResponseEntity<Object> doLike(Long postId) {
        Member member = getMember();
        Post post = isPresentPost(postId);
        Optional<LikePost> isDoneLike = likeRepository.findByPostAndMember(post,member);

        if(post==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        if(isDoneLike.isEmpty()){
            LikePost likePost = LikePost.builder().post(post).member(member).build();
            likeRepository.save(likePost);
            post.updateLikes(true);
            return ResponseEntity.ok().body(ResponseDto.success("Like Success"));
        }else{
            likeRepository.delete(isDoneLike.get());
            post.updateLikes(false);
            return ResponseEntity.ok().body(ResponseDto.success("Like Cancel Success"));
        }
    }

    @Transactional
    public ResponseEntity<Object> disLike(Long postId) {
        Member member = getMember();
        Post post = isPresentPost(postId);
        Optional<DislikePost> isDoneDislike = dislikeRepository.findByPostAndMember(post,member);

        if(post==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        if(isDoneDislike.isEmpty()){
            DislikePost dislikePost = DislikePost.builder().post(post).member(member).build();
            dislikeRepository.save(dislikePost);
            post.updateDislikes(true);
            return ResponseEntity.ok().body(ResponseDto.success("Dislike Success"));
        }else{
            dislikeRepository.delete(isDoneDislike.get());
            post.updateDislikes(false);
            return ResponseEntity.ok().body(ResponseDto.success("Dislike Cancel Success"));
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
