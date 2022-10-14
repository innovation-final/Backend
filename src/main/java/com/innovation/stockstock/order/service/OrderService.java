package com.innovation.stockstock.order.service;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.account.repository.StockHoldingRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.order.domain.BuyOrder;
import com.innovation.stockstock.order.domain.LimitPriceOrder;
import com.innovation.stockstock.order.domain.SellOrder;
import com.innovation.stockstock.order.dto.OrderRequestDto;
import com.innovation.stockstock.order.repository.BuyOrderRepository;
import com.innovation.stockstock.order.repository.LimitPriceOrderRepository;
import com.innovation.stockstock.order.repository.SellOrderRepository;
import com.innovation.stockstock.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final BuyOrderRepository buyOrderRepository;
    private final SellOrderRepository sellOrderRepository;
    private final AccountRepository accountRepository;
    private final StockHoldingRepository stockHoldingRepository;
    private final LimitPriceOrderRepository limitPriceOrderRepository;


    @Transactional
    public ResponseEntity<?> buyStock(String stockCode, OrderRequestDto requestDto) {
        Member member = getMember();
        Account account = accountRepository.findByMember(member);
        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        String category = requestDto.getOrderCategory();
        int amount = requestDto.getAmount();
        int price = requestDto.getPrice();
        int totalPrice = amount * price;

        if (category.equals("시장가") && totalPrice <= account.getBalance()) {
            StockHolding stock = stockHoldingRepository.findByStockCodeAndAccountId(stockCode, account.getId());
            buyOrderRepository.save(
                    BuyOrder.builder()
                            .orderCategory(category)
                            .buyPrice(price)
                            .buyAmount(amount)
                            .account(account)
                            .stockHolding(stock)
                            .build()
            );
            if (stock == null) {
                stockHoldingRepository.save(
                        StockHolding.builder()
                                .stockCode(stockCode)
                                .amount(amount)
                                .account(account)
                                .build()
                );
            } else {
                stock.updateAmount(true, amount);
            }
            account.updateBalance(true, totalPrice);
        } else if (category.equals("지정가")) {
            StockHolding stock = stockHoldingRepository.findByStockCodeAndAccountId(stockCode, account.getId());
            buyOrderRepository.save(
                    BuyOrder.builder()
                            .orderCategory(category)
                            .buyPrice(price)
                            .buyAmount(amount)
                            .account(account)
                            .stockHolding(stock)
                            .build()
            );
            limitPriceOrderRepository.save(
                    LimitPriceOrder.builder()
                            .stockCode(stockCode)
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
        Member member = getMember();
        Account account = accountRepository.findByMember(member);
        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        String category = requestDto.getOrderCategory();
        int amount = requestDto.getAmount();
        int price = requestDto.getPrice();
        int totalPrice = amount * price;

        StockHolding stock = stockHoldingRepository.findByStockCodeAndAccountId(stockCode, account.getId());
        if (stock == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.ORDER_FAIL));
        }

        if (category.equals("시장가") && amount <= stock.getAmount()) {
            sellOrderRepository.save(
                    SellOrder.builder()
                            .orderCategory(category)
                            .sellPrice(price)
                            .sellAmount(amount)
                            .account(account)
                            .stockHolding(stock)
                            .build()
            );
            stock.updateAmount(false, amount);
            account.updateBalance(false, totalPrice);
        } else if (category.equals("지정가")) {
            sellOrderRepository.save(
                    SellOrder.builder()
                            .orderCategory(category)
                            .sellPrice(price)
                            .sellAmount(amount)
                            .account(account)
                            .stockHolding(stock)
                            .build()
            );
            limitPriceOrderRepository.save(
                    LimitPriceOrder.builder()
                            .stockCode(stockCode)
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

    public Member getMember() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }
}
