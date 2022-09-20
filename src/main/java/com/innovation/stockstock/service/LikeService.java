package com.innovation.stockstock.service;

import com.innovation.stockstock.ErrorCode;
import com.innovation.stockstock.dto.CommentDto;
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

    public Member getMember(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }

    @Transactional
    public ResponseEntity<Object> doLike(int postId) {
        Member member = getMember();
        Post post = isPresentPost(Long.valueOf(postId));
        Optional<Like> isDoneLike = likeRepository.findByPostAndMember(post,member);
        if(member==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_ALLOWED));
        }
        if(post==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        if(isDoneLike.isEmpty()){
            Like like = Like.builder().post(post).member(member).build();
            likeRepository.save(like);
            return ResponseEntity.ok().body(ResponseDto.success("Like Success"));
        }else{
            likeRepository.delete(isDoneLike.get());
            return ResponseEntity.ok().body(ResponseDto.success("Like Cancel Success"));
        }
    }
    @Transactional
    public ResponseEntity<Object> disLike(int postId) {
        Member member = getMember();
        Post post = isPresentPost(Long.valueOf(postId));
        Optional<Dislike> isDoneDislike = dislikeRepository.findByPostAndMember(post,member);
        if(member==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_ALLOWED));
        }
        if(post==null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        if(isDoneDislike.isEmpty()){
            Dislike dislike = Dislike.builder().post(post).member(member).build();
            dislikeRepository.save(dislike);
            return ResponseEntity.ok().body(ResponseDto.success("Dislike Success"));
        }else{
            dislikeRepository.delete(isDoneDislike.get());
            return ResponseEntity.ok().body(ResponseDto.success("Dislike Cancel Success"));
        }
    }
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

}
