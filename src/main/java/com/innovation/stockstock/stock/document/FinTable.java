package com.innovation.stockstock.stock.document;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Document(collection = "finsInfo")
@Getter
public class FinTable {
    @Id
    private String id;
    private String code;
    private String name;
    private List<List<Integer>> data;
}
