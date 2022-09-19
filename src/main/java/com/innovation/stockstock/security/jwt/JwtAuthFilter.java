package com.innovation.stockstock.security.jwt;

import com.innovation.stockstock.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            TokenDto jwt = getJwtFromRequest(request);
            assert jwt != null;
            validateToken(request, jwt.getAccessToken(), jwt.getRefreshToken());
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private void validateToken(HttpServletRequest request, String accessToken, String refreshToken) {
        if (accessToken != null && jwtProvider.validateToken(accessToken) && jwtProvider.validateToken(refreshToken)) {
            Authentication auth = jwtProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            request.setAttribute("INVALID_JWT", "INVALID_JWT");
        }
    }

    private TokenDto getJwtFromRequest(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("refresh-token");
        if (refreshToken != null && accessToken.startsWith("BEARER ")) {
            return new TokenDto(accessToken.substring("BEARER ".length()), refreshToken);
        }
        return null;
    }
}
