package com.innovation.stockstock.post.service;

import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.achievement.domain.MemberAchievement;
import com.innovation.stockstock.achievement.repository.AchievementRepository;
import com.innovation.stockstock.achievement.repository.MemberAchievementRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.comment.domain.Comment;
import com.innovation.stockstock.comment.dto.CommentResponseDto;
import com.innovation.stockstock.notification.domain.Event;
import com.innovation.stockstock.notification.dto.NotificationRequestDto;
import com.innovation.stockstock.notification.service.NotificationService;
import com.innovation.stockstock.post.repository.DislikeRepository;
import com.innovation.stockstock.post.repository.LikeRepository;
import com.innovation.stockstock.post.domain.Post;
import com.innovation.stockstock.post.dto.PostRequestDto;
import com.innovation.stockstock.post.dto.PostResponseDto;
import com.innovation.stockstock.post.repository.PostRepository;
import com.innovation.stockstock.stock.document.StockList;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.stock.repository.StockListRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.security.jwt.JwtProvider;
import com.innovation.stockstock.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final DislikeRepository dislikeRepository;
    private final LikeRepository likeRepository;
    private final JwtProvider jwtProvider;
    private final PostRepository postRepository;
    private final StockListRepository stockListRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final AchievementRepository achievementRepository;
    private final NotificationService notificationService;

    @Transactional // ???????????? ?????? ??????
    public ResponseEntity<?> getPost(Long postId,HttpServletRequest request) {
        List<CommentResponseDto> responseDtoList = new ArrayList<>();
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        post.addViews();

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

        // ????????? ??? ??? ????????? ???.
        String accessToken = request.getHeader("Authorization");
        if(accessToken==null) {
            return ResponseEntity.ok().body(ResponseDto.success(makePostOneResponse(post, responseDtoList, false, false)));
        }

        try {
            // ???????????? ????????? ???.
            Member member = getMemberFromJwt(request);
            Member postWriter = post.getMember();

            if (!member.getId().equals(postWriter.getId())) {
                postWriter.addViewNum();
                if (postWriter.getViewNum() == 50) {
                    Achievement achievement = achievementRepository.findByName("VIEW");
                    memberAchievementRepository.save(new MemberAchievement(postWriter, achievement));
                    NotificationRequestDto notificationRequestDto = new NotificationRequestDto(Event.????????????, "???????????? ?????? ????????? ???????????????.",null);
                    notificationService.send(postWriter.getId(), notificationRequestDto);
                }
            }

            // ????????? ???????????? ????????? ??? ????????? ??????
            boolean isDonelike = likeRepository.existsByMemberAndPost(member, post);
            boolean isDoneDislike = dislikeRepository.existsByMemberAndPost(member, post);
            PostResponseDto postResponseDto = makePostOneResponse(post, responseDtoList, isDonelike, isDoneDislike);
            return ResponseEntity.ok().body(ResponseDto.success(postResponseDto));
        }catch(Exception e){
            return ResponseEntity.ok().body(ResponseDto.success(makePostOneResponse(post,responseDtoList,false,false)));
        }
    }

    public ResponseDto<?> getFivePosts() {
        List<Post> posts = postRepository.findFirst5ByOrderByCreatedAtDesc();
        List<PostResponseDto> responseDtoList = makePostResponse(posts);
        return ResponseDto.success(responseDtoList);
    }

    public ResponseDto<?> getFivePostsByLikes() {
        List<Post> posts = postRepository.findFirst5ByOrderByLikesDesc();
        List<PostResponseDto> responseDtoList = makePostResponse(posts);
        return ResponseDto.success(responseDtoList);
    }

    public ResponseDto<?> getFivePostsByOldTime() {
        List<Post> posts = postRepository.findFirst5ByOrderByCreatedAt();
        List<PostResponseDto> responseDtoList = makePostResponse(posts);
        return ResponseDto.success(responseDtoList);
    }

    @Transactional
    public ResponseEntity<?> writePost(PostRequestDto requestDto, HttpServletRequest request) {
        Member member = getMemberFromJwt(request);
        Post post = new Post(requestDto, member);
        postRepository.save(post);
        member.updatePostNum(true);
        if (member.getPostNum() == 10) {
            Achievement achievement = achievementRepository.findByName("POST");
            boolean hasAchieved = memberAchievementRepository.existsByMemberAndAchievement(member, achievement);
            if (!hasAchieved) {
                memberAchievementRepository.save(new MemberAchievement(member, achievement));
                NotificationRequestDto forPostWriter = new NotificationRequestDto(Event.????????????, "????????? ???????????? ????????? ???????????????.",null);
                try {
                    notificationService.send(member.getId(), forPostWriter);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
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

    @Transactional
    public ResponseEntity<?> deletePost(Long postId, HttpServletRequest request) {
        Member member = getMemberFromJwt(request);
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        } else if (!post.getMember().getId().equals(member.getId())) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_ALLOWED));
        } else {
            member.updatePostNum(false);

            postRepository.deleteById(postId);
            return ResponseEntity.ok().body(ResponseDto.success("Delete Post Success"));
        }
    }

    public ResponseEntity<?> getAllPostsByPages(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        List<Post> postList = posts.getContent();
        List<PostResponseDto> responseDtoList = makePostResponse(postList);
        Long totalSum = posts.getTotalElements();
        HashMap<Object,Object> response = new HashMap<>();
        response.put("??? ????????? ??????",totalSum);
        response.put("???????????? ?????????",responseDtoList);
        return ResponseEntity.ok().body(ResponseDto.success(response));
    }

    public ResponseDto<?> getStockPosts(String code) {
        StockList stock = stockListRepository.findByCode(code);
        if(stock == null){
            return ResponseDto.fail(ErrorCode.NULL_ID);
        }
        List<Post> postList = postRepository.findByStockName(stock.getName());
        List<PostResponseDto> postResponseDtoList = makePostResponse(postList);
        return ResponseDto.success(postResponseDtoList);
    }

    private static PostResponseDto makePostOneResponse(Post post,List<CommentResponseDto> responseDtoList,boolean isDoneLike,boolean isDoneDislike){
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .stockName(post.getStockName())
                .likes(post.getLikes())
                .dislikes(post.getDislikes())
                .commentNum(post.getCommentNum())
                .views(post.getViews())
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
                    .commentNum(post.getCommentNum())
                    .views(post.getViews())
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
}
