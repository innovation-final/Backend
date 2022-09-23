package com.innovation.stockstock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.stockstock.dto.ResponseDto;
import com.innovation.stockstock.service.GoogleMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.innovation.stockstock.service.KakaoMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
public class MemberController {
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
}
