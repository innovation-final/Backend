package com.innovation.stockstock.dto.response;

import com.innovation.stockstock.dto.StockDetailDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class StockResponseDto {
    private String code;
    private String name;
    private String market;
    private Long marCap;
    private List<StockDetailDto> stockDetail;
    private StockDetailDto current;
}
