package com.innovation.stockstock.account;

import com.innovation.stockstock.account.dto.AccountRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // 계좌 생성
    @PostMapping("/api/auth/account")
    public ResponseEntity<?> makeAccount(@RequestBody AccountRequestDto accountRequestDto){
        return accountService.makeAccount(accountRequestDto);
    }

    // 계좌 정보 조회
    @GetMapping("/api/auth/account")
    public ResponseEntity<?> getAccount(){
        return accountService.getAccount();
    }

    // 보유 종목 정보 조회
    @GetMapping("/api/auth/account/stocks")
    public ResponseEntity<?> getReturn(){
        return accountService.getReturn();
    }

    // 계좌 정보 수정
    @PatchMapping("/api/auth/account")
    public ResponseEntity<?> updateAccount(@RequestBody AccountRequestDto requestDto){
        return accountService.updateAccount(requestDto);
    }
}
