package com.innovation.stockstock.entity;

import lombok.Getter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Stock {
    @Id
    @GeneratedValue
    Long id;
    String code;
    int current_price;
    int volumn;
    int traded_volume;
    String date;
    int start_price;
    int higher_price;
    int lower_price;

    public Stock(String code, String current_Price, String volumn, String traded_volume, String day, String start_price, String higher_price, String lower_price){
        this.code = code;
        this.current_price = Integer.valueOf(current_Price);
        this.volumn = Integer.valueOf(volumn);
        this.traded_volume = Integer.valueOf(traded_volume);
        this.date = day;
        this.start_price = Integer.valueOf(start_price);
        this.higher_price = Integer.valueOf(higher_price);
        this.lower_price = Integer.valueOf(lower_price);
    }

    public Stock() {}
}
