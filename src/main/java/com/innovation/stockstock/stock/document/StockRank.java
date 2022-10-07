package com.innovation.stockstock.stock.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;

@Document(collection = "ranking")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockRank {
    @Id
    private String id;
    private String criteria;
    private List<Map<String, String>> data;
}
