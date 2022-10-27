package com.innovation.stockstock.member.mypage;

import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.mypage.dto.ProfileRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
    public ResponseEntity<?> changeProfile(HttpServletRequest request, @ModelAttribute @Valid ProfileRequestDto requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.MAX_SIZE_OVER));
        }
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
        log.info("MaxUploadSizeExceededException", e);
        return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.FILE_SIZE_EXCEED));
    }
}
