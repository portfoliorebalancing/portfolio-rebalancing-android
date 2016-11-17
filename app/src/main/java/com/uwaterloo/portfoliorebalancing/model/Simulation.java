package com.uwaterloo.portfoliorebalancing.model;

import android.util.Base64;

import com.orm.SugarRecord;
import com.uwaterloo.portfoliorebalancing.util.AppUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by yuweixu on 2015-10-19.
 */
public class Simulation extends SugarRecord<Simulation> {
    private int numStocks; // Number of stocks in portfolio
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

    public Simulation() {}

    // Constructor for backtest simulation
    //TODO: For now, just one stock per simulation.  Perhaps later, multiple stocks will be added to a simulation.
    //TODO: The code for this has been left intact.  However, we enforce that only one stock can be added to a simulation.
    //TODO: Note the use of constructors here.
    public Simulation(String symbol, int type, String name, double account, Date begin, Date end) {
        this(Collections.singletonList(symbol), Collections.singletonList(1.0d), type, name, account, begin, end);
    }

    private Simulation(List<String> stockList, List<Double> ratios, int type, String name, double account, Date begin, Date end) {
        if (end == null) {
            realTime = true;
        }

        setName(name);
        setAccount(account);
        bank = account;
        setStartDate(AppUtils.formatDate(begin));
        if (end != null) {
            setEndDate(AppUtils.formatDate(end));
        }

        setType(type);

        numStocks = stockList.size();
        for (Double d: ratios) {
            totalWeight += d;
        }

        StringBuilder weightStringBuilder = new StringBuilder();
        StringBuilder idStringBuilder = new StringBuilder();
        for (int i=0; i<numStocks; i++) {
            idStringBuilder.append(stockList.get(i)).append(",");
            weightStringBuilder.append(ratios.get(i)/totalWeight).append(",");
        }
        this.symbols = idStringBuilder.toString();
        this.weights = weightStringBuilder.toString();

        this.name = name;
    }

    public List<SimulationStrategy> getSimulationStrategies() {
        return SimulationStrategy.find(SimulationStrategy.class, "simulation = ?", String.valueOf(getId()));
    }

    public void setRealTime(boolean b){ realTime = b; }
    public void setName(String n) { name = n; }
    public void setAccount(double n) { account = n; }
    public void setBank(double n) { bank = n; }
    public void setType(int n) { type = n; }
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

    public boolean isRealTime() {return realTime;}
    public double getBank() {return bank;}
    public double getAccount() {return account;}
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
}
