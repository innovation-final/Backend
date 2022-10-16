package com.innovation.stockstock.account;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.dto.AccountRequestDto;
import com.innovation.stockstock.account.dto.AccountResponseDto;
import com.innovation.stockstock.account.dto.StockHoldingResponseDto;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.chatRedis.redis.RedisRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.order.repository.BuyOrderRepository;
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
    private final RedisRepository redisRepository;
    private final BuyOrderRepository buyOrderRepository;

    public ResponseEntity<?> makeAccount(AccountRequestDto accountRequestDto) {
        Member member = getMember();
        if (accountRepository.findByMember(member)!=null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_DUPLICATES));
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusDays(accountRequestDto.getExpireAt());
        Account account = Account.builder()
                .accountNumber(member.getId()+System.currentTimeMillis())
                .seedMoney(accountRequestDto.getSeedMoney())
                .balance((long) accountRequestDto.getSeedMoney())
                .targetReturnRate(accountRequestDto.getTargetReturnRate())
                .totalReturnRate(0f)
                .totalProfit(0L)
                .expireAt(expiredAt)
                .member(member)
                .build();
        accountRepository.save(account);
        return ResponseEntity.ok().body(ResponseDto.success("Account opening"));
    }

    @Transactional // 지연로딩 해결
    public ResponseEntity<?> getAccount() {
        Member member = getMember();
        List<StockHoldingResponseDto> responseDtoList = new ArrayList<>();
        Account account = accountRepository.findByMember(member);
        if(account == null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        Long accountTotalProfit = 0L;

        for (StockHolding stockHolding : account.getStockHoldingsList()) {
            Long curPrice = (Long) redisRepository.getTradePrice(stockHolding.getStockCode());

            Long totalBuyPrice = buyOrderRepository.sumBuyPrice(stockHolding);
            Long avgBuy = totalBuyPrice/stockHolding.getAmount();
            Long profit = (curPrice - avgBuy) * stockHolding.getAmount();
            stockHolding.setProfit(profit);

            accountTotalProfit +=profit;

            float returnRate = (curPrice-avgBuy)/avgBuy-1;
            stockHolding.setReturnRate(returnRate);

            StockHoldingResponseDto responseDto = StockHoldingResponseDto.builder()
                    .id(stockHolding.getId())
                    .stockCode(stockHolding.getStockCode())
                    .targetReturnRate(stockHolding.getTargetReturnRate())
                    .profit(stockHolding.getProfit())
                    .returnRate(stockHolding.getReturnRate())
                    .build();

            responseDtoList.add(responseDto);
        }

        // 계좌 손익 : 종목별 손익 합산
        account.setTotalProfit(accountTotalProfit);
        // 계좌 손익률 : 총 손익 / deposit - 1
        float accountTotalReturnRate = (accountTotalProfit-account.getSeedMoney())/account.getSeedMoney()-1;
        if(accountTotalReturnRate==-2){
            account.setTotalReturnRate(0);
        }else{
            account.setTotalReturnRate(accountTotalReturnRate);
        }
        AccountResponseDto accountResponseDto = AccountResponseDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .seedMoney(account.getSeedMoney())
                .balance(account.getBalance())
                .targetReturnRate(account.getTargetReturnRate())
                .totalReturnRate(account.getTotalReturnRate())
                .totalProfit(account.getTotalProfit())
                .expireAt(String.valueOf(account.getExpireAt()))
                .stockHoldingsList(responseDtoList)
                .createdAt(String.valueOf(account.getCreatedAt()))
                .member(account.getMember()).build();

    return ResponseEntity.ok().body(ResponseDto.success(accountResponseDto));
    }

    public ResponseEntity<?> getReturn() {
        Member member = getMember();
        List<StockHoldingResponseDto> responseDtoList = new ArrayList<>();
        Account account = accountRepository.findByMember(member);
        if(account == null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        for (StockHolding stockHolding : account.getStockHoldingsList()) {
            Long curPrice = (Long) redisRepository.getTradePrice(stockHolding.getStockCode());

            // 종목별 손익 : (현재가 - 평균 매수가) * 총 보유 수량
            // 평균 매수가 : 매수수량 * 매수가격 / 총 보유 수량
            Long totalBuyPrice = buyOrderRepository.sumBuyPrice(stockHolding);
            Long avgBuy = totalBuyPrice/stockHolding.getAmount();
            Long profit = (curPrice - avgBuy) * stockHolding.getAmount();
            stockHolding.setProfit(profit);

            // 종목별 손익률 = (현재가격 - 평균 매수가) / 평균매수가 - 1
            float returnRate = (curPrice-avgBuy)/avgBuy-1;
            stockHolding.setReturnRate(returnRate);

            StockHoldingResponseDto responseDto = StockHoldingResponseDto.builder()
                    .id(stockHolding.getId())
                    .stockCode(stockHolding.getStockCode())
                    .targetReturnRate(stockHolding.getTargetReturnRate())
                    .profit(stockHolding.getProfit())
                    .returnRate(stockHolding.getReturnRate())
                    .build();

            responseDtoList.add(responseDto);
        }
        return ResponseEntity.ok().body(ResponseDto.success(responseDtoList));
    }

    public Member getMember(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }
}
