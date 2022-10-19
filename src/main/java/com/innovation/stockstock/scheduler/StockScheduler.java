package com.innovation.stockstock.scheduler;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.repository.StockHoldingRepository;
import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.achievement.domain.MemberAchievement;
import com.innovation.stockstock.achievement.repository.AchievementRepository;
import com.innovation.stockstock.achievement.repository.MemberAchievementRepository;
import com.innovation.stockstock.chatRedis.redis.RedisRepository;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.member.repository.MemberRepository;
import com.innovation.stockstock.notification.domain.Event;
import com.innovation.stockstock.notification.dto.NotificationRequestDto;
import com.innovation.stockstock.notification.service.NotificationService;
import com.innovation.stockstock.order.domain.BuyOrder;
import com.innovation.stockstock.order.domain.LimitPriceOrder;
import com.innovation.stockstock.order.domain.SellOrder;
import com.innovation.stockstock.order.repository.BuyOrderRepository;
import com.innovation.stockstock.order.repository.LimitPriceOrderRepository;
import com.innovation.stockstock.order.repository.SellOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StockScheduler {

    private final RedisRepository redisRepository;
    private final LimitPriceOrderRepository limitPriceOrderRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private final NotificationService notificationService;
    private final BuyOrderRepository buyOrderRepository;
    private final SellOrderRepository sellOrderRepository;
    private final AchievementRepository achievementRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Scheduled(cron = "0 1/2 * * * *", zone = "Asia/Seoul")
    //@Scheduled(cron = "0 1/2 9-15 * * MON-FRI", zone = "Asia/Seoul")
    public void contractLimitPriceOrder() throws InterruptedException {
        //TimeUnit.SECONDS.sleep(21);
        System.out.println(LocalDateTime.now());
        List<LimitPriceOrder> limitPriceOrders = limitPriceOrderRepository.findAll();
        for (LimitPriceOrder limitPriceOrder : limitPriceOrders) {
            Account account = limitPriceOrder.getAccount();
            String category = limitPriceOrder.getCategory();
            String stockName = limitPriceOrder.getStockName();
            String stockCode = limitPriceOrder.getStockCode();
            int orderPrice = limitPriceOrder.getPrice();
            int orderAmount = limitPriceOrder.getAmount();
            int totalAmount = orderAmount;

            Member member = memberRepository.findByAccount(account);

            int currentPrice = Integer.parseInt(redisRepository.getTradePrice(stockCode));
            if (currentPrice == 0) {
                return;
            }
            int totalPrice = orderAmount * currentPrice;
            StockHolding stock = stockHoldingRepository.findByStockCodeAndAccountId(stockCode, account.getId());

            if (category.equals("buy") && orderPrice >= currentPrice && totalPrice <= account.getBalance()) { // 지정가 매수주문 체결
                if (stock == null) {
                    stock = stockHoldingRepository.save(
                            StockHolding.builder()
                                    .stockCode(stockCode)
                                    .stockName(stockName)
                                    .amount(orderAmount)
                                    .account(account)
                                    .avgBuying(currentPrice)
                                    .profit(0L)
                                    .returnRate(0f)
                                    .build()
                    );
                } else {
                    Long totalSumBuying = buyOrderRepository.sumBuyPrice(stock) + totalPrice;
                    totalAmount += buyOrderRepository.sumBuyAmount(stock);
                    int avgBuying = Long.valueOf(totalSumBuying/totalAmount).intValue();

                    long profit = currentPrice*totalAmount - totalSumBuying; // 총보유량 * 현재가 - 총매수가
                    stock.setProfit(profit);

                    BigDecimal curPrice = new BigDecimal(currentPrice*totalAmount);
                    BigDecimal sumBuying=new BigDecimal(totalSumBuying);
                    float returnRate = curPrice.subtract(sumBuying).divide(sumBuying, 5, RoundingMode.HALF_EVEN).floatValue();
                    stock.setReturnRate(returnRate);

                    stock.setAvgBuying(avgBuying);
                    stock.updateAmount(true, orderAmount);
                }
                account.updateBalance(true, totalPrice);
                NotificationRequestDto notificationRequestDto = new NotificationRequestDto(Event.지정가, stock.getStockName()+"이/가 지정가("+orderPrice+"원) 이하인 "+currentPrice+ "원에 매수되었습니다.");
                notificationService.send(account.getMember().getId(),notificationRequestDto);
                limitPriceOrderRepository.deleteById(limitPriceOrder.getId());
                buyOrderRepository.save(
                        BuyOrder.builder()
                                .stockName(stockName)
                                .orderCategory("지정가")
                                .buyPrice(currentPrice)
                                .buyAmount(orderAmount)
                                .account(account)
                                .stockHolding(stock)
                                .build()
                );
                // 첫 매수인 경우 뱃지 부여 및 알람 전송
                Achievement firstBuying = achievementRepository.findByName("BUY");
                boolean firstBuyingHasAchieved = memberAchievementRepository.existsByMemberAndAchievement(member, firstBuying);
                if (!firstBuyingHasAchieved) {
                    memberAchievementRepository.save(new MemberAchievement(member, firstBuying));
                    NotificationRequestDto forFirstBuyer = new NotificationRequestDto(Event.뱃지취득, "뱃지를 취득하였습니다.");
                    notificationService.send(member.getId(), forFirstBuyer);
                }

                // 한 종목 100주 이상 매수 시 최대 주주
                if (totalAmount>=100) {
                    Achievement topStockholder = achievementRepository.findByName("STOCKHOLD");
                    boolean topStockholderHasAchieved = memberAchievementRepository.existsByMemberAndAchievement(member, topStockholder);
                    if (!topStockholderHasAchieved) {
                        memberAchievementRepository.save(new MemberAchievement(member, topStockholder));
                        NotificationRequestDto forTopStockholder = new NotificationRequestDto(Event.뱃지취득, "뱃지를 취득하였습니다.");
                        notificationService.send(member.getId(), forTopStockholder);
                    }
                }
            } else if (stock != null && category.equals("sell") && orderPrice <= currentPrice && orderAmount <= stock.getAmount()) { // 지정가 매도주문 체결
                stock.updateAmount(false, orderAmount);
                account.updateBalance(false, totalPrice);
                NotificationRequestDto notificationRequestDto = new NotificationRequestDto(Event.지정가, stock.getStockName()+"이/가 지정가("+orderPrice+"원) 이상인 "+currentPrice+"원에 매도되었습니다.");
                notificationService.send(limitPriceOrder.getAccount().getMember().getId(),notificationRequestDto);
                limitPriceOrderRepository.deleteById(limitPriceOrder.getId());
                sellOrderRepository.save(
                        SellOrder.builder()
                                .stockName(stockName)
                                .orderCategory("지정가")
                                .sellPrice(currentPrice)
                                .sellAmount(orderAmount)
                                .account(account)
                                .stockHolding(stock)
                                .build()
                );
                if (stock.getAmount() == 0) {
                    stockHoldingRepository.deleteById(stock.getId());
                }
            }
        }
        System.out.println(LocalDateTime.now());
    }
}
