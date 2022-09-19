package com.innovation.stockstock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.stockstock.config.GoogleConfigUtils;
import com.innovation.stockstock.service.GoogleMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.innovation.stockstock.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final GoogleMemberService googleMemberService;
    private final GoogleConfigUtils googleConfigUtils;

    @GetMapping(value = "/api/member/login/google")
    public ResponseEntity<Object> moveGoogleInitUrl() {
        String authUrl = googleConfigUtils.googleInitUrl();
        URI redirectUri;
        try {
            redirectUri = new URI(authUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("login/oauth2/code/google")
    public ResponseEntity<?> redirectGoogleLogin(@RequestParam(value = "code") String authCode, HttpServletResponse response) throws JsonProcessingException {
        return googleMemberService.googleLogin(authCode, response);
    }

    private final MemberService memberService;

    // Redirect URI : http://localhost:8080/api/member/login/kakao
    // 로그인 요청 주소 (GET)
    // https://kauth.kakao.com/oauth/authorize?client_id={REST_API_KEY}&redirect_uri={REDIRECT_URI}&response_type=code
    @GetMapping("/api/member/login/kakao")
    public void kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        // authorizedCode: 카카오 서버로부터 받은 인가 코드
        memberService.kakaoLogin(code);
    }

}
