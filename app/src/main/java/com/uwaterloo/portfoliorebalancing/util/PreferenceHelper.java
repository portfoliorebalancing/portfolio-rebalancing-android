package com.uwaterloo.portfoliorebalancing.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.model.Stock;
import com.uwaterloo.portfoliorebalancing.ui.StockSelectorActivity;

import java.util.Comparator;

/**
 * Created by lucas on 23/11/16.
 */

public class PreferenceHelper {

    public static int getPreferredType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String preference = prefs.getString("preferred_type", "1");
        return Integer.valueOf(preference);
    }

    public static int getPreferredStrategy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String preference = prefs.getString("preferred_strategy", "1");
        return Integer.valueOf(preference);
    }

    public static Comparator<Stock> getStockComparator(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String preference = prefs.getString("stock_ordering_portfolio", "-1");
        switch (preference) {
            case "in_order_added":
                return null;
            case "alphabetical_name":
                return new Comparator<Stock>() {
                    @Override
                    public int compare(Stock o1, Stock o2) {
                        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                    }
                };
            case "alphabetical_symbol":
                return new Comparator<Stock>() {
                    @Override
                    public int compare(Stock o1, Stock o2) {
                        return o1.getSymbol().toLowerCase().compareTo(o2.getSymbol().toLowerCase());
                    }
                };
            case "increasing_price":
                return new Comparator<Stock>() {
                    @Override
                    public int compare(Stock o1, Stock o2) {
                        return Double.compare(o1.getPrice(), o2.getPrice());
                    }
                };
            default:
                return null;
        }
    }

    public static Comparator<StockSelectorActivity.StockData> getStockSelectorComparator(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String preference = prefs.getString("stock_ordering_selector", "-1");
        switch (preference) {
            case "alphabetical_name":
                return new Comparator<StockSelectorActivity.StockData>() {
                    @Override
                    public int compare(StockSelectorActivity.StockData o1, StockSelectorActivity.StockData o2) {
                        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                    }
                };
            case "alphabetical_symbol":
                return new Comparator<StockSelectorActivity.StockData>() {
                    @Override
                    public int compare(StockSelectorActivity.StockData o1, StockSelectorActivity.StockData o2) {
                        return o1.getSymbol().toLowerCase().compareTo(o2.getSymbol().toLowerCase());
                    }
                };
            case "increasing_price":
                return new Comparator<StockSelectorActivity.StockData>() {
                    @Override
                    public int compare(StockSelectorActivity.StockData o1, StockSelectorActivity.StockData o2) {
                        return Double.compare(o1.getPrice(), o2.getPrice());
                    }
                };
            default:
                return null;
        }
    }

    public static String getStockMarketString(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("stock_market", "nyse");
    }

    public static Comparator<Simulation> getSimulationComparator(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String preference = prefs.getString("simulation_ordering", "-1");
        switch (preference) {
            case "in_order_added":
                return null;
            case "alphabetical_name":
                return new Comparator<Simulation>() {
                    @Override
                    public int compare(Simulation o1, Simulation o2) {
                        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                    }
                };
            case "alphabetical_symbol":
                return new Comparator<Simulation>() {
                    @Override
                    public int compare(Simulation o1, Simulation o2) {
                        return o1.getSymbols()[0].toLowerCase().compareTo(o2.getSymbols()[0].toLowerCase());
                    }
                };
            case "increasing_account_balance":
                return new Comparator<Simulation>() {
                    @Override
                    public int compare(Simulation o1, Simulation o2) {
                        return Double.compare(o1.getAccount(), o2.getAccount());
                    }
                };
            default:
                return null;
        }
    }
}
