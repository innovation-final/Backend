package com.innovation.stockstock.service;

import com.innovation.stockstock.ErrorCode;
import com.innovation.stockstock.dto.CommentResponseDto;
import com.innovation.stockstock.dto.PostResponseDto;
import com.innovation.stockstock.dto.PostRequestDto;
import com.innovation.stockstock.dto.ResponseDto;
import com.innovation.stockstock.entity.*;
import com.innovation.stockstock.repository.DislikeRepository;
import com.innovation.stockstock.repository.LikeRepository;
import com.innovation.stockstock.repository.PostRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final JwtProvider jwtProvider;
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;

    @Transactional // 지연로딩 에러 해결
    public ResponseEntity<?> getPost(Long postId,HttpServletRequest request) {
        Member member = getMemberFromJwt(request);
        List<CommentResponseDto> responseDtoList = new ArrayList<>();
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        for (Comment comment : post.getComments()) {
            CommentResponseDto responseDto = CommentResponseDto.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .member(comment.getMember())
                    .createdAt(String.valueOf(comment.getCreatedAt()))
                    .modifiedAt(String.valueOf(comment.getModifiedAt()))
                    .build();
            responseDtoList.add(responseDto);
        }
        // 로그인한 멤버일 때
        if(member != null) {
            // 멤버가 좋아요를 눌렀는 지 여부를 확인 후
            Optional<LikePost> islike = likeRepository.findByPostAndMember(post, member);
            Optional<DislikePost> isDislike = dislikeRepository.findByPostAndMember(post,member);
            // 좋아요를 눌렀다면, PostResponseDto에 isLike(true) 반환
            if (islike.isPresent()) {
                return ResponseEntity.ok().body(ResponseDto.success(
                                PostResponseDto.builder()
                                        .id(post.getId())
                                        .title(post.getTitle())
                                        .content(post.getContent())
                                        .stockName(post.getStockName())
                                        .likes(post.getLikes())
                                        .dislikes(post.getDislikes())
                                        .member(post.getMember())
                                        .comments(responseDtoList)
                                        .createdAt(String.valueOf(post.getCreatedAt()))
                                        .modifiedAt(String.valueOf(post.getModifiedAt()))
                                        .isDoneLike(true)
                                        .build()
                        )
                );
            }
            // 싫어요를 눌렀다면, PostResponseDto에 isDislike(true) 반환
            else if(isDislike.isPresent()){
                return ResponseEntity.ok().body(ResponseDto.success(
                        PostResponseDto.builder()
                                .id(post.getId())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .stockName(post.getStockName())
                                .likes(post.getLikes())
                                .dislikes(post.getDislikes())
                                .member(post.getMember())
                                .comments(responseDtoList)
                                .createdAt(String.valueOf(post.getCreatedAt()))
                                .modifiedAt(String.valueOf(post.getModifiedAt()))
                                .isDoneDisLike(true)
                                .build()
                    )
                );
            }
        }
        return ResponseEntity.ok().body(ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .stockName(post.getStockName())
                        .likes(post.getLikes())
                        .dislikes(post.getDislikes())
                        .member(post.getMember())
                        .comments(responseDtoList)
                        .createdAt(String.valueOf(post.getCreatedAt()))
                        .modifiedAt(String.valueOf(post.getModifiedAt()))
                        .build()));
    }

    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> responseDtoList = makePostResponse(posts);
        return ResponseEntity.ok().body(ResponseDto.success(responseDtoList));
    }

    public ResponseDto<?> getFivePosts() {
        List<Post> posts = postRepository.findFirst5ByOrderByCreatedAtDesc();
        List<PostResponseDto> responseDtoList = makePostResponse(posts);
        return ResponseDto.success(responseDtoList);
    }

    public ResponseDto<?> getPostsByLikes() {
        List<Post> posts = postRepository.findAllByOrderByLikesDesc();
        List<PostResponseDto> responseDtoList = makePostResponse(posts);
        return ResponseDto.success(responseDtoList);
    }

    public ResponseDto<?> getFivePostsByLikes() {
        List<Post> posts = postRepository.findFirst5ByOrderByLikesDesc();
        List<PostResponseDto> responseDtoList = makePostResponse(posts);
        return ResponseDto.success(responseDtoList);
    }

    public ResponseDto<?> getPostsByOldTime() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAt();
        List<PostResponseDto> responseDtoList = makePostResponse(posts);
        return ResponseDto.success(responseDtoList);
    }

    public ResponseDto<?> getFivePostsByOldTime() {
        List<Post> posts = postRepository.findFirst5ByOrderByCreatedAt();
        List<PostResponseDto> responseDtoList = makePostResponse(posts);
        return ResponseDto.success(responseDtoList);
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

    private static List<PostResponseDto> makePostResponse(List<Post> posts) {
        List<PostResponseDto> responseDtoList = new ArrayList<>();
        for (Post post : posts) {
            PostResponseDto responseDto = PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .stockName(post.getStockName())
                    .likes(post.getLikes())
                    .dislikes(post.getDislikes())
                    .member(post.getMember())
                    .createdAt(String.valueOf(post.getCreatedAt()))
                    .modifiedAt(String.valueOf(post.getModifiedAt()))
                    .build();
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }
}
