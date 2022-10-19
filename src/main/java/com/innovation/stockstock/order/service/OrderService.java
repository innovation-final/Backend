package com.innovation.stockstock.order.service;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.account.repository.StockHoldingRepository;
import com.innovation.stockstock.achievement.domain.Achievement;
import com.innovation.stockstock.achievement.domain.MemberAchievement;
import com.innovation.stockstock.achievement.repository.AchievementRepository;
import com.innovation.stockstock.achievement.repository.MemberAchievementRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.MemberUtil;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.notification.domain.Event;
import com.innovation.stockstock.notification.dto.NotificationRequestDto;
import com.innovation.stockstock.notification.service.NotificationService;
import com.innovation.stockstock.order.domain.BuyOrder;
import com.innovation.stockstock.order.domain.LimitPriceOrder;
import com.innovation.stockstock.order.domain.SellOrder;
import com.innovation.stockstock.order.dto.GetOrderRequestDto;
import com.innovation.stockstock.order.dto.OrderRequestDto;
import com.innovation.stockstock.order.dto.OrderResponseDto;
import com.innovation.stockstock.order.repository.BuyOrderRepository;
import com.innovation.stockstock.order.repository.LimitPriceOrderRepository;
import com.innovation.stockstock.order.repository.SellOrderRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final BuyOrderRepository buyOrderRepository;
    private final SellOrderRepository sellOrderRepository;
    private final AccountRepository accountRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private final LimitPriceOrderRepository limitPriceOrderRepository;
    private final AchievementRepository achievementRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final NotificationService notificationService;

    public ResponseEntity<?> getOrders(GetOrderRequestDto requestDto) {
        Account account = getAccount();
        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        Boolean isSigned = requestDto.getIsSigned();
        String orderCategory = requestDto.getOrderCategory();
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (requestDto.getStartDate() == null && requestDto.getEndDate() == null) {
            startDate = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0, 0);
            endDate = LocalDateTime.now();
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDate = LocalDate.parse(requestDto.getStartDate(), formatter).atStartOfDay();
            endDate = LocalDate.parse(requestDto.getEndDate(), formatter).atTime(LocalTime.MAX);
        }

        ArrayList<OrderResponseDto> res = new ArrayList<>();
        if (!isSigned) {
            List<LimitPriceOrder> limitPriceOrders = limitPriceOrderRepository.findAllByAccountIdAndCategoryAndOrderAtBetween(account.getId(), orderCategory, startDate, endDate);
            for (LimitPriceOrder limitPriceOrder : limitPriceOrders) {
                res.add(
                        OrderResponseDto.builder()
                                .id(limitPriceOrder.getId())
                                .stockName(limitPriceOrder.getStockName())
                                .date(String.valueOf(limitPriceOrder.getOrderAt()))
                                .orderCategory("지정가")
                                .amount(limitPriceOrder.getAmount())
                                .price(limitPriceOrder.getPrice())
                                .build()
                );
            }
        } else {
            if (orderCategory.equals("buy")) {
                List<BuyOrder> buyOrders = buyOrderRepository.findAllByAccountIdAndBuyAtBetween(account.getId(), startDate, endDate);
                for (BuyOrder buyOrder : buyOrders) {
                    res.add(
                            OrderResponseDto.builder()
                                    .id(buyOrder.getId())
                                    .stockName(buyOrder.getStockName())
                                    .date(String.valueOf(buyOrder.getBuyAt()))
                                    .orderCategory(buyOrder.getOrderCategory())
                                    .amount(buyOrder.getBuyAmount())
                                    .price(buyOrder.getBuyPrice())
                                    .build()
                    );
                }
            } else {
                List<SellOrder> sellOrders = sellOrderRepository.findAllByAccountIdAndSellAtBetween(account.getId(), startDate, endDate);
                for (SellOrder sellOrder : sellOrders) {
                    res.add(
                            OrderResponseDto.builder()
                                    .id(sellOrder.getId())
                                    .stockName(sellOrder.getStockName())
                                    .date(String.valueOf(sellOrder.getSellAt()))
                                    .orderCategory(sellOrder.getOrderCategory())
                                    .amount(sellOrder.getSellAmount())
                                    .price(sellOrder.getSellPrice())
                                    .build()
                    );
                }
            }
        }

        return ResponseEntity.ok().body(ResponseDto.success(res));
    }

    @Transactional
    public ResponseEntity<?> buyStock(String stockCode, OrderRequestDto requestDto) {
        Member member = MemberUtil.getMember();
        Account account = accountRepository.findByMember(member);
        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        String category = requestDto.getOrderCategory();
        int amount = requestDto.getAmount();
        int price = requestDto.getPrice();
        int totalPrice = amount * price;
        String stockName = requestDto.getStockName();
        int totalAmount = amount;

        if (category.equals("시장가") && totalPrice <= account.getBalance()) {
            StockHolding stock = stockHoldingRepository.findByStockCodeAndAccountId(stockCode, account.getId());
            if (stock == null) {
                stock = stockHoldingRepository.save(
                        StockHolding.builder()
                                .stockCode(stockCode)
                                .stockName(stockName)
                                .amount(amount)
                                .account(account)
                                .avgBuying(price)
                                .returnRate(0f) // 시장가 = 현재가, 매수 시 수익 0
                                .profit(0L)
                                .build()
                );
            } else {
                Long totalSumBuying = buyOrderRepository.sumBuyPrice(stock) + totalPrice;
                totalAmount += buyOrderRepository.sumBuyAmount(stock);
                int avgBuying = Long.valueOf(totalSumBuying / totalAmount).intValue();

                stock.setAvgBuying(avgBuying);
                stock.updateAmount(true, amount);

                long profit = price*amount - totalSumBuying; // 총보유량 * 현재가 - 총매수가
                stock.setProfit(profit);

                BigDecimal curPrice = new BigDecimal(price*totalAmount);
                BigDecimal sumBuying=new BigDecimal(totalSumBuying);
                float returnRate = curPrice.subtract(sumBuying).divide(sumBuying, 5, RoundingMode.HALF_EVEN).floatValue();
                stock.setReturnRate(returnRate);

            }
            buyOrderRepository.save(
                    BuyOrder.builder()
                            .stockName(stockName)
                            .orderCategory(category)
                            .buyPrice(price)
                            .buyAmount(amount)
                            .account(account)
                            .stockHolding(stock)
                            .build()
            );
            account.updateBalance(true, totalPrice);

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

        } else if (category.equals("지정가")) {
            limitPriceOrderRepository.save(
                    LimitPriceOrder.builder()
                            .stockCode(stockCode)
                            .stockName(stockName)
                            .category("buy")
                            .price(price)
                            .amount(amount)
                            .account(account)
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.ORDER_FAIL));
        }
        return ResponseEntity.ok().body(ResponseDto.success("Buy Order Success"));
    }

    @Transactional
    public ResponseEntity<?> sellStock(String stockCode, OrderRequestDto requestDto) {
        Account account = getAccount();
        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        String category = requestDto.getOrderCategory();
        int amount = requestDto.getAmount();
        int price = requestDto.getPrice();
        int totalPrice = amount * price;
        String stockName = requestDto.getStockName();

        StockHolding stock = stockHoldingRepository.findByStockCodeAndAccountId(stockCode, account.getId());
        if (stock == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.ORDER_FAIL));
        }

        if (category.equals("시장가") && amount <= stock.getAmount()) {
            sellOrderRepository.save(
                    SellOrder.builder()
                            .stockName(stockName)
                            .orderCategory(category)
                            .sellPrice(price)
                            .sellAmount(amount)
                            .account(account)
                            .stockHolding(stock)
                            .build()
            );
            stock.updateAmount(false, amount);
            if (stock.getAmount() == 0) {
                stockHoldingRepository.deleteById(stock.getId());
            }
            account.updateBalance(false, totalPrice);
        } else if (category.equals("지정가")) {
            limitPriceOrderRepository.save(
                    LimitPriceOrder.builder()
                            .stockCode(stockCode)
                            .stockName(stockName)
                            .category("sell")
                            .price(price)
                            .amount(amount)
                            .account(account)
                            .build()
            );
        } else {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.ORDER_FAIL));
        }
        return ResponseEntity.ok().body(ResponseDto.success("Sell Order Success"));
    }

    public Account getAccount() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findByMember(userDetails.getMember());
    }
}
