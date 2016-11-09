package com.uwaterloo.portfoliorebalancing.model;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by yuweixu on 2015-10-19.
 */
public class Simulation extends SugarRecord<Simulation> {
    private int type; // Real time or historical
    private double account; // Amount of money in portfolio account
    private double bank; // Amount of money in bank
    private String name; // Simulation name
    private String symbol; // Stock symbol in portfolio
    private String startDate; // Start date for the simulation
    private String endDate; // End date for the simulation
    private boolean realTime = false; // True if simulation is real time

    private String strategies; //List of strategy ints separated by commas.

    private String balances;
    private String timestamps;

    private double cppiFloor;
    private double cppiMultiplier;

    private double optionPrice;
    private double strike;

    public Simulation() {}

    public Simulation(String stock, List<Integer> strategy, double bank, double money, String name) {
        this.account = money;
        this.symbol = stock;
        this.timestamps = startDate + ",";
        this.bank = bank;

        setStrategies(strategy);

        this.name = name;
        this.balances = account + ",";
        this.cppiFloor = 0;
        this.cppiMultiplier = 0;
        this.optionPrice = 0;
        this.strike = 0;
    }

    public void setStrategies(List<Integer> strategy) {
        StringBuilder strategyString = new StringBuilder();
        for (int i = 0; i < strategy.size(); i++) {
            strategyString.append(strategy.get(i)).append(",");
        }
        this.strategies = strategyString.toString();
    }

    public void setRealTime(boolean b){ realTime = b; }
    public void setName(String n) { if (name.equals("")){return;} name = n; }
    public void setAccount(double n) { account = n; }
    public void setBank(double n) { bank = n; }
    public void setType(int n) { type = n; }
    public void setStartDate(String date) { startDate = date;}
    public void setEndDate(String date) {endDate = date;}
    public void setCppiFloor(double cppiFloor) {
        this.cppiFloor = cppiFloor;
    }
    public void setCppiMultiplier(double cppiMultiplier) {
        this.cppiMultiplier = cppiMultiplier;
    }
    public void setOptionPrice(double optionPrice) {
        this.optionPrice = optionPrice;
    }
    public void setStrike(double strike) {
        this.strike = strike;
    }


    public boolean isRealTime() {return realTime;}
    public double getBank() {return bank;}
    public double getAccount() {return account;}
    public int getType() {return type;}
    public String getName() {return name;}
    public String getStartDate() {return startDate;}
    public String getEndDate() {return endDate;}
    public String getSymbol() {return symbol;}

    public int[] getStrategies() {
        String[] arr = strategies.split(",");
        int[] s = new int[arr.length];
        for (int i=0; i<arr.length; i++) {
            s[i] = Integer.parseInt(arr[i]);
        }
        return s;
    }

    public List<Integer> getStrategiesList() {
        List<Integer> result = new ArrayList<>();
        int[] array = getStrategies();
        for (int i = 0; i < array.length; i++) {
            result.add(array[i]);
        }
        return result;
    }

    public double getCppiFloor() {
        return cppiFloor;
    }
    public double getCppiMultiplier() {
        return cppiMultiplier;
    }
    public double getOptionPrice() {
        return optionPrice;
    }
    public double getStrike() {
        return strike;
    }
}
