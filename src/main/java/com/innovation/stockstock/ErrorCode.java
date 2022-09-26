package com.innovation.stockstock;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.OBJECT;

@JsonFormat(shape = OBJECT)
@AllArgsConstructor
@Getter
public enum ErrorCode {
    //요청 위치에 리소스 없음
    NULL_ID("NULL_ID", "Id Doesn't Exist"),

    //유효하지 않은 토큰
    INVALID_TOKEN("INVALID_TOKEN", "Invalid or No Token"),

    // refresh-token DB에 없음
    REFRESH_TOKEN_NOT_FOUND("REFRESH_TOKEN_NOT_FOUND", "No Such Refresh Token"),

    // DB의 refresh-token과 보낸 토큰이 다름
    REFRESH_TOKEN_NOT_ALLOWED("REFRESH_TOKEN_NOT_ALLOWED", "Refresh Token Different"),

    // access-token 만료
    ACCESS_TOKEN_EXPIRED("ACCESS_TOKEN_EXPIRED", "Access Token Expired"),

    // refresh-token 만료
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED", "Refresh Token Expired"),

    //유저의 권한 없는 요청
    NOT_ALLOWED("NOT_ALLOWED", "Only The Writer Is Allowed"),

    // 용량 초과
    FILE_SIZE_EXCEED("FILE_SIZE_EXCEED","File size exceeded. Limit is 20KB.");
    private final String code;
    private final String message;
}
