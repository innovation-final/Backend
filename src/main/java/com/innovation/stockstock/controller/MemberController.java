package com.innovation.stockstock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.stockstock.config.GoogleConfigUtils;
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

    @Value("${kakao.restapi.key}")
    private String kakaoKey;
    @Value("${kakao.redirect.uri}")
    private String kakaoUri;

    private final KakaoMemberService kakaoMemberService;
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
    public ResponseEntity<?> redirectGoogleLogin(@RequestParam(value = "code") String authCode, HttpServletResponse response) throws JsonProcessingException, URISyntaxException {
        googleMemberService.googleLogin(authCode, response);
        return goMainPageIfSuccessful();
    }

    @GetMapping("/api/member/login/kakao")
    public ResponseEntity<Object> moveKakaoInitUrl() {
        try {

            URI redirectUri = new URI("https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoKey + "&redirect_uri=" + kakaoUri + "&response_type=code");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("/user/kakao/callback")
    public ResponseEntity<?> redirectKakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException, URISyntaxException {
        kakaoMemberService.kakaoLogin(code, kakaoKey, kakaoUri, response);
        return goMainPageIfSuccessful();
    }

    private ResponseEntity<Object> goMainPageIfSuccessful() throws URISyntaxException {
        String MAIN_PAGE_URL = "http://localhost:8080";
        URI redirectUri = new URI(MAIN_PAGE_URL);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.PERMANENT_REDIRECT);
    }
}
