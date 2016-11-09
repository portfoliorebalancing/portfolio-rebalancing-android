package com.uwaterloo.portfoliorebalancing.model;

import com.orm.SugarRecord;

/**
 * Created by lucas on 16/10/16.
 */

public class StockList extends SugarRecord<StockList> {

    private static String SEPARATOR = ">";
    private String stockList = "";

    public StockList() {}

    public void addStock(String name, String symbol, String price) {
        if (!stockList.equals("")) {
            stockList = stockList + SEPARATOR;
        }
        stockList = stockList + name + SEPARATOR + symbol + SEPARATOR + price;
    }

    public String getStockList() {
        return stockList;
    }

    public static String getSEPARATOR() {
        return SEPARATOR;
    }
}