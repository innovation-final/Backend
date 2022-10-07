package com.innovation.stockstock.member.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ProfileRequestDto {
    private String nickname;
    private MultipartFile profileImg;
    private String profileMsg;
}
