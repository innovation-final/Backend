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
import com.innovation.stockstock.stock.document.Stock;
import com.innovation.stockstock.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

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
    private final StockRepository stockRepository;

    @Transactional
    @Scheduled(cron = "0 1/2 9-21 * * MON-FRI", zone = "Asia/Seoul")
    public void contractLimitPriceOrder() throws InterruptedException {
        //TimeUnit.SECONDS.sleep(21);
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
            int currentPrice;
            try {
                currentPrice = Integer.parseInt(redisRepository.getTradePrice(stockCode));
            }catch(Exception e){
                Stock stock = stockRepository.findByCode(stockCode);
                Map<String, String> current = stock.getCurrent();
                currentPrice = Integer.valueOf(current.get("last_price"));
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
                    // 미실현 수익 : 보유주식현재가 - 보유주식매수가
                    Long totalSumBuying = stockHoldingRepository.sumHoldingBuyPrice(stockCode,account) + totalPrice;
                    totalAmount += stock.getAmount();

                    int avgBuying = Long.valueOf(totalSumBuying/totalAmount).intValue();
                    stock.setAvgBuying(avgBuying);

                    long profit = currentPrice*totalAmount - totalSumBuying; // 총보유량 * 현재가 - 총매수가
                    stock.setProfit(profit);

                    BigDecimal curPrice = new BigDecimal(currentPrice*totalAmount);
                    BigDecimal sumBuying=new BigDecimal(totalSumBuying);
                    float returnRate = curPrice.subtract(sumBuying).divide(sumBuying, 5, RoundingMode.HALF_EVEN).floatValue();
                    stock.setReturnRate(returnRate);

                    stock.updateAmount(true, orderAmount);
                }

                account.updateBalance(true, totalPrice);

                buyOrderRepository.save(
                        BuyOrder.builder()
                                .stockName(stockName)
                                .orderCategory("지정가")
                                .buyPrice(currentPrice)
                                .buyAmount(orderAmount)
                                .account(account)
                                .stockCode(stockCode)
                                .build()
                );
                limitPriceOrderRepository.deleteById(limitPriceOrder.getId());

                NotificationRequestDto notificationRequestDto = new NotificationRequestDto(Event.지정가, stock.getStockName()+"이/가 지정가("+orderPrice+"원) 이하인 "+currentPrice+ "원에 매수되었습니다.");
                try {
                    notificationService.send(account.getMember().getId(), notificationRequestDto);
                }catch (Exception e){
                    e.getMessage();
                }

                // 첫 매수인 경우 뱃지 부여 및 알람 전송
                Achievement firstBuying = achievementRepository.findByName("BUY");
                boolean firstBuyingHasAchieved = memberAchievementRepository.existsByMemberAndAchievement(member, firstBuying);
                if (!firstBuyingHasAchieved) {
                    memberAchievementRepository.save(new MemberAchievement(member, firstBuying));
                    NotificationRequestDto forFirstBuyer = new NotificationRequestDto(Event.뱃지취득, "워렌버핏이 돼보자 뱃지를 얻었습니다.");
                    notificationService.send(member.getId(), forFirstBuyer);
                }

                // 한 종목 100주 이상 매수 시 최대 주주
                if (totalAmount>=100) {
                    Achievement topStockholder = achievementRepository.findByName("STOCKHOLD");
                    boolean topStockholderHasAchieved = memberAchievementRepository.existsByMemberAndAchievement(member, topStockholder);
                    if (!topStockholderHasAchieved) {
                        memberAchievementRepository.save(new MemberAchievement(member, topStockholder));
                        NotificationRequestDto forTopStockholder = new NotificationRequestDto(Event.뱃지취득, "이 구역의 최대주주 뱃지를 얻었습니다.");
                        notificationService.send(member.getId(), forTopStockholder);
                    }
                }
            } else if (stock != null && category.equals("sell") && orderPrice <= currentPrice && orderAmount <= stock.getAmount()) { // 지정가 매도주문 체결
                Long buyingPrice = Long.valueOf(stock.getAvgBuying()*orderAmount);

                // 매도시마다 계좌실현손익 , 계좌잔고 , 보유종목 보유량 업데이트
                account.updateTotalRealizedProfit(orderAmount * currentPrice - buyingPrice);
                account.updateBalance(false, totalPrice);

                stock.updateAmount(false, orderAmount);
                if (stock.getAmount() == 0) {
                    stockHoldingRepository.deleteById(stock.getId());
                }

                account.updateBalance(false, totalPrice);

                sellOrderRepository.save(
                        SellOrder.builder()
                                .stockName(stockName)
                                .stockCode(stockCode)
                                .orderCategory("지정가")
                                .sellPrice(currentPrice)
                                .sellAmount(orderAmount)
                                .account(account)
                                .build()
                );
                limitPriceOrderRepository.deleteById(limitPriceOrder.getId());

                NotificationRequestDto notificationRequestDto = new NotificationRequestDto(Event.지정가, stock.getStockName()+"이/가 지정가("+orderPrice+"원) 이상인 "+currentPrice+"원에 매도되었습니다.");
                try {
                    notificationService.send(limitPriceOrder.getAccount().getMember().getId(), notificationRequestDto);
                }catch (Exception e){
                    e.getMessage();
                }
            }
        }
    }
//    @Transactional
//    // @Scheduled(cron = "0 1/2 * * * *", zone = "Asia/Seoul")
//    public void noticeLikeStockPrice() {
//        List<LikeStock> likeStockList = likeStockRepository.findAll();
//        for(LikeStock likeStock:likeStockList){
//            int curPrice = Integer.valueOf(redisRepository.getTradePrice(likeStock.getStockId()));
//            String stockName = likeStock.getStockName();
//            NotificationRequestDto notificationRequestDto=null;
//            if(likeStock.getBuyLimitPrice()<=curPrice){
//                Member member = likeStock.getMember();
//                notificationRequestDto = new NotificationRequestDto(Event.관심종목, stockName+"이 희망매수가("+likeStock.getBuyLimitPrice()+"원)이하입니다.");
//                notificationService.send(member.getId(), notificationRequestDto);
//            }else if(likeStock.getSellLimitPrice()>=curPrice) {
//                Member member = likeStock.getMember();
//                notificationRequestDto = new NotificationRequestDto(Event.관심종목, stockName +"이 희망매도가("+likeStock.getSellLimitPrice()+"원)이상입니다.");
//                notificationService.send(member.getId(), notificationRequestDto);
//            }
//        }
//    }
}
