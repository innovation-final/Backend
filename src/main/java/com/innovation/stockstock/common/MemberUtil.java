package com.innovation.stockstock.common;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.dto.AccountResponseDto;
import com.innovation.stockstock.account.dto.StockHoldingResponseDto;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.account.repository.StockHoldingRepository;
import com.innovation.stockstock.chatRedis.redis.RedisRepository;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MemberUtil {
    private final RedisRepository redisRepository;
    private final StockHoldingRepository stockHoldingRepository;

    public static Member getMember(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }

    // 계좌 수익률 업데이트
    public void updateAccountInfoAtCurrentTime(Account account){ // 사용 시 계좌 null인 경우 고려
        Long accountTotalProfit = 0L;
        Long totalBuyPrice = 0L;
            for (StockHolding stockHolding : account.getStockHoldingsList()) { //stockHoldings이 있는 경우
                int curPrice = Integer.parseInt(redisRepository.getTradePrice(stockHolding.getStockCode()));
                int avgBuying = stockHolding.getAvgBuying();

                Long profit = (long) (curPrice - avgBuying) * stockHolding.getAmount();
                stockHolding.setProfit(profit);

                BigDecimal cur = new BigDecimal(curPrice);
                BigDecimal avgBuy=new BigDecimal(avgBuying);
                float returnRate = cur.subtract(avgBuy).divide(avgBuy, 5, RoundingMode.HALF_EVEN).floatValue();
                stockHolding.setReturnRate(returnRate);

                totalBuyPrice += (long) stockHolding.getAmount() * avgBuying;
                accountTotalProfit +=profit;
            }
            account.setTotalProfit(accountTotalProfit);
            BigDecimal totalProfit = new BigDecimal(accountTotalProfit);
            float returnRate = 0f;
            if(totalBuyPrice!=0L){
                BigDecimal totalBuyingPrice = new BigDecimal(totalBuyPrice);
                returnRate = totalProfit.divide(totalBuyingPrice, 5, RoundingMode.HALF_EVEN).floatValue();
            }
            account.setTotalReturnRate(returnRate);
            }

    // 보유 종목 리스트 수익률 업데이트
    public void updateStockListInfoAtCurrentTime(Account account) {
        List<StockHolding> stockHoldings = stockHoldingRepository.findByAccount(account);
        if (!stockHoldings.isEmpty()){
            for (StockHolding stockHolding : account.getStockHoldingsList()) {
                int curPrice = Integer.parseInt(redisRepository.getTradePrice(stockHolding.getStockCode()));
                int avgBuying = stockHolding.getAvgBuying();

                Long profit = (long) (curPrice - avgBuying) * stockHolding.getAmount();
                stockHolding.setProfit(profit);

                BigDecimal cur = new BigDecimal(curPrice);
                BigDecimal avgBuy = new BigDecimal(avgBuying);

                float returnRate = cur.subtract(avgBuy).divide(avgBuy, 5, RoundingMode.HALF_EVEN).floatValue();
                stockHolding.setReturnRate(returnRate);
            }
        }
    }

}
