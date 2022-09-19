package com.innovation.stockstock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.stockstock.dto.KakaoMemberInfoDto;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.repository.MemberRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class MemberService {

    @Value("${kakao-restapi-key}")
    private final String kakaoKey;
    private final MemberRepository memberRepository;

    // 토큰 발급 요청(POST)
    // key = value 타입의 데이터를 보내줘야 함
    // RestTemplate rt = new RestTemplate()
    public void kakaoLogin(String code) throws JsonProcessingException {
        String accessToken = getAccessToken(code);
        KakaoMemberInfoDto kakaoMemberInfo = getKakaoMemberInfo(accessToken);
        Member kakaoUser = registerKakaoUserIfNeed(kakaoMemberInfo);
        Authentication authentication = forceLogin(kakaoUser);
        kakaoMembersAuthorizationInput(kakaoUser, authentication, response);
    }

    private String getAccessToken(String code) throws JsonProcessingException{
        // 1. "인가 코드"로 "액세스 토큰" 요청
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        // LinkedHashMap 입력순서를 보장하는 HashMap
        // MultiValueMap 중복된키로 들어오는 밸류값들을 온전하게 저장하고 싶을 때 사용.
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoKey);
        body.add("redirect_uri", "http://localhost:8080/api/member/login/kakao");
        body.add("code", code);

        // Http Header 와 Http Body를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);

        // HTTP 요청 보내기 그리고 response의 응답을 받기
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
        // 2. 토큰으로 카카오 API 호출
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
        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        return new KakaoMemberInfoDto(id, nickname, email);
    }
    private Member registerKakaoUserIfNeed(KakaoMemberInfoDto kakaoMemberInfo){
        Long kakaoId = kakaoMemberInfo.getId();
        String kakaoEmail = kakaoMemberInfo.getEmail();
        Member kakaoMember = memberRepository.findByEmail(kakaoEmail).orElse(null);

        if(kakaoMember == null){
            String nickname = kakaoMemberInfo.getNickname();
            kakaoMember = new Member(kakaoEmail, nickname, kakaoId);
            memberRepository.save(kakaoMember);
        }
        return kakaoMember;
    }
    private Authentication forceLogin(Member kakaoUser) {
        // 4. 강제 로그인 처리
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void kakaoMembersAuthorizationInput(Member kakaoUser, Authentication authentication, HttpServletResponse response) {
        // response header에 token 추가
        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String token = tokenProvider.createToken(kakaoUser);
        response.addHeader("access-token", token);
    }

}
