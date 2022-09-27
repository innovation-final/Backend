package com.innovation.stockstock.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.innovation.stockstock.dto.AchievementsResponseDto;
import com.innovation.stockstock.dto.ProfileResponseDto;
import com.innovation.stockstock.dto.ResponseDto;
import com.innovation.stockstock.entity.Achievements;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.repository.MemberRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyPageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3Client s3Client;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public ResponseDto<?> getMyProfile(HttpServletRequest request) {
        Member member = getMemberFromJwt(request);
        List<AchievementsResponseDto> achievementsList = new ArrayList<>();
        for (Achievements ach : member.getAchievements()) {
            AchievementsResponseDto responseDto = AchievementsResponseDto.builder()
                    .AchievementCode(ach.getAchievementCode())
                    .member(member)
                    .id(ach.getId())
                    .build();
            achievementsList.add(responseDto);
        }
        return ResponseDto.success(
                ProfileResponseDto.builder()
                        .email(member.getEmail())
                        .nickname(member.getNickname())
                        .profileImg(member.getProfileImg())
                        .totalReturnRate(member.getTotalReturnRate())
                        .achievements(achievementsList)
                        .build()
        );
    }

    public ResponseDto<?> changeProfile(HttpServletRequest request, String nickname, MultipartFile profileImg) throws IOException {
        Member member = getMemberFromJwt(request);
        if (nickname == null && profileImg==null) {
            return ResponseDto.success("Nothing to change");
        } else if (nickname != null && profileImg==null) {
            member.updateNickname(nickname);
            memberRepository.save(member);
            return ResponseDto.success("Nickname Changed");
        } else if (nickname == null && profileImg!=null) {
            String s3FileName = UUID.randomUUID() + "-" + profileImg.getOriginalFilename();
            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentLength(profileImg.getSize());
            objMeta.setContentType(profileImg.getContentType()); // 이 값을 설정해야 다운로드가 되지 않음
            s3Client.putObject(bucket, s3FileName, profileImg.getInputStream(), objMeta);

            String imgUrl = s3Client.getUrl(bucket, s3FileName).toString();
            member.updateProfileImg(imgUrl);
            memberRepository.save(member);
            return ResponseDto.success("Profile_Img Changed");
        } else {
            String s3FileName = UUID.randomUUID() + "-" + profileImg.getOriginalFilename();
            ObjectMetadata objMeta = new ObjectMetadata();
            objMeta.setContentLength(profileImg.getSize());
            objMeta.setContentType(profileImg.getContentType());
            s3Client.putObject(bucket, s3FileName, profileImg.getInputStream(), objMeta);
            String imgUrl = s3Client.getUrl(bucket, s3FileName).toString();
            member.update(nickname, imgUrl);
            memberRepository.save(member);
            return ResponseDto.success("Nickname And Profile_Img Changed");
        }
    }

    public ResponseDto<?> deleteMyAccount(HttpServletRequest request) {
        Member member = getMemberFromJwt(request);
        memberRepository.deleteById(member.getId());
        return ResponseDto.success("Delete Success");
    }

    private Member getMemberFromJwt(HttpServletRequest request) {
        Authentication authentication = jwtProvider.getAuthentication(request.getHeader("Authorization").substring(7));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getMember();
    }

}
