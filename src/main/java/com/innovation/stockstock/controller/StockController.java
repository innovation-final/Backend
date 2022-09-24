package com.innovation.stockstock.controller;

import com.innovation.stockstock.dto.StockResponseDto;
import com.innovation.stockstock.entity.Stock;
import com.innovation.stockstock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StockController {
    private final StockRepository stockRepository;

    @GetMapping("/api/stock/get")
    public ResponseEntity<?> getInfo(){
        List<Stock> stocks = stockRepository.findAll();
        List<StockResponseDto> stockResponseDtoList = new ArrayList<>();
        for (int i=0;i< stocks.size();i++){
            Stock stock = stocks.get(i);
            StockResponseDto responseDto = StockResponseDto.builder()
                    .id(stock.getId())
                    .code(stock.getCode())
                    .date(stock.getDate())
                    .start_price(stock.getStart_price())
                    .final_price(stock.getFinal_price())
                    .current_price(stock.getCurrent_price())
                    .volumn(stock.getVolumn())
                    .volumn_amount(stock.getVolumn_amount())
                    .build();
            stockResponseDtoList.add(responseDto);
        }
        return ResponseEntity.ok(stockResponseDtoList);
    }
//    @PostMapping("/save")
//    @Transactional
//    public List<Stock> save(@RequestParam("file") MultipartFile file) throws IOException, ParseException {
//
//        String fileName = file.getOriginalFilename();
//        String extension = fileName.substring(fileName.lastIndexOf(".")+1);// 3
//
//        if (!extension.equals("xlsx") && !extension.equals("xls")) {
//            throw new IOException("엑셀파일만 업로드 해주세요.");
//        }
//
//        Workbook workbook = null;
//
//        if (extension.equals("xlsx")) {
//            workbook = new XSSFWorkbook(file.getInputStream());
//        } else if (extension.equals("xls")) {
//            workbook = new HSSFWorkbook(file.getInputStream());
//        }
//
//        Sheet worksheet = workbook.getSheetAt(0);
//
//        int rows = worksheet.getPhysicalNumberOfRows();
//        List<Stock> dataList = new ArrayList<>();
//        for (int i = 1; i < rows; i++) { // 4
//            Row row = worksheet.getRow(i);
//            if(row==null){
//                break;
//            }
//            Stock data = new Stock(
//                    row.getCell(1).getStringCellValue(),
//                    row.getCell(2).getStringCellValue(),
//                    row.getCell(3).getStringCellValue(),
//                    row.getCell(4).getStringCellValue(),
//                    row.getCell(5).getStringCellValue(),
//                    row.getCell(6).getStringCellValue(),
//                    row.getCell(7).getStringCellValue()
//            );
//            dataList.add(data);
//            tempRepository.save(data);
//        }
//        return dataList;
//    }
}