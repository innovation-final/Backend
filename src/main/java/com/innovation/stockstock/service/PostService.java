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

@Service
@RequiredArgsConstructor
public class PostService {

    private final DislikeRepository dislikeRepository;
    private final LikeRepository likeRepository;
    private final JwtProvider jwtProvider;
    private final PostRepository postRepository;
    @Transactional // 지연로딩 에러 해결
    public ResponseEntity<?> getPost(Long postId,HttpServletRequest request) {

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
        // 로그인 안 한 멤버일 때.
        String accessToken = request.getHeader("Authorization");
        if(accessToken==null) {
            return ResponseEntity.ok().body(ResponseDto.success(makePostOneResponse(post, responseDtoList, false, false)));
        }

        // 로그인한 멤버일 때.
        Member member = getMemberFromJwt(request);
        // 멤버가 좋아요를 눌렀는 지 여부를 확인
        boolean isDonelike = likeRepository.existsByMemberAndPost(member, post);
        boolean isDoneDislike = dislikeRepository.existsByMemberAndPost(member,post);
        PostResponseDto postResponseDto = makePostOneResponse(post,responseDtoList,isDonelike,isDoneDislike);
        return ResponseEntity.ok().body(ResponseDto.success(postResponseDto));
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

    private static PostResponseDto makePostOneResponse(Post post,List<CommentResponseDto> responseDtoList,boolean isDoneLike,boolean isDoneDislike){
        return PostResponseDto.builder()
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
                .isDoneLike(isDoneLike)
                .isDoneDisLike(isDoneDislike)
                .build();
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

    private Member getMemberFromJwt(HttpServletRequest request) {
        Authentication authentication = jwtProvider.getAuthentication(request.getHeader("Authorization").substring(7));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getMember();
    }


//    public ResponseEntity<?> getAllPostsByPages(int page, int size, String sortBy, boolean isAsc) {
//        Sort.Direction direction = isAsc? Sort.Direction.ASC : Sort.Direction.DESC;
//        Sort sort = Sort.by(direction, sortBy);
//        Pageable pageable = PageRequest.of(page,size,sort);
//        Page<Post> posts = postRepository.findAll(pageable);
//        return ResponseEntity.ok().body(ResponseDto.success(posts));
//    }
}
