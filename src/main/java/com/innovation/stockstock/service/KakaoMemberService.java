package com.innovation.stockstock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.stockstock.dto.KakaoMemberInfoDto;
import com.innovation.stockstock.dto.TokenDto;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.repository.MemberRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.security.jwt.JwtProvider;
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
    private final JwtProvider jwtProvider;

<<<<<<< HEAD
=======
    // 토큰 발급 요청(POST)

    public void kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getAccessToken(code);
        KakaoMemberInfoDto kakaoMemberInfo = getKakaoMemberInfo(accessToken);
        Member kakaoUser = registerKakaoUserIfNeed(kakaoMemberInfo);
        forceLogin(kakaoUser);
        kakaoMembersAuthorizationInput(kakaoUser, response);
    }
>>>>>>> origin/main

    private String getAccessToken(String code) throws JsonProcessingException{
        // "인가 코드"로 "액세스 토큰" 요청
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoKey);
        body.add("redirect_uri", kakaoRedirectUrl);
        body.add("code", code);

        // Http Header 와 Http Body를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);

        // HTTP 요청 보내기 그리고 response의 응답 받기
        // RestTemplate : 간편하게 rest API 호출할 수 있는 스프링 내장 클래스
        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        // ObjectMapper : json을 자바 객체로.
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String accessToken = jsonNode.get("access_token").asText();

        return accessToken;
    }
    private KakaoMemberInfoDto getKakaoMemberInfo(String accessToken) throws JsonProcessingException{
        HttpHeaders headers = new HttpHeaders();
        // 토큰으로 카카오 API 호출
        // HTTP Header 생성
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP 요청 보내기
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
        // 강제 로그인 처리
        UserDetails userDetails = new UserDetailsImpl(kakaoMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void kakaoMembersAuthorizationInput(Member kakaoUser, HttpServletResponse response) {
        // response header에 token 추가
        TokenDto token = jwtProvider.generateTokenDto(kakaoUser);
        response.addHeader("Authorization", "BEARER " + token.getAccessToken());
        response.addHeader("refresh-token",token.getRefreshToken());
    }
    // 토큰 발급 요청(POST)
    public void kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getAccessToken(code);
        KakaoMemberInfoDto kakaoMemberInfo = getKakaoMemberInfo(accessToken);
        Member kakaoUser = registerKakaoUserIfNeed(kakaoMemberInfo);
        forceLogin(kakaoUser);
        kakaoMembersAuthorizationInput(kakaoUser, response);
    }

}
