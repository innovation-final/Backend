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
    @PatchMapping("/api/auth/account/make")
    public ResponseEntity<?> makeAccount(@RequestBody AccountRequestDto accountRequestDto){
        return ResponseEntity.ok(accountService.makeAccount(accountRequestDto));
    }

    // 계좌 정보 조회
    @GetMapping("/api/auth/account/balance")
    public ResponseEntity<?> getBalance(){
        return ResponseEntity.ok(accountService.balance());
    }

}
