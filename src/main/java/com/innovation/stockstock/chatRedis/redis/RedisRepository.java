package com.innovation.stockstock.chatRedis.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;

@Repository
@RequiredArgsConstructor
public class RedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private ListOperations<String,String> prices;

    @PostConstruct
    private void init() {
        prices = redisTemplate.opsForList();
    }

    // 실시간 데이터 현재가 리턴
    public String getTradePrice(String stockCode){
        return prices.range(stockCode, -1, -1).get(0);
    }
}
