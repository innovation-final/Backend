package com.innovation.stockstock.member.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoMemberInfoDto {
    private String nickname;
    private String email;
}
