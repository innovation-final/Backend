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

    //유저의 권한 없는 요청
    NOT_ALLOWED("NOT_ALLOWED", "Only The Writer Is Allowed");

    private final String code;
    private final String message;
}
