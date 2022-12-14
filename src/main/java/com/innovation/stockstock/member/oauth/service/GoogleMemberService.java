package com.innovation.stockstock.member.oauth.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.innovation.stockstock.member.oauth.dto.GoogleLoginDto;
import com.innovation.stockstock.member.oauth.vo.GoogleLoginRequestVo;
import com.innovation.stockstock.member.oauth.vo.GoogleLoginResponseVo;
import com.innovation.stockstock.member.oauth.util.GoogleConfigUtils;
import com.innovation.stockstock.security.jwt.RefreshToken;
import com.innovation.stockstock.security.jwt.RefreshTokenRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.security.jwt.JwtProvider;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.member.repository.MemberRepository;
import com.innovation.stockstock.security.jwt.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class GoogleMemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GoogleConfigUtils googleConfigUtils;
    private final JwtProvider jwtProvider;

    public void googleLogin(String authCode, HttpServletResponse response) throws JsonProcessingException {
        GoogleLoginDto userInfo = getGoogleUserInfo(authCode);

        Member googleUser = signupGoogleUserIfNeeded(userInfo);

        forceLogin(googleUser);

        String refreshToken = sendJwt(response, googleUser);

        refreshTokenRepository.save(new RefreshToken(googleUser, refreshToken));
    }

    private GoogleLoginDto getGoogleUserInfo(String authCode) throws JsonProcessingException {
        // HTTP ????????? ?????? RestTemplate ??????
        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginRequestVo requestParams = GoogleLoginRequestVo.builder()
                .clientId(googleConfigUtils.getGoogleClientId())
                .clientSecret(googleConfigUtils.getGoogleSecret())
                .code(authCode)
                .redirectUri(googleConfigUtils.getGoogleRedirectUri())
                .grantType("authorization_code")
                .build();

        // Http Header ??????
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GoogleLoginRequestVo> httpRequestEntity = new HttpEntity<>(requestParams, headers);
        ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(googleConfigUtils.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);

        // ObjectMapper??? ?????? String to Object??? ??????
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL??? ?????? ?????? ????????????(NULL??? ????????? ??????)
        GoogleLoginResponseVo googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<>() {});

        // ???????????? ????????? JWT Token?????? ???????????? ??????, Id_Token??? ?????? ????????????.
        String jwtToken = googleLoginResponse.getIdToken();

        // JWT Token??? ????????? JWT ????????? ????????? ?????? ??????
        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleConfigUtils.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();

        String resultJson = restTemplate.getForObject(requestUrl, String.class);
        GoogleLoginDto userInfoDto = objectMapper.readValue(resultJson, new TypeReference<>() {});

        return userInfoDto;
    }
    private Member signupGoogleUserIfNeeded(GoogleLoginDto userInfo) {
        String email = userInfo.getEmail();
        Member googleUser = memberRepository.findByEmail(email).orElse(null);

        if (googleUser == null) { // ????????????
            String nickname = userInfo.getName();

            googleUser = new Member(email, nickname);
            memberRepository.save(googleUser);
        }
        return googleUser;
    }

    private void forceLogin(Member googleUser) {
        UserDetails userDetails = new UserDetailsImpl(googleUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String sendJwt(HttpServletResponse response, Member googleUser) {
        TokenDto tokenDto = jwtProvider.generateTokenDto(googleUser);
        response.addHeader("Authorization","BEARER " + tokenDto.getAccessToken());
        response.addHeader("refresh-token",tokenDto.getRefreshToken());

        return tokenDto.getRefreshToken();
    }
}
