package com.innovation.stockstock.ranking;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.common.MemberUtil;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final MemberUtil memberUtil;

    @Transactional
    public ResponseDto<?> getReturnRank() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            memberUtil.updateAccountInfoAtCurrentTime(account);
        }

        List<ReturnRankingResponseDto> res = new ArrayList<>();
        List<Account> result = accountRepository.findFirst10ByOrderByTotalReturnRateDesc();

        for (Account account : result) {
            Member member = account.getMember();
            res.add(
                    ReturnRankingResponseDto.builder()
                            .memberId(member.getId())
                            .nickname(member.getNickname())
                            .profileImg(member.getProfileImg())
                            .targetReturnRate(account.getTargetReturnRate())
                            .returnRate(account.getTotalReturnRate())
                            .profit(account.getTotalProfit())
                            .realizedProfit(account.getTotalRealizedProfit())
                            .unrealizedProfit(account.getTotalUnrealizedProfit())
                            .build()
            );
        }
        return ResponseDto.success(res);
    }

    public ResponseDto<?> getLikeRank() {
        ArrayList<LikeRankingResponseDto> res = new ArrayList<>();
        List<Member> result = memberRepository.findFirst10ByOrderByLikeNumDesc();
        for (Member member : result) {
            res.add(new LikeRankingResponseDto(member.getId(), member.getNickname(), member.getProfileImg(), member.getLikeNum()));
        }
        return ResponseDto.success(res);
    }
}
