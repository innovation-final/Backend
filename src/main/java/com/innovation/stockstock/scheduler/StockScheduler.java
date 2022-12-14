package com.innovation.stockstock.scheduler;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.repository.StockHoldingRepository;
import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.achievement.domain.MemberAchievement;
import com.innovation.stockstock.achievement.repository.AchievementRepository;
import com.innovation.stockstock.achievement.repository.MemberAchievementRepository;
import com.innovation.stockstock.chatRedis.redis.RedisRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    public void contractLimitPriceOrder() throws InterruptedException, IllegalArgumentException {
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

                if (currentPrice == 0) {
                    throw new IllegalArgumentException("Order Fail");
                }
            } catch (Exception e) {
                Stock stock = stockRepository.findByCode(stockCode);
                Map<String, String> current = stock.getCurrent();
                currentPrice = Integer.valueOf(current.get("last_price"));
            }
            int totalPrice = orderAmount * currentPrice;
            StockHolding stock = stockHoldingRepository.findByStockCodeAndAccountId(stockCode, account.getId());

            if (category.equals("buy") && orderPrice >= currentPrice && totalPrice <= account.getBalance()) { // ????????? ???????????? ??????
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
                    // ????????? ?????? : ????????????????????? - ?????????????????????
                    Long totalSumBuying = stockHoldingRepository.sumHoldingBuyPrice(stockCode,account) + totalPrice;
                    totalAmount += stock.getAmount();

                    int avgBuying = Long.valueOf(totalSumBuying/totalAmount).intValue();
                    stock.setAvgBuying(avgBuying);

                    long profit = currentPrice*totalAmount - totalSumBuying; // ???????????? * ????????? - ????????????
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
                                .orderCategory("?????????")
                                .buyPrice(currentPrice)
                                .buyAmount(orderAmount)
                                .account(account)
                                .stockCode(stockCode)
                                .build()
                );
                limitPriceOrderRepository.deleteById(limitPriceOrder.getId());

                NotificationRequestDto notificationRequestDto = new NotificationRequestDto(Event.?????????, stock.getStockName()+"???/??? ?????????("+orderPrice+"???) ????????? "+currentPrice+ "?????? ?????????????????????.",null);
                try {
                    notificationService.send(account.getMember().getId(), notificationRequestDto);
                }catch (Exception e){
                    e.getMessage();
                }
                try {
                    // ??? ????????? ?????? ?????? ?????? ??? ?????? ??????
                    Achievement firstBuying = achievementRepository.findByName("BUY");
                    boolean firstBuyingHasAchieved = memberAchievementRepository.existsByMemberAndAchievement(member, firstBuying);
                    if (!firstBuyingHasAchieved) {
                        memberAchievementRepository.save(new MemberAchievement(member, firstBuying));
                        NotificationRequestDto forFirstBuyer = new NotificationRequestDto(Event.????????????, "??????????????? ????????? ????????? ???????????????.",null);
                        notificationService.send(member.getId(), forFirstBuyer);
                    }
                }catch (Exception e){
                    e.getMessage();
                }
                try {
                    // ??? ?????? 100??? ?????? ?????? ??? ?????? ??????
                    if (totalAmount >= 100) {
                        Achievement topStockholder = achievementRepository.findByName("STOCKHOLD");
                        boolean topStockholderHasAchieved = memberAchievementRepository.existsByMemberAndAchievement(member, topStockholder);
                        if (!topStockholderHasAchieved) {
                            memberAchievementRepository.save(new MemberAchievement(member, topStockholder));
                            NotificationRequestDto forTopStockholder = new NotificationRequestDto(Event.????????????, "??? ????????? ???????????? ????????? ???????????????.",null);
                            notificationService.send(member.getId(), forTopStockholder);
                        }
                    }
                }catch (Exception e){
                    e.getMessage();
                }
            } else if (stock != null && category.equals("sell") && orderPrice <= currentPrice && orderAmount <= stock.getAmount()) { // ????????? ???????????? ??????
                Long buyingPrice = Long.valueOf(stock.getAvgBuying()*orderAmount);

                // ??????????????? ?????????????????? , ???????????? , ???????????? ????????? ????????????
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
                                .orderCategory("?????????")
                                .sellPrice(currentPrice)
                                .sellAmount(orderAmount)
                                .account(account)
                                .build()
                );
                limitPriceOrderRepository.deleteById(limitPriceOrder.getId());

                NotificationRequestDto notificationRequestDto = new NotificationRequestDto(Event.?????????, stock.getStockName()+"???/??? ?????????("+orderPrice+"???) ????????? "+currentPrice+"?????? ?????????????????????.",null);
                try {
                    notificationService.send(limitPriceOrder.getAccount().getMember().getId(), notificationRequestDto);
                }catch (Exception e){
                    e.getMessage();
                }
            }
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleOrderException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.OUT_OF_MARKET_HOUR));
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
//                notificationRequestDto = new NotificationRequestDto(Event.????????????, stockName+"??? ???????????????("+likeStock.getBuyLimitPrice()+"???)???????????????.");
//                notificationService.send(member.getId(), notificationRequestDto);
//            }else if(likeStock.getSellLimitPrice()>=curPrice) {
//                Member member = likeStock.getMember();
//                notificationRequestDto = new NotificationRequestDto(Event.????????????, stockName +"??? ???????????????("+likeStock.getSellLimitPrice()+"???)???????????????.");
//                notificationService.send(member.getId(), notificationRequestDto);
//            }
//        }
//    }
}
