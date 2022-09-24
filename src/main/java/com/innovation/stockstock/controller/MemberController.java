package com.innovation.stockstock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.stockstock.dto.ResponseDto;
import com.innovation.stockstock.service.GoogleMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.innovation.stockstock.service.KakaoMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;


@RestController
@RequiredArgsConstructor
public class MemberController {

    @Value("${kakao-restapi-key}")
    private String kakaoKey;
    @Value("${kakao-redirect-url}")
    private String kakaoRedirectUrl;
    private final KakaoMemberService kakaoMemberService;
    private final GoogleMemberService googleMemberService;


    @GetMapping("/api/member/login/google")
    public ResponseEntity<?> redirectGoogleLogin(@RequestParam(value = "code") String authCode, HttpServletResponse response) throws JsonProcessingException {
        googleMemberService.googleLogin(authCode, response);
        return ResponseEntity.ok().body(ResponseDto.success("Google OAuth Success"));
    }

    @GetMapping("/api/member/login/kakao")
    public ResponseEntity<?> redirectKakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        kakaoMemberService.kakaoLogin(code, response);
        return ResponseEntity.ok().body(ResponseDto.success("Kakao OAuth Success"));
    }

    @GetMapping("/api/member/login/kakao/re")
    public ResponseEntity<Object> moveKakaoInitUrl() {
        try {
            URI redirectUri = new URI("https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoKey + "&redirect_uri=" + kakaoRedirectUrl + "&response_type=code");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }


}
