package com.innovation.stockstock.stock.document;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Document(collection = "chart_year")
@Getter
public class StockYear {
    @Id
    private String id;
    private String code;
    private String name;
    private String market;
    private List<List<String>> data;
}

