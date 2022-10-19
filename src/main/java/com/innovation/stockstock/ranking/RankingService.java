package com.innovation.stockstock.ranking;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.chatRedis.redis.RedisRepository;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.order.repository.BuyOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final AccountRepository accountRepository;
    private final RedisRepository redisRepository;
    private final BuyOrderRepository buyOrderRepository;

    public ResponseDto<?> getReturnRank() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            Long accountTotalProfit = 0L;
            Long totalBuyPrice = 0L;
            for (StockHolding stockHolding : account.getStockHoldingsList()) {
                int curPrice = Integer.parseInt(redisRepository.getTradePrice(stockHolding.getStockCode()));
                int amount = buyOrderRepository.sumBuyAmount(stockHolding);
                Long avgBuying = buyOrderRepository.sumBuyPrice(stockHolding) / amount;
                Long profit = Long.valueOf((curPrice - avgBuying) * stockHolding.getAmount());
                stockHolding.setProfit(profit);
                // float returnRate = Float.valueOf((curPrice-avgBuying)/avgBuying)-1;
                totalBuyPrice += stockHolding.getAmount() * avgBuying;
                BigDecimal cur = new BigDecimal(curPrice);
                BigDecimal avgBuy = new BigDecimal(avgBuying);
                float returnRate = cur.subtract(avgBuy).divide(avgBuy, 5, RoundingMode.HALF_EVEN).floatValue();
                stockHolding.setReturnRate(returnRate);

                accountTotalProfit += profit;
            }
            account.setTotalProfit(accountTotalProfit);
            BigDecimal totalProfit = new BigDecimal(accountTotalProfit);

            if (totalBuyPrice == 0) {
                account.setTotalReturnRate(0);
            } else {
                BigDecimal totalBuyingPrice = new BigDecimal(totalBuyPrice);
                float returnRate = totalProfit.divide(totalBuyingPrice, 5, RoundingMode.HALF_EVEN).floatValue();
                account.setTotalReturnRate(returnRate);
            }
        }

        List<RankingResponseDto> res = new ArrayList<>();
        List<Account> result = accountRepository.findFirst10ByOrderByTotalReturnRateDesc();
        for (Account account : result) {
            Member member = account.getMember();
            res.add(
                    RankingResponseDto.builder()
                            .memberId(member.getId())
                            .nickname(member.getNickname())
                            .profileImg(member.getProfileImg())
                            .targetReturnRate(account.getTargetReturnRate())
                            .returnRate(account.getTotalReturnRate())
                            .profit(account.getTotalProfit())
                            .build()
            );
        }
        return ResponseDto.success(res);
    }
}
