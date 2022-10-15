package com.innovation.stockstock.order.service;

import com.innovation.stockstock.account.domain.Account;
import com.innovation.stockstock.account.domain.StockHolding;
import com.innovation.stockstock.account.repository.AccountRepository;
import com.innovation.stockstock.account.repository.StockHoldingRepository;
import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.order.domain.BuyOrder;
import com.innovation.stockstock.order.domain.LimitPriceOrder;
import com.innovation.stockstock.order.domain.SellOrder;
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

    public ResponseEntity<?> getBuyOrders() {
        Account account = getAccount();
        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        ArrayList<OrderResponseDto> res = new ArrayList<>();
        List<BuyOrder> buyOrders = buyOrderRepository.findAllByAccountId(account.getId());
        for (BuyOrder buyOrder : buyOrders) {
            res.add(
                    OrderResponseDto.builder()
                            .id(buyOrder.getId())
                            .date(String.valueOf(buyOrder.getBuyAt()))
                            .orderCategory(buyOrder.getOrderCategory())
                            .amount(buyOrder.getBuyAmount())
                            .price(buyOrder.getBuyPrice())
                            .build()
            );
        }
        return ResponseEntity.ok().body(ResponseDto.success(res));
    }

    public ResponseEntity<?> getSellOrders() {
        Account account = getAccount();
        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        ArrayList<OrderResponseDto> res = new ArrayList<>();
        List<SellOrder> sellOrders = sellOrderRepository.findAllByAccountId(account.getId());
        for (SellOrder sellOrder : sellOrders) {
            res.add(
                    OrderResponseDto.builder()
                            .id(sellOrder.getId())
                            .date(String.valueOf(sellOrder.getSellAt()))
                            .orderCategory(sellOrder.getOrderCategory())
                            .amount(sellOrder.getSellAmount())
                            .price(sellOrder.getSellPrice())
                            .build()
            );
        }
        return ResponseEntity.ok().body(ResponseDto.success(res));
    }

    @Transactional
    public ResponseEntity<?> buyStock(String stockCode, OrderRequestDto requestDto) {
        Account account = getAccount();
        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }

        String category = requestDto.getOrderCategory();
        int amount = requestDto.getAmount();
        int price = requestDto.getPrice();
        int totalPrice = amount * price;

        if (category.equals("시장가") && totalPrice <= account.getBalance()) {
            StockHolding stock = stockHoldingRepository.findByStockCodeAndAccountId(stockCode, account.getId());
            if (stock == null) {
                stock = stockHoldingRepository.save(
                        StockHolding.builder()
                                .stockCode(stockCode)
                                .amount(amount)
                                .account(account)
                                .returnRate(0f)
                                .profit(0L)
                                .build()
                );
            } else {
                stock.updateAmount(true, amount);
            }
            buyOrderRepository.save(
                    BuyOrder.builder()
                            .orderCategory(category)
                            .buyPrice(price)
                            .buyAmount(amount)
                            .account(account)
                            .stockHolding(stock)
                            .build()
            );
            account.updateBalance(true, totalPrice);
        } else if (category.equals("지정가")) {
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
        Account account = getAccount();
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

    public Account getAccount() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findByMember(userDetails.getMember());
    }
}
