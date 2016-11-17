package com.uwaterloo.portfoliorebalancing.model;

/**
 * Created by lucas on 16/11/16.
 */

import android.support.v4.util.Pair;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class houses all the data for the multiple strategies that a simulation has.
 */
public class SimulationStrategies implements Serializable {

    public static class StrategyPair implements Serializable {
        public Integer first;
        public StrategyData second;

        public StrategyPair(Integer f, StrategyData s) {
            first = f;
            second = s;
        }
    }

    public static class StrategyData implements Serializable {
        private double floor;
        private double multiplier;
        private double optionPrice;
        private double strike;

        public StrategyData(double floor, double multiplier, double optionPrice, double strike) {
            this.floor = floor;
            this.multiplier = multiplier;
            this.optionPrice = optionPrice;
            this.strike = strike;
        }

        public double getFloor() {
            return floor;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public double getOptionPrice() {
            return optionPrice;
        }

        public double getStrike() {
            return strike;
        }
    }

    private List<StrategyPair> data = new ArrayList<>();

    public SimulationStrategies(int strategy, double floor, double multiplier, double optionPrice, double strike) {
        data.add(new StrategyPair(strategy, new StrategyData(floor, multiplier, optionPrice, strike)));
    }

    public void addStrategy(int strategy, double floor, double multiplier, double optionPrice, double strike) {
        data.add(new StrategyPair(strategy, new StrategyData(floor, multiplier, optionPrice, strike)));
    }

    public List<StrategyPair> getData() {
        return data;
    }

    /** Read the object from Base64 string. */
    public static SimulationStrategies fromString(String s) throws IOException , ClassNotFoundException {
        byte [] data = Base64.decode(s, Base64.DEFAULT);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        SimulationStrategies o  = (SimulationStrategies)ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    public static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject(o);
        oos.close();
        return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
    }
}
