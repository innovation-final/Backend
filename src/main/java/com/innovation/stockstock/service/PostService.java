package com.innovation.stockstock.service;

import com.innovation.stockstock.ErrorCode;
import com.innovation.stockstock.dto.PostRequestDto;
import com.innovation.stockstock.dto.ResponseDto;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.entity.Post;
import com.innovation.stockstock.repository.PostRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final JwtProvider jwtProvider;
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok().body(ResponseDto.success(posts));
    }

    public ResponseEntity<?> getPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        return ResponseEntity.ok().body(ResponseDto.success(post));
    }

    public ResponseEntity<?> writePost(PostRequestDto requestDto, HttpServletRequest request) {
        Member member = getMemberFromJwt(request);
        Post post = new Post(requestDto, member);
        postRepository.save(post);
        return ResponseEntity.ok().body(ResponseDto.success("Write Post Success"));
    }

    @Transactional
    public ResponseEntity<?> editPost(Long postId, PostRequestDto requestDto, HttpServletRequest request) {
        Member member = getMemberFromJwt(request);
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        } else if (!post.getMember().equals(member)) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_ALLOWED));
        } else {
            post.updatePost(requestDto);
            return ResponseEntity.ok().body(ResponseDto.success("Edit Post Success"));
        }
    }

    public ResponseEntity<?> deletePost(Long postId, HttpServletRequest request) {
        Member member = getMemberFromJwt(request);
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        } else if (!post.getMember().getId().equals(member.getId())) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_ALLOWED));
        } else {
            postRepository.deleteById(postId);
            return ResponseEntity.ok().body(ResponseDto.success("Delete Post Success"));
        }
    }

    private Member getMemberFromJwt(HttpServletRequest request) {
        Authentication authentication = jwtProvider.getAuthentication(request.getHeader("Authorization").substring(7));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getMember();
    }
}
