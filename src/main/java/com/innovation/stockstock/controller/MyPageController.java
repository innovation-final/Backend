package com.innovation.stockstock.controller;

import com.innovation.stockstock.ErrorCode;
import com.innovation.stockstock.dto.request.ProfileRequestDto;
import com.innovation.stockstock.dto.response.ResponseDto;
import com.innovation.stockstock.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController
@ControllerAdvice
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;


    @GetMapping("/api/auth/mypage")
    public ResponseEntity<?> getMyProfile(HttpServletRequest request) {
        return ResponseEntity.ok().body(myPageService.getMyProfile(request));
    }

    @PatchMapping("/api/auth/mypage")
    public ResponseEntity<?> changeProfile(HttpServletRequest request, @ModelAttribute ProfileRequestDto requestDto) {
        return myPageService.changeProfile(request, requestDto);
    }

    @DeleteMapping("/api/auth/mypage")
    public ResponseEntity<?> deleteMyAccount(HttpServletRequest request) {
        return ResponseEntity.ok().body(myPageService.deleteMyAccount(request));
    }

    @ExceptionHandler({SizeLimitExceededException.class,MaxUploadSizeExceededException.class})
    protected ResponseEntity<?> MaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.info("MaxUploadSizeExceededException", e);
        return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.FILE_SIZE_EXCEED));
    }

}
