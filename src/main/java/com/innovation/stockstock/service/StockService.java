package com.innovation.stockstock.service;

import com.innovation.stockstock.ErrorCode;
import com.innovation.stockstock.document.*;
import com.innovation.stockstock.dto.StockDetailDto;
import com.innovation.stockstock.dto.response.ResponseDto;
import com.innovation.stockstock.dto.response.StockRankResponseDto;
import com.innovation.stockstock.dto.response.StockResponseDto;
import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.repository.*;
import com.innovation.stockstock.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockRankRepository stockRankRepository;
    private final NewsRepository newsRepository;
    private final FinTableRepository finTableRepository;
    private final StockIndexRepository stockIndexRepository;

    public ResponseEntity<?> getStock(String stockCode) {
        Stock stock = stockRepository.findByCode(stockCode);
        if (stock == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        ArrayList<StockDetailDto> result = new ArrayList<>();
        List<List<String>> datas = stock.getData();
        for (List<String> data : datas) {
            Float flucRate;
            try {
                flucRate = Float.parseFloat(data.get(6));
            } catch (NumberFormatException e) {
                flucRate = null;
            }
            StockDetailDto res = StockDetailDto.builder()
                    .date(data.get(0))
                    .open(Integer.parseInt(data.get(1)))
                    .high(Integer.parseInt(data.get(2)))
                    .low(Integer.parseInt(data.get(3)))
                    .close(Integer.parseInt(data.get(4)))
                    .volume(Long.valueOf(data.get(5)))
                    .change(flucRate)
                    .build();
            result.add(res);
        }
        Map<String, String> current = stock.getCurrent();
        StockDetailDto now = StockDetailDto.builder()
                .date(String.valueOf(LocalDate.now()))
                .open(Integer.parseInt(current.get("first_price")))
                .high(Integer.parseInt(current.get("high_price")))
                .low(Integer.parseInt(current.get("low_price")))
                .close(Integer.parseInt(current.get("last_price")))
                .volume(Long.valueOf(current.get("volume")))
                .tradingValue(Long.valueOf(current.get("trading_value")))
                .change(Float.parseFloat(current.get("fluctuation_rate")) / 100)
                .build();

        return ResponseEntity.ok().body(ResponseDto.success(StockResponseDto.builder()
                        .code(stock.getCode())
                        .name(stock.getName())
                        .market(stock.getMarket())
                        .marCap(stock.getMarcap())
                        .stockDetail(result)
                        .current(now)
                        .build()
                )
        );
    }

    public ResponseEntity<?> getStockNews(String stockCode) {
        News news = newsRepository.findByCode(stockCode);
        if (news == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        return ResponseEntity.ok().body(ResponseDto.success(news.getData()));
    }

    public ResponseEntity<?> getStockTable(String stockCode) {
        FinTable table = finTableRepository.findByCode(stockCode);
        if (table == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        return ResponseEntity.ok().body(ResponseDto.success(table.getData()));
    }

    public ResponseDto<?> getRank(String criteria, HttpServletRequest request) {
        StockRank stockRank = stockRankRepository.findByCriteria(criteria);
        List<Map<String, String>> datas = stockRank.getData();

        String accessToken = request.getHeader("Authorization");
        if(accessToken==null) {
            return ResponseDto.success(makeStockRankResponse(datas, new ArrayList<>()));
        }

        Member member = getMember();
        List<StockRankResponseDto> result = makeStockRankResponse(datas, member.getInterests());
        return ResponseDto.success(result);
    }

    public ResponseEntity<?> getIndex(String name) {
        Index indexInfo = stockIndexRepository.findByName(name);
        if (indexInfo == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        List<List<String>> indexlist = indexInfo.getIndex();
        return ResponseEntity.ok().body(ResponseDto.success(indexlist));
    }

    public ResponseDto<?> getLikeStock() {
        Member member = getMember();
        List<String> interests = member.getInterests();
        ArrayList<StockResponseDto> result = new ArrayList<>();
        for (String interest : interests) {
            ArrayList<StockDetailDto> temp = new ArrayList<>();
            Stock stock = stockRepository.findByCode(interest);
            List<List<String>> datas = stock.getData();
            for (List<String> data : datas) {
                Float flucRate;
                try {
                    flucRate = Float.parseFloat(data.get(6));
                } catch (NumberFormatException e) {
                    flucRate = null;
                }
                StockDetailDto res = StockDetailDto.builder()
                        .date(data.get(0))
                        .open(Integer.parseInt(data.get(1)))
                        .high(Integer.parseInt(data.get(2)))
                        .low(Integer.parseInt(data.get(3)))
                        .close(Integer.parseInt(data.get(4)))
                        .volume(Long.valueOf(data.get(5)))
                        .change(flucRate)
                        .build();
                temp.add(res);
            }
            Map<String, String> current = stock.getCurrent();
            StockDetailDto now = StockDetailDto.builder()
                    .date(String.valueOf(LocalDate.now()))
                    .open(Integer.parseInt(current.get("first_price")))
                    .high(Integer.parseInt(current.get("high_price")))
                    .low(Integer.parseInt(current.get("low_price")))
                    .close(Integer.parseInt(current.get("last_price")))
                    .volume(Long.valueOf(current.get("volume")))
                    .tradingValue(Long.valueOf(current.get("trading_value")))
                    .change(Float.parseFloat(current.get("fluctuation_rate")) / 100)
                    .build();

            result.add(StockResponseDto.builder()
                    .code(stock.getCode())
                    .name(stock.getName())
                    .market(stock.getMarket())
                    .marCap(stock.getMarcap())
                    .stockDetail(temp)
                    .current(now)
                    .build());
        }
        return ResponseDto.success(result);
    }

    @Transactional
    public ResponseDto<?> likeStock(String stockCode) {
        Member member = getMember();
        member.updateStockInterest(stockCode, true);
        return ResponseDto.success("Like Success");
    }

    @Transactional
    public ResponseDto<?> cancelLikeStock(String stockCode) {
        Member member = getMember();
        member.updateStockInterest(stockCode, false);
        return ResponseDto.success("Like Cancel Success");
    }

    public Member getMember(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getMember();
    }

    public List<StockRankResponseDto> makeStockRankResponse(List<Map<String, String>> datas, List<String> codes) {
        List<StockRankResponseDto> responseDtoList = new ArrayList<>();
        for (Map<String, String> data : datas) {
            boolean isLike = codes.contains(data.get("stock_code"));
            StockRankResponseDto rankResponseDto = StockRankResponseDto.builder()
                    .rank(Integer.parseInt(data.get("rank")))
                    .stockCode(data.get("stock_code"))
                    .stockName(data.get("stock_name"))
                    .firstPrice(Integer.parseInt(data.get("first_price")))
                    .highPrice(Integer.parseInt(data.get("high_price")))
                    .lowPrice(Integer.parseInt(data.get("low_price")))
                    .lastPrice(Integer.parseInt(data.get("last_price")))
                    .volume(Long.valueOf(data.get("volume")))
                    .tradingValue(Long.valueOf(data.get("trading_value")))
                    .fluctuationRate(Float.parseFloat(data.get("fluctuation_rate")))
                    .isDoneInterest(isLike)
                    .build();
            responseDtoList.add(rankResponseDto);
        }
        return responseDtoList;
    }
}
