package com.uwaterloo.portfoliorebalancing.ui.extra;

/**
 * Created by yuweixu on 15-11-09.
 */
public class StockWeight {
    private String symbol;
    private double weight;
    public StockWeight(String symbol, double weight) {
        this.symbol = symbol;
        this.weight = weight;
    }

    public String getSymbol() {return symbol;}
    public double getWeight() {return weight;}
}
