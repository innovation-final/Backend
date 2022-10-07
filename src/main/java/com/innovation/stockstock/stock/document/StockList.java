package com.innovation.stockstock.stock.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.Id;

@Document(collection = "stocklist")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockList {
    @Id
    private String id;
    private String code;
    private String name;
}
