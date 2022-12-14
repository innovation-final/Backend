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
import com.innovation.stockstock.stock.document.Stock;
import com.innovation.stockstock.stock.repository.StockRepository;
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
    private final MemberUtil memberUtil;
    private final StockRepository stockRepository;
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
                .targetReturnRate(accountRequestDto.getTargetReturnRate()/100)
                .expireAt(expiredAt)
                .totalProfit(0L)
                .totalRealizedProfit(0L)
                .totalUnrealizedProfit(0L)
                .member(member)
                .build();

        accountRepository.save(account);
        return ResponseEntity.ok().body(ResponseDto.success("Account opening"));
    }

    @Transactional
    public ResponseEntity<?> getAccount() {
        Member member = MemberUtil.getMember();
        Account account = accountRepository.findByMember(member);
        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        memberUtil.updateAccountInfoAtCurrentTime(account);
        List<StockHolding> stockHoldings = account.getStockHoldingsList();
        List<StockHoldingResponseDto> responseDtoList = new ArrayList<>();

        if (!stockHoldings.isEmpty()) {
            for (StockHolding stockHolding : stockHoldings) {
                StockHoldingResponseDto responseDto = StockHoldingResponseDto.builder()
                        .id(stockHolding.getId())
                        .stockName(stockHolding.getStockName())
                        .profit(stockHolding.getProfit())
                        .returnRate(stockHolding.getReturnRate())
                        .amount(stockHolding.getAmount())
                        .avgBuying(stockHolding.getAvgBuying())
                        .build();
                responseDtoList.add(responseDto);
            }
        }
            AccountResponseDto accountResponseDto = AccountResponseDto.builder()
                    .id(account.getId())
                    .accountNumber(account.getAccountNumber())
                    .seedMoney(account.getSeedMoney())
                    .balance(account.getBalance())
                    .targetReturnRate(account.getTargetReturnRate())
                    .totalProfit(account.getTotalProfit())
                    .totalReturnRate(account.getTotalReturnRate())
                    .totalRealizedProfit(account.getTotalRealizedProfit())
                    .totalUnrealizedProfit(account.getTotalUnrealizedProfit())
                    .expireAt(String.valueOf(account.getExpireAt()))
                    .stockHoldingsList(responseDtoList)
                    .createdAt(String.valueOf(account.getCreatedAt()))
                    .build();

            return ResponseEntity.ok().body(ResponseDto.success(accountResponseDto));
        }

    @Transactional // ?????? ?????? ??????
    public ResponseEntity<?> getReturn() {
        Member member = MemberUtil.getMember();
        Account account = accountRepository.findByMember(member);

        if(account == null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        List<StockHoldingResponseDto> responseDtoList = new ArrayList<>();
        for (StockHolding stockHolding : account.getStockHoldingsList()) {
            int curPrice = 0;
            try {
                curPrice = Integer.parseInt(redisRepository.getTradePrice(stockHolding.getStockCode()));
            }catch (Exception e){
                Stock stock = stockRepository.findByCode(stockHolding.getStockCode());
                Map<String, String> current = stock.getCurrent();
                curPrice = Integer.valueOf(current.get("last_price"));
            }
            int avgBuying = stockHolding.getAvgBuying();

            Long profit = (long) (curPrice - avgBuying) *stockHolding.getAmount();
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
                    .avgBuying(stockHolding.getAvgBuying())
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
