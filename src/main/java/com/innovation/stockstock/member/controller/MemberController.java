package com.innovation.stockstock.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.service.MemberService;
import com.innovation.stockstock.member.oauth.service.GoogleMemberService;
import com.innovation.stockstock.member.oauth.service.KakaoMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
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

    @PostMapping("/api/auth/reissue")
    public ResponseEntity<?> reissueJwt(HttpServletRequest request, HttpServletResponse response) {
        return memberService.reissueJwt(request, response);
    }

    @DeleteMapping ("/api/auth/member/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().body(memberService.logout());
    }
}
