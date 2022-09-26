package com.innovation.stockstock.service;

import com.innovation.stockstock.ErrorCode;
import com.innovation.stockstock.dto.ResponseDto;
import com.innovation.stockstock.dto.TokenDto;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.entity.RefreshToken;
import com.innovation.stockstock.repository.RefreshTokenRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.security.jwt.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public ResponseEntity<?> reissueJwt(HttpServletRequest request, HttpServletResponse response) {
        //Member member = getMember();

        String refreshToken = request.getHeader("refresh-token");
        Member member;

        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) jwtProvider.getAuthentication(refreshToken).getPrincipal();
            member = userDetails.getMember();
        } catch (ExpiredJwtException e) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.REFRESH_TOKEN_EXPIRED));
        }

        RefreshToken tokenFromDB = refreshTokenRepository.findById(member.getEmail()).orElse(null);

        if (!jwtProvider.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.INVALID_TOKEN));
        } else if (tokenFromDB == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        } else if (!refreshToken.equals(tokenFromDB.getToken())) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.REFRESH_TOKEN_NOT_ALLOWED));
        }

        TokenDto tokenDto = jwtProvider.generateTokenDto(member);
        response.addHeader("Authorization", "BEARER " + tokenDto.getAccessToken());
        response.addHeader("refresh-token", tokenDto.getRefreshToken());

        tokenFromDB.updateToken(tokenDto.getRefreshToken());
        return ResponseEntity.ok().body(ResponseDto.success("Reissue Success"));
    }

    public ResponseDto<?> logout() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        refreshTokenRepository.deleteById(userDetails.getMember().getEmail());
        return ResponseDto.success("Logout Success");
    }
}
