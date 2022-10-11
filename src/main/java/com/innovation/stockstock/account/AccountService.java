package com.innovation.stockstock.account;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.dto.AccountRequestDto;
import com.innovation.stockstock.account.dto.AccountResponseDto;
import com.innovation.stockstock.account.dto.StockHoldingResponseDto;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public ResponseEntity<?> makeAccount(AccountRequestDto accountRequestDto) {
        Member member = getMember();
        if (accountRepository.findByMember(member)!=null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_DUPLICATES));
        };
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusDays(accountRequestDto.getExpireAt());
        Account account = Account.builder()
                .accountNumber(member.getId()+System.currentTimeMillis())
                .deposit(Long.valueOf(accountRequestDto.getDeposit()))
                .targetReturnRate(accountRequestDto.getTargetReturnRate())
                .totalReturnRate(0F)
                .totalProfit(0L)
                .expireAt(expiredAt)
                .member(member)
                .build();
        accountRepository.save(account);
        return ResponseEntity.ok().body(ResponseDto.success("Account opening"));
    }

    @Transactional // 지연로딩 해결
    public ResponseEntity<?> balance() {
        Member member = getMember();
        List<StockHoldingResponseDto> responseDtoList = new ArrayList<>();
        Account account = accountRepository.findByMember(member);
        if(account == null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        for (StockHolding stockHolding : account.getStockHoldingsList()) {
            StockHoldingResponseDto responseDto = StockHoldingResponseDto.builder()
                    .id(stockHolding.getId())
                    .stockCode(stockHolding.getStockCode())
                    .targetReturnRate(stockHolding.getTargetReturnRate())
                    .returnRate(stockHolding.getReturnRate())
                    .profit(stockHolding.getProfit())
                    .build();
            responseDtoList.add(responseDto);
        }
        AccountResponseDto accountResponseDto = AccountResponseDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .deposit(account.getDeposit())
                .targetReturnRate(account.getTargetReturnRate())
                .totalReturnRate(account.getTotalReturnRate())
                .totalProfit(account.getTotalProfit())
                .expireAt(String.valueOf(account.getExpireAt()))
                .stockHoldingsList(responseDtoList)
                .createdAt(String.valueOf(account.getCreatedAt()))
                .member(account.getMember()).build();

    return ResponseEntity.ok().body(ResponseDto.success(accountResponseDto));
    }

    public Member getMember(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }
}
