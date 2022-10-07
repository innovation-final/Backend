package com.innovation.stockstock.stock.document;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;

@Document(collection = "news")
@Getter
public class News {
    @Id
    private String id;
    private String code;
    private String name;
    private List<Map<String, String>> data;
}
