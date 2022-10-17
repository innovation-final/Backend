package com.innovation.stockstock.member.mypage;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.innovation.stockstock.achievement.domain.MemberAchievement;
import com.innovation.stockstock.comment.domain.Comment;
import com.innovation.stockstock.comment.repository.CommentRepository;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.achievement.dto.AchievementResponseDto;
import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.member.mypage.dto.ProfileRequestDto;
import com.innovation.stockstock.member.mypage.dto.ProfileResponseDto;
import com.innovation.stockstock.member.repository.MemberRepository;
import com.innovation.stockstock.post.domain.DislikePost;
import com.innovation.stockstock.post.domain.LikePost;
import com.innovation.stockstock.post.domain.Post;
import com.innovation.stockstock.post.repository.DislikeRepository;
import com.innovation.stockstock.post.repository.LikeRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyPageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${basic.image}")
    private String basicImg;
    private final AmazonS3Client s3Client;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;

    public ResponseDto<?> getMyProfile(HttpServletRequest request) {
        Member member = getMemberFromJwt(request);
        List<AchievementResponseDto> achievementsList = new ArrayList<>();
        List<MemberAchievement> memberAchievements = member.getMemberAchievements();
        for (MemberAchievement memberAchievement : memberAchievements) {
            Achievement achievement = memberAchievement.getAchievement();
            AchievementResponseDto responseDto = AchievementResponseDto.builder()
                    .id(achievement.getId())
                    .name(achievement.getName())
                    .date(String.valueOf(memberAchievement.getCreatedAt()))
                    .build();
            achievementsList.add(responseDto);
        }
        return ResponseDto.success(
                ProfileResponseDto.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .nickname(member.getNickname())
                        .profileImg(member.getProfileImg())
                        .profileMsg(member.getProfileMsg())
                        .totalReturnRate(member.getTotalReturnRate())
                        .achievements(achievementsList)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<?> changeProfile(HttpServletRequest request, ProfileRequestDto requestDto) {
        Member member = getMemberFromJwt(request);
        String nickname = requestDto.getNickname();
        MultipartFile profileImg = requestDto.getProfileImg();
        String profileMsg = requestDto.getProfileMsg();

        try {
            if (nickname == null && profileImg == null && profileMsg == null) {
                return ResponseEntity.ok().body(ResponseDto.success("Nothing Changed"));
            }
            if (nickname != null) {
                member.updateNickname(nickname);
            }
            if (profileImg != null) {
                String imgUrl = uploadS3(profileImg, member);
                member.updateProfileImg(imgUrl);
            }
            if (profileMsg != null) {
                member.updateProfileMsg(profileMsg);
            }
            return ResponseEntity.ok().body(ResponseDto.success("Profile Changed"));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional
    public ResponseDto<?> deleteMyAccount(HttpServletRequest request) {
        Long memberId = getMemberFromJwt(request).getId();
        List<Comment> comments = commentRepository.findAllByMemberId(memberId);
        for (Comment comment : comments) {
            Post post = comment.getPost();
            post.updateCommentNum(false);
        }
        List<LikePost> likePosts = likeRepository.findAllByMemberId(memberId);
        for (LikePost likePost : likePosts) {
            Post post = likePost.getPost();
            post.updateLikes(false);
        }
        List<DislikePost> dislikePosts = dislikeRepository.findAllByMemberId(memberId);
        for (DislikePost dislikePost : dislikePosts) {
            Post post = dislikePost.getPost();
            post.updateDislikes(false);
        }
        memberRepository.deleteById(memberId);
        return ResponseDto.success("Delete Success");
    }

    private Member getMemberFromJwt(HttpServletRequest request) {
        Authentication authentication = jwtProvider.getAuthentication(request.getHeader("Authorization").substring(7));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getMember();
    }

    private String uploadS3(MultipartFile profileImg, Member member) throws IOException {
        String imgUrl = member.getProfileImg();
        if(imgUrl!=null && !imgUrl.equals(basicImg)){fileDelete(imgUrl);}
        String s3FileName = UUID.randomUUID() + "-" + profileImg.getOriginalFilename();
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(profileImg.getSize());
        objMeta.setContentType(profileImg.getContentType()); // 이 값을 설정해야 다운로드가 되지 않음
        s3Client.putObject(bucket, s3FileName, profileImg.getInputStream(), objMeta);
       return s3Client.getUrl(bucket, s3FileName).toString();
    }

    public void fileDelete(String url){
        try{
            String decodeVal = URLDecoder.decode(url.substring(51), StandardCharsets.UTF_8);
            s3Client.deleteObject(this.bucket,decodeVal);
        }catch (AmazonServiceException e){
            log.error(e.getErrorMessage());
        }
    }
}
