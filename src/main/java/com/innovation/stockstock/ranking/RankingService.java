package com.innovation.stockstock.ranking;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.chatRedis.redis.RedisRepository;
import com.innovation.stockstock.common.MemberUtil;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.order.repository.BuyOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final AccountRepository accountRepository;
    private final MemberUtil memberUtil;

    @Transactional
    public ResponseDto<?> getReturnRank() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            memberUtil.updateAccountInfoAtCurrentTime(account);
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
