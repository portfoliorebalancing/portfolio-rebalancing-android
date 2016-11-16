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
    private int numStocks; // Number of stocks in portfolio
    private int strategy; // Strategy (Ex. Constant Proportions)
    private int type; // Real time or historical
    private double account; // Amount of money in portfolio account
    private double bank; // Amount of money in bank
    private double totalWeight; // Sum of all the weights (used for constant proportions)
    private String name; // Simulation name
    private String symbols; // List of stock symbols in portfolio separated by commas
    private String weights; // List of stock weights/ratios separated by commas (used for constant proportions)
    private String startDate; // Start date for the simulation
    private String endDate; // End date for the simulation
    private boolean realTime = false; // True if simulation is real time

    private double cppiFloor;
    private double cppiMultiplier;

    private double optionPrice;
    private double strike;

    public Simulation() {}

    // Constructor for backtest simulation
    //TODO: For now, just one stock per simulation.  Perhaps later, multiple stocks will be added to a simulation.
    //TODO: The code for this has been left intact.  However, we enforce that only one stock can be added to a simulation.
    //TODO: Note the use of constructors here.
    public Simulation(String symbol, List<Double> ratios, int strategy, double bank, double money, String name) {
        this(Collections.singletonList(symbol), ratios, strategy, bank, money, name);
    }

    private Simulation(List<String> stockList, List<Double> ratios, int strategy, double bank, double money, String name) {
        numStocks = stockList.size();
        for (Double d: ratios) {
            totalWeight += d;
        }
        this.account = money;

        StringBuilder weightStringBuilder = new StringBuilder();
        StringBuilder idStringBuilder = new StringBuilder();
        for (int i=0; i<numStocks; i++) {
            idStringBuilder.append(stockList.get(i)).append(",");
            weightStringBuilder.append(ratios.get(i)/totalWeight).append(",");
        }
        this.symbols = idStringBuilder.toString();
        this.weights = weightStringBuilder.toString();

        this.bank = bank;
        this.strategy = strategy;
        this.name = name;

        this.cppiFloor = 0;
        this.cppiMultiplier = 0;
        this.optionPrice = 0;
        this.strike = 0;
    }

    public void setRealTime(boolean b){ realTime = b; }
    public void setName(String n) { if (name.equals("")){return;} name = n; }
    public void setAccount(double n) { account = n; }
    public void setBank(double n) { bank = n; }
    public void setType(int n) { type = n; }
    public void setStrategy(int n) { strategy = n; }
    public void setStartDate(String date) { startDate = date;}
    public void setEndDate(String date) {endDate = date;}
    public void setWeights(List<Double> ratios) {
        totalWeight = 0;
        for (Double d: ratios) {
            totalWeight += d;
        }
        StringBuilder weightStringBuilder = new StringBuilder();
        for (int i=0; i<numStocks; i++) {
            weightStringBuilder.append(ratios.get(i)/totalWeight).append(",");
        }
        weights = weightStringBuilder.toString();
    }
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
    public int getStrategy() {return strategy;}
    public int getType() {return type;}
    public String getName() {return name;}
    public String getStartDate() {return startDate;}
    public String getEndDate() {return endDate;}
    public String[] getSymbols() {
        return symbols.split(",");
    }
    public List<String> getSymbolsList() {
        String [] symbols = getSymbols();
        return new ArrayList<String>(Arrays.asList(symbols));
    }
    public double[] getWeights() {
        String[] stringWeights = weights.split(",");
        double[] weights = new double[stringWeights.length];
        for (int i=0; i<stringWeights.length; i++) {
            weights[i] = Double.parseDouble(stringWeights[i]);
        }
        return weights;
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
