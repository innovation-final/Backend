package com.innovation.stockstock.member.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.stockstock.member.oauth.dto.KakaoMemberInfoDto;
import com.innovation.stockstock.security.jwt.RefreshToken;
import com.innovation.stockstock.security.jwt.RefreshTokenRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.security.jwt.JwtProvider;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.member.repository.MemberRepository;
import com.innovation.stockstock.security.jwt.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class KakaoMemberService {
    @Value("${kakao-restapi-key}")
    private String kakaoKey;

    @Value("${kakao-redirect-url}")
    private String kakaoRedirectUrl;

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public void kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getAccessToken(code);
        KakaoMemberInfoDto kakaoMemberInfo = getKakaoMemberInfo(accessToken);

        Member kakaoUser = registerKakaoUserIfNeed(kakaoMemberInfo);

        forceLogin(kakaoUser);

        String refreshToken = kakaoMembersAuthorizationInput(kakaoUser, response);

        refreshTokenRepository.save(new RefreshToken(kakaoUser, refreshToken));
    }
    private String getAccessToken(String code) throws JsonProcessingException{
        // "?????? ??????"??? "????????? ??????" ??????
        // HTTP Header ??????
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body ??????
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoKey);
        body.add("redirect_uri", kakaoRedirectUrl);
        body.add("code", code);

        // Http Header ??? Http Body??? ????????? ??????????????? ??????
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);

        // HTTP ?????? ????????? ????????? response??? ?????? ??????
        // RestTemplate : ???????????? rest API ????????? ??? ?????? ????????? ?????? ?????????
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP ?????? (JSON) -> ????????? ?????? ??????
        // ObjectMapper : json??? ?????? ?????????.
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String accessToken = jsonNode.get("access_token").asText();

        return accessToken;
    }
    private KakaoMemberInfoDto getKakaoMemberInfo(String accessToken) throws JsonProcessingException{
        HttpHeaders headers = new HttpHeaders();
        // ???????????? ????????? API ??????
        // HTTP Header ??????
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP ?????? ?????????
        HttpEntity<MultiValueMap<String, String>> kakaoMemberInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoMemberInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String nickname = jsonNode.get("properties").get("nickname").asText();
        String email = jsonNode.get("kakao_account").get("email").asText();

        return new KakaoMemberInfoDto(nickname, email);
    }
    private Member registerKakaoUserIfNeed(KakaoMemberInfoDto kakaoMemberInfo){
        String kakaoEmail = kakaoMemberInfo.getEmail();
        Member kakaoMember = memberRepository.findByEmail(kakaoEmail).orElse(null);

        if(kakaoMember == null){
            String nickname = kakaoMemberInfo.getNickname();
            kakaoMember = new Member(kakaoEmail, nickname);
            memberRepository.save(kakaoMember);
        }

        return kakaoMember;
    }

    private void forceLogin(Member kakaoMember) {
        // ?????? ????????? ??????
        UserDetails userDetails = new UserDetailsImpl(kakaoMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String kakaoMembersAuthorizationInput(Member kakaoUser, HttpServletResponse response) {
        // response header??? token ??????
        TokenDto token = jwtProvider.generateTokenDto(kakaoUser);
        response.addHeader("Authorization", "BEARER " + token.getAccessToken());
        response.addHeader("refresh-token",token.getRefreshToken());
        return token.getRefreshToken();
    }

}
