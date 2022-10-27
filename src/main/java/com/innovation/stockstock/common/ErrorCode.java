package com.innovation.stockstock.common;

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

    // 닉네임 길이 초과
    MAX_SIZE_OVER("MAX_SIZE_OVER", "Over Maximum Length(6)"),

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
    FILE_SIZE_EXCEED("FILE_SIZE_EXCEED","File size exceeded. Limit is 1MB."),

    // 중복 계좌 개설 불가
    NOT_DUPLICATES("ONE_ACCOUNT_PER_PERSON","Duplicate account opening is not allowed."),

    // 매수 주문 불가: 잔액 초과
    BUY_ORDER_FAIL("ORDER_FAIL", "Over Balance"),

    // 매도 주문 불가: 보유 갯수 초과
    SELL_ORDER_FAIL("ORDER_FAIL", "Over Holding Amount"),

    // 계좌 없음
    NO_ACCOUNT("ORDER_FAIL", "Has No Account to Order"),

    // 보유하지 않은 종목
    NO_OWNER("ORDER_FAIL", "Not Owner of the Stock"),

    // 장외 주문 불가
    OUT_OF_MARKET_HOUR("OUT_OF_MARKET_HOUR", "Not Allowed to Order at Current Time");

    private final String code;
    private final String message;
}
