package com.innovation.stockstock.common;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.repository.StockHoldingRepository;
import com.innovation.stockstock.chatRedis.redis.RedisRepository;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.stock.document.Stock;
import com.innovation.stockstock.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class MemberUtil {
    private final RedisRepository redisRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private final StockRepository stockRepository;

    public static Member getMember(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }

    // 계좌 수익률 업데이트
    public void updateAccountInfoAtCurrentTime(Account account) { // 사용 시 계좌 null인 경우 고려
        // 미실현손익(보유주식현재가 - 보유주식매수가)
        Long accountUnrealizedProfit = 0L;
        List<StockHolding> stockHoldings = account.getStockHoldingsList();

        if (!stockHoldings.isEmpty()) {
            for (StockHolding stockHolding : stockHoldings) {
                // 보유량 * (현재가 - 평균매수가)
                int holdingAmount = stockHolding.getAmount();
                int curPrice = 0;
                try {
                    curPrice = Integer.parseInt(redisRepository.getTradePrice(stockHolding.getStockCode()));
                } catch (Exception e) {
                    Stock stock = stockRepository.findByCode(stockHolding.getStockCode());
                    Map<String, String> current = stock.getCurrent();
                    curPrice = Integer.valueOf(current.get("last_price"));
                }

                int avgBuying = stockHolding.getAvgBuying();

                // 종목별 수익과 수익률
                Long profit = Long.valueOf((curPrice - avgBuying) * holdingAmount);
                stockHolding.setProfit(profit);

                BigDecimal cur = new BigDecimal(curPrice);
                BigDecimal avgBuy = new BigDecimal(avgBuying);
                float returnRate = cur.subtract(avgBuy).divide(avgBuy, 5, RoundingMode.HALF_EVEN).floatValue();
                stockHolding.setReturnRate(returnRate);

                accountUnrealizedProfit += profit;
            }
        }

        account.setTotalUnrealizedProfit(accountUnrealizedProfit);

        // 실현손익(매도주식매도가총계 - 매도주식매수가총계) = 잔고 - 씨드머니 + 보유주식매수가
        // 실현손익률  = 실현손익 / 매도주식매수가총계 = account.getBalance() - account.getSeedMoney() + totalHoldingBuyingPrice;
        Long accountRealizedProfit = account.getTotalRealizedProfit(); // 매도할 때마다 집계 중

        account.setTotalRealizedProfit(accountRealizedProfit);

        Long accountTotalProfit = accountRealizedProfit + accountUnrealizedProfit;
        int accountTotalExpense = account.getSeedMoney();

        account.setTotalProfit(accountTotalProfit);

        if (accountTotalExpense != 0) {
            BigDecimal totalProfit = new BigDecimal(accountTotalProfit);
            BigDecimal totalExpense = new BigDecimal(accountTotalExpense);
            float accountTotalReturnRate = totalProfit.divide(totalExpense, 5, RoundingMode.HALF_EVEN).floatValue();
            account.setTotalReturnRate(accountTotalReturnRate);
        }
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
