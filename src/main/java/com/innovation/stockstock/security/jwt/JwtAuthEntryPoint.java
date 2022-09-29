package com.innovation.stockstock.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovation.stockstock.ErrorCode;
import com.innovation.stockstock.dto.response.ResponseDto;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Object invalidJwt = request.getAttribute("INVALID_JWT");
        Object expiredJwt = request.getAttribute("EXPIRED_JWT");

        if (invalidJwt != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setStatus(400);

            ResponseDto<?> msg = ResponseDto.fail(ErrorCode.INVALID_TOKEN);

            String result = objectMapper.writeValueAsString(msg);
            response.getWriter().write(result);
        } else if (expiredJwt != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.setStatus(401);

            ResponseDto<?> msg = ResponseDto.fail(ErrorCode.ACCESS_TOKEN_EXPIRED);

            String result = objectMapper.writeValueAsString(msg);
            response.getWriter().write(result);
        }
    }
}
