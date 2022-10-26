package com.innovation.stockstock.member.mypage;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.dto.AccountResponseDto;
import com.innovation.stockstock.account.dto.StockHoldingResponseDto;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.account.repository.StockHoldingRepository;
import com.innovation.stockstock.achievement.domain.MemberAchievement;
import com.innovation.stockstock.comment.domain.Comment;
import com.innovation.stockstock.comment.repository.CommentRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.MemberUtil;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.achievement.dto.AchievementResponseDto;
import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.member.mypage.dto.OtherProfileResponseDto;
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
import com.innovation.stockstock.security.jwt.RefreshTokenRepository;
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
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyPageService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3Client s3Client;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;
    private final MemberUtil memberUtil;
    private final StockHoldingRepository stockHoldingRepository;

    @Transactional
    public ResponseDto<?> getMyProfile(HttpServletRequest request) {
        Member member = getMemberFromJwt(request);
        List<AchievementResponseDto> achievementsList = achievementsList(member);

        Account account = accountRepository.findByMember(member);
        AccountResponseDto accountResponseDto = null;
        ProfileResponseDto profileResponseDto;
        if(account == null){
            profileResponseDto = ProfileResponseDto.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .profileImg(member.getProfileImg())
                    .profileMsg(member.getProfileMsg())
                    .achievements(achievementsList)
                    .account(accountResponseDto)
                    .build();
        }
        else {
            memberUtil.updateAccountInfoAtCurrentTime(account); // 현재가 기준 수익률 반영
            accountResponseDto = getAccountResponseDto(account, null);
            profileResponseDto = ProfileResponseDto.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .profileImg(member.getProfileImg())
                    .profileMsg(member.getProfileMsg())
                    .account(accountResponseDto)
                    .achievements(achievementsList)
                    .build();
        }
        return ResponseDto.success(profileResponseDto);
    }

    @Transactional
    public ResponseEntity<?> changeProfile(HttpServletRequest request, ProfileRequestDto requestDto) {
        Member member = getMemberFromJwt(request);
        String nickname = requestDto.getNickname();
        MultipartFile profileImg = requestDto.getProfileImg();
        String profileMsg = requestDto.getProfileMsg();
        try {
            if (nickname == null && profileImg.isEmpty() && profileMsg == null) {
                return ResponseEntity.ok().body(ResponseDto.success("Nothing Changed"));
            }
            if (nickname != null) {
                member.updateNickname(nickname);
            }
            if (!profileImg.isEmpty()) {
                String imgUrl = uploadS3(profileImg, member);
                member.updateProfileImg(imgUrl);
            }
            if (profileMsg != null) {
                member.updateProfileMsg(profileMsg);
            }
            return ResponseEntity.ok().body(ResponseDto.success("Profile Changed"));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Transactional
    public ResponseDto<?> deleteMyAccount(HttpServletRequest request) {
        Long memberId = getMemberFromJwt(request).getId();
        String email = getMemberFromJwt(request).getEmail();
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
        refreshTokenRepository.deleteById(email);
        return ResponseDto.success("Delete Success");
    }

    @Transactional
    public ResponseEntity<?> getInfoOther(Long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if(optionalMember.isEmpty()){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        Member member = optionalMember.get();
        List<AchievementResponseDto> achievementsList = achievementsList(member);
        Account account = accountRepository.findByMember(member);
        AccountResponseDto accountResponseDto=null;
        List<StockHoldingResponseDto> stockHoldingResponseDtoList = new ArrayList<>();
        if(account==null){
            return ResponseEntity.ok().body(
                    ResponseDto.success(
                            OtherProfileResponseDto.builder()
                                    .id(member.getId())
                                    .nickname(member.getNickname())
                                    .profileImg(member.getProfileImg())
                                    .profileMsg(member.getProfileMsg())
                                    .email(member.getEmail())
                                    .achievements(achievementsList)
                                    .account(accountResponseDto)
                                    .build()));
        }else if(account.getStockHoldingsList().isEmpty()){
            accountResponseDto = getAccountResponseDto(account, stockHoldingResponseDtoList);
        }else {
            memberUtil.updateAccountInfoAtCurrentTime(account);
            List<StockHolding> stockHoldings = stockHoldingRepository.findByAccount(account);
            for (StockHolding stockHolding : stockHoldings) {
                StockHoldingResponseDto responseDto = StockHoldingResponseDto.builder()
                        .id(stockHolding.getId())
                        .stockName(stockHolding.getStockName())
                        .profit(stockHolding.getProfit())
                        .returnRate(stockHolding.getReturnRate())
                        .avgBuying(stockHolding.getAvgBuying())
                        .amount(stockHolding.getAmount())
                        .avgBuying(stockHolding.getAvgBuying())
                        .build();
                stockHoldingResponseDtoList.add(responseDto);
            }
            accountResponseDto = getAccountResponseDto(account, stockHoldingResponseDtoList);
        }
            return ResponseEntity.ok().body(
                    ResponseDto.success(
                            OtherProfileResponseDto.builder()
                                    .id(member.getId())
                                    .nickname(member.getNickname())
                                    .profileImg(member.getProfileImg())
                                    .profileMsg(member.getProfileMsg())
                                    .email(member.getEmail())
                                    .achievements(achievementsList)
                                    .account(accountResponseDto)
                                    .build()
                    )
            );
        }

    private AccountResponseDto getAccountResponseDto(Account account, List<StockHoldingResponseDto> stockHoldingResponseDtoList) {
        AccountResponseDto accountResponseDto;
        accountResponseDto = AccountResponseDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .seedMoney(account.getSeedMoney())
                .balance(account.getBalance())
                .targetReturnRate(account.getTargetReturnRate())
                .totalReturnRate(account.getTotalReturnRate())
                .totalProfit(account.getTotalProfit())
                .expireAt(String.valueOf(account.getExpireAt()))
                .stockHoldingsList(stockHoldingResponseDtoList)
                .createdAt(String.valueOf(account.getCreatedAt()))
                .totalRealizedProfit(account.getTotalRealizedProfit())
                .totalUnrealizedProfit(account.getTotalUnrealizedProfit())
                .build();
        return accountResponseDto;
    }

    private List<AchievementResponseDto> achievementsList(Member member) {
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
        return achievementsList;
    }

    private Member getMemberFromJwt(HttpServletRequest request) {
        Authentication authentication = jwtProvider.getAuthentication(request.getHeader("Authorization").substring(7));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getMember();
    }

    private String uploadS3(MultipartFile profileImg, Member member) throws IOException {
        String imgUrl = member.getProfileImg();
        String basicImg ="https://stockstock.s3.ap-northeast-2.amazonaws.com/e00a05fd-882b-448d-8b4f-9f3a541a5e2b-%EA%B0%9C%EB%AF%B8.jpg";
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
