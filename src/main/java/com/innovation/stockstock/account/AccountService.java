package com.innovation.stockstock.account;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.dto.AccountRequestDto;
import com.innovation.stockstock.account.dto.AccountResponseDto;
import com.innovation.stockstock.account.dto.StockHoldingResponseDto;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.account.repository.StockHoldingRepository;
import com.innovation.stockstock.chatRedis.redis.RedisRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.order.repository.BuyOrderRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.stock.repository.StockListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final BuyOrderRepository buyOrderRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private final StockListRepository stockListRepository;

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

    @Transactional
    public ResponseEntity<?> getAccount() {
        Member member = getMember();
        List<StockHoldingResponseDto> responseDtoList = new ArrayList<>();
        Account account = accountRepository.findByMember(member);
        if(account == null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        Long accountTotalProfit = 0L;
        Long totalBuyPrice = 0L;
        for (StockHolding stockHolding : account.getStockHoldingsList()) {
            if(stockHolding.getAmount()==0){
                stockHoldingRepository.deleteById(stockHolding.getId());
                continue;
            }
            int curPrice = Integer.parseInt(redisRepository.getTradePrice(stockHolding.getStockCode()));
            int amount = buyOrderRepository.sumBuyAmount(stockHolding);
            Long avgBuying = buyOrderRepository.sumBuyPrice(stockHolding)/amount;
            Long profit = Long.valueOf((curPrice - avgBuying) *stockHolding.getAmount());
            stockHolding.setProfit(profit);
            // float returnRate = Float.valueOf((curPrice-avgBuying)/avgBuying)-1;
            totalBuyPrice+=stockHolding.getAmount()*avgBuying;
            BigDecimal cur = new BigDecimal(curPrice);
            BigDecimal avgBuy=new BigDecimal(avgBuying);
            float returnRate = cur.subtract(avgBuy).divide(avgBuy, 5, RoundingMode.HALF_EVEN).floatValue();
            stockHolding.setReturnRate(returnRate);

            accountTotalProfit +=profit;

            StockHoldingResponseDto responseDto = StockHoldingResponseDto.builder()
                    .id(stockHolding.getId())
                    .stockName(stockHolding.getStockName())
                    .profit(stockHolding.getProfit())
                    .returnRate(stockHolding.getReturnRate())
                    .amount(stockHolding.getAmount())
                    .build();

            responseDtoList.add(responseDto);
        }

        account.setTotalProfit(accountTotalProfit);
        BigDecimal totalProfit = new BigDecimal(accountTotalProfit);

        if(totalBuyPrice==0){
            account.setTotalReturnRate(0);
        }else{
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
                .member(account.getMember()).build();

    return ResponseEntity.ok().body(ResponseDto.success(accountResponseDto));
    }

    @Transactional // 보유 종목 정보
    public ResponseEntity<?> getReturn() {
        Member member = getMember();
        List<StockHoldingResponseDto> responseDtoList = new ArrayList<>();
        Account account = accountRepository.findByMember(member);
        if(account == null){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        for (StockHolding stockHolding : account.getStockHoldingsList()) {
            int curPrice = Integer.parseInt(redisRepository.getTradePrice(stockHolding.getStockCode()));
            int avgBuying = stockHolding.getAvgBuying();
            Long profit = Long.valueOf((curPrice - avgBuying) *stockHolding.getAmount());
            stockHolding.setProfit(profit);

            // float returnRate = Float.valueOf((curPrice-avgBuying)/avgBuying); // 종목별 손익률 = (현재가격 - 평균 매수가) / 평균매수가

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
        }
        return ResponseEntity.ok().body(ResponseDto.success(responseDtoList));
    }

    public Member getMember(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }
}
