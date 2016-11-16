package com.uwaterloo.portfoliorebalancing.model;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by yuweixu on 2015-10-05.
 */
public class Stock extends SugarRecord<Stock> {

    private String symbol;
    private String name;
    private double price;

    public Stock() {}

    public Stock (String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }
}
