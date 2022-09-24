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
    int code;
    int current_price;
    int volumn;
    int volumn_amount;
    String date;
    int start_price;
    int final_price;
//    public Stock(String code, String current_Price, String volumn, String volumn_amount, String day, String start_price, String final_price) throws ParseException {
//        this.code = Integer.valueOf(code);
//        this.current_price = Integer.valueOf(current_Price);
//        this.volumn = Integer.valueOf(volumn);
//        this.volumn_amount = Integer.valueOf(volumn_amount);
//        this.date = day;
//        this.start_price = Integer.valueOf(start_price);
//        this.final_price = Integer.valueOf(final_price);
//    }
//
    public Stock() {}
}
