package com.innovation.stockstock.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ProfileRequestDto {
    private String nickname;
    private MultipartFile profileImg;
    private String profileMsg;
}
