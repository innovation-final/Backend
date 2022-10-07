package com.innovation.stockstock.stock.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Document(collection = "index")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Index {
    @Id
    private String id;
    private String name;
    private List<List<String>> index;
    private int current;
}
