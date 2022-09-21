package com.innovation.stockstock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.stockstock.config.GoogleConfigUtils;
import com.innovation.stockstock.dto.ResponseDto;
import com.innovation.stockstock.service.GoogleMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import com.innovation.stockstock.service.KakaoMemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MemberController {

    @Value("${kakao-restapi-key}")
    private String kakaoKey;
    @Value("${kakao-redirect-url}")
    private String kakaoRedirectUrl;
    private final KakaoMemberService kakaoMemberService;
    private final GoogleMemberService googleMemberService;
    private final GoogleConfigUtils googleConfigUtils;


    //@GetMapping(value = "/api/member/login/google")
    //public ResponseEntity<Object> moveGoogleInitUrl() {
    //    String authUrl = googleConfigUtils.googleInitUrl();
    //    URI redirectUri;
    //    try {
    //        redirectUri = new URI(authUrl);
    //        HttpHeaders httpHeaders = new HttpHeaders();
    //        httpHeaders.setLocation(redirectUri);
    //        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    //    } catch (URISyntaxException e) {
    //        e.printStackTrace();
    //    }
    //
    //    return ResponseEntity.badRequest().build();
    //}

    @GetMapping("login/oauth2/code/google")
    public ResponseEntity<?> redirectGoogleLogin(@RequestParam(value = "code") String authCode, HttpServletResponse response) throws JsonProcessingException {
        googleMemberService.googleLogin(authCode, response);
        return ResponseEntity.ok().body(ResponseDto.success("Google OAuth Success"));
    }

    //@GetMapping("/api/member/login/kakao")
    //public ResponseEntity<Object> moveKakaoInitUrl() {
    //    try {
    //        URI redirectUri = new URI("https://kauth.kakao.com/oauth/authorize?client_id=" + kakaoKey + "&redirect_uri=" + kakaoRedirectUrl + "&response_type=code");
    //        HttpHeaders httpHeaders = new HttpHeaders();
    //        httpHeaders.setLocation(redirectUri);
    //        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    //    } catch (URISyntaxException e) {
    //        e.printStackTrace();
    //    }
    //    return ResponseEntity.badRequest().build();
    //}
    @GetMapping("/api/user/callback")
    public ResponseEntity<?> redirectKakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        kakaoMemberService.kakaoLogin(code, kakaoKey, kakaoRedirectUrl, response);
        return ResponseEntity.ok().body(ResponseDto.success("Kakao OAuth Success"));
    }

}
