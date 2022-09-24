package com.innovation.stockstock.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
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
            String path = request.getServletPath();
            if (path.startsWith("/api/auth/reissue")) {
                filterChain.doFilter(request, response);
            } else {
                String accessToken = getAccessTokenFromRequest(request);
                if (accessToken != null && jwtProvider.validateToken(accessToken)) {
                    Authentication auth = jwtProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    request.setAttribute("INVALID_JWT", "INVALID_JWT");
                }
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute("EXPIRED_JWT", "EXPIRED_JWT");
            logger.error("Could not set user authentication in security context", e);
        } catch (NullPointerException e) {
            logger.error("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if (accessToken.startsWith("BEARER ")) {
            return accessToken.substring("BEARER ".length());
        }
        return null;
    }
}
