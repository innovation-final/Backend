package com.innovation.stockstock.member.mypage;

import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.mypage.dto.ProfileRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

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
    public ResponseEntity<?> changeProfile(HttpServletRequest request, @ModelAttribute @Valid ProfileRequestDto requestDto) {
        return myPageService.changeProfile(request, requestDto);
    }

    @DeleteMapping("/api/auth/mypage")
    public ResponseEntity<?> deleteMyAccount(HttpServletRequest request) {
        return ResponseEntity.ok().body(myPageService.deleteMyAccount(request));
    }

    @GetMapping("/api/profile/{memberId}")
    public ResponseEntity<?> getInfoOther(@PathVariable Long memberId){
        return myPageService.getInfoOther(memberId);
    }

    @ExceptionHandler({SizeLimitExceededException.class,MaxUploadSizeExceededException.class})
    protected ResponseEntity<?> MaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.FILE_SIZE_EXCEED));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<?> handleProfileChangeException(ConstraintViolationException e) {
        String errMsg = e.getMessage();
        ErrorCode errorCode = null;
        if (errMsg.contains("20") && errMsg.contains("50")) {
            errorCode = ErrorCode.BOTH_SIZE_OVER;
        } else if (errMsg.contains("20")) {
            errorCode = ErrorCode.NICKNAME_SIZE_OVER;
        } else if (errMsg.contains("50")) {
            errorCode = ErrorCode.PROFILE_MSG_SIZE_OVER;
        }
        return ResponseEntity.badRequest().body(ResponseDto.fail(errorCode));
    }
}
