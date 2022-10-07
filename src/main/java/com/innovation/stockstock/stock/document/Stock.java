package com.innovation.stockstock.stock.document;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;

@Document(collection = "chart")
@Getter
public class Stock {
    @Id
    private String id;
    private String code;
    private String name;
    private String market;
    private Long marcap;
    private List<List<String>> data;
    private Map<String, String> current;
}
