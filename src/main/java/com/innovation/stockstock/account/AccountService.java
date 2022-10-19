package com.innovation.stockstock.account;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.dto.AccountRequestDto;
import com.innovation.stockstock.account.dto.AccountResponseDto;
import com.innovation.stockstock.account.dto.AccountUpdateRequestDto;
import com.innovation.stockstock.account.dto.StockHoldingResponseDto;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.chatRedis.redis.RedisRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.MemberUtil;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final RedisRepository redisRepository;
    public ResponseEntity<?> makeAccount(AccountRequestDto accountRequestDto) {
        Member member = MemberUtil.getMember();
        if (accountRepository.findByMember(member)!=null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_DUPLICATES));
        }

        LocalDateTime expiredAt = LocalDateTime.now().plusDays(accountRequestDto.getExpireAt());

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

    @Transactional
    public ResponseEntity<?> getAccount() {
        Member member = MemberUtil.getMember();
        Account account = accountRepository.findByMember(member);
        if(account == null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        List<StockHoldingResponseDto> responseDtoList = new ArrayList<>();

        Long accountTotalProfit = 0L;
        Long totalBuyPrice = 0L;

        if (!account.getStockHoldingsList().isEmpty()){
            for (StockHolding stockHolding : account.getStockHoldingsList()) {
                int curPrice = Integer.parseInt(redisRepository.getTradePrice(stockHolding.getStockCode()));
                int avgBuying = stockHolding.getAvgBuying();

                Long profit = Long.valueOf((curPrice - avgBuying) *stockHolding.getAmount());
                stockHolding.setProfit(profit);

                BigDecimal cur = new BigDecimal(curPrice);
                BigDecimal avgBuy=new BigDecimal(avgBuying);
                float returnRate = cur.subtract(avgBuy).divide(avgBuy, 5, RoundingMode.HALF_EVEN).floatValue();
                stockHolding.setReturnRate(returnRate);

                StockHoldingResponseDto responseDto = StockHoldingResponseDto.builder()
                        .id(stockHolding.getId())
                        .stockName(stockHolding.getStockName())
                        .profit(stockHolding.getProfit())
                        .returnRate(stockHolding.getReturnRate())
                        .amount(stockHolding.getAmount())
                        .build();
                responseDtoList.add(responseDto);

                totalBuyPrice += stockHolding.getAmount() * avgBuying;
                accountTotalProfit +=profit;
            }
            account.setTotalProfit(accountTotalProfit);
            BigDecimal totalProfit = new BigDecimal(accountTotalProfit);
            BigDecimal totalBuyingPrice = new BigDecimal(totalBuyPrice);
            float returnRate = totalProfit.divide(totalBuyingPrice, 5, RoundingMode.HALF_EVEN).floatValue();
            account.setTotalReturnRate(returnRate);
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
                //.member(account.getMember())
        .build();
        System.out.println(accountResponseDto);
        return ResponseEntity.ok().body(ResponseDto.success(accountResponseDto));
    }

    @Transactional // 보유 종목 정보
    public ResponseEntity<?> getReturn() {
        Member member = MemberUtil.getMember();
        Account account = accountRepository.findByMember(member);

        if(account == null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        List<StockHoldingResponseDto> responseDtoList = new ArrayList<>();
        for (StockHolding stockHolding : account.getStockHoldingsList()) {
            int curPrice = Integer.parseInt(redisRepository.getTradePrice(stockHolding.getStockCode()));
            int avgBuying = stockHolding.getAvgBuying();

            Long profit = (long) (curPrice - avgBuying) *stockHolding.getAmount();
            stockHolding.setProfit(profit);

            BigDecimal cur = new BigDecimal(curPrice);
            BigDecimal avgBuy=new BigDecimal(avgBuying);
            float returnRate = cur.subtract(avgBuy).divide(avgBuy, 5, RoundingMode.HALF_EVEN).floatValue();
            // double returnRate = (curPrice-avgBuying)/avgBuying;
            stockHolding.setReturnRate(returnRate);

            StockHoldingResponseDto responseDto = StockHoldingResponseDto.builder()
                    .id(stockHolding.getId())
                    .stockName(stockHolding.getStockName())
                    .profit(stockHolding.getProfit())
                    .returnRate(stockHolding.getReturnRate())
                    .amount(stockHolding.getAmount())
                    .build();
            responseDtoList.add(responseDto);
        }
        return ResponseEntity.ok().body(ResponseDto.success(responseDtoList));
    }

    @Transactional
    public ResponseEntity<?> updateAccount(AccountUpdateRequestDto requestDto) {
        Member member = MemberUtil.getMember();
        Account account = accountRepository.findByMember(member);
        if(account == null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        } else if (!account.getMember().getId().equals(member.getId())) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NOT_ALLOWED));
        } else {
            if(requestDto.getExpireAt()!=0){
                account.updateExpiredAt(requestDto.getExpireAt());
            }
            return ResponseEntity.ok().body(ResponseDto.success("Account Info Update Success"));
        }
    }
}