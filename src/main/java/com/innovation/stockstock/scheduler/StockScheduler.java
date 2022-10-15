package com.innovation.stockstock.scheduler;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.repository.StockHoldingRepository;
import com.innovation.stockstock.chatRedis.redis.RedisRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class StockScheduler {

    private final RedisRepository redisRepository;
    private final LimitPriceOrderRepository limitPriceOrderRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private final BuyOrderRepository buyOrderRepository;
    private final SellOrderRepository sellOrderRepository;

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
            String stockCode = limitPriceOrder.getStockCode();
            int orderPrice = limitPriceOrder.getPrice();
            int orderAmount = limitPriceOrder.getAmount();

            int currentPrice = Integer.parseInt(redisRepository.getTradePrice(stockCode));
            int totalPrice = orderAmount * currentPrice;
            StockHolding stock = stockHoldingRepository.findByStockCodeAndAccountId(stockCode, account.getId());

            if (category.equals("buy") && orderPrice >= currentPrice && totalPrice <= account.getBalance()) { // 지정가 매수주문 체결
                if (stock == null) {
                    stock = stockHoldingRepository.save(
                            StockHolding.builder()
                                    .stockCode(stockCode)
                                    .amount(orderAmount)
                                    .account(account)
                                    .profit(0L)
                                    .returnRate(0f)
                                    .build()
                    );
                } else {
                    stock.updateAmount(true, orderAmount);
                }
                account.updateBalance(true, totalPrice);
                limitPriceOrderRepository.deleteById(limitPriceOrder.getId());
                buyOrderRepository.save(
                        BuyOrder.builder()
                                .orderCategory("지정가")
                                .buyPrice(currentPrice)
                                .buyAmount(orderAmount)
                                .account(account)
                                .stockHolding(stock)
                                .build()
                );
            } else if (stock != null && category.equals("sell") && orderPrice <= currentPrice && orderAmount <= stock.getAmount()) { // 지정가 매도주문 체결
                stock.updateAmount(false, orderAmount);
                account.updateBalance(false, totalPrice);
                limitPriceOrderRepository.deleteById(limitPriceOrder.getId());
                sellOrderRepository.save(
                        SellOrder.builder()
                                .orderCategory("지정가")
                                .sellPrice(currentPrice)
                                .sellAmount(orderAmount)
                                .account(account)
                                .stockHolding(stock)
                                .build()
                );
            }
        }
        System.out.println(LocalDateTime.now());
    }
}
