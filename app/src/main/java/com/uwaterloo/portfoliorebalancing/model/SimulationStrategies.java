package com.uwaterloo.portfoliorebalancing.model;

/**
 * Created by lucas on 16/11/16.
 */

import android.support.v4.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * This class houses all the data for the multiple strategies that a simulation has.
 */
class SimulationStrategies implements Serializable {

    private static class StrategyData {
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
    }

    private List<Pair<Integer, StrategyData>> data;

    public SimulationStrategies(int strategy, double floor, double multiplier, double optionPrice, double strike) {
        data = Collections.singletonList(new Pair<>(strategy, new StrategyData(floor, multiplier, optionPrice, strike)));
    }

    public void addStrategy(int strategy, double floor, double multiplier, double optionPrice, double strike) {
        data.add(new Pair<>(strategy, new StrategyData(floor, multiplier, optionPrice, strike)));
    }

    public List<Pair<Integer, StrategyData>> getData() {
        return data;
    }

    /** Read the object from string. */
    private static SimulationStrategies fromString(String serializedObject) throws IOException, ClassNotFoundException {
        SimulationStrategies obj = null;
        try {
            byte b[] = serializedObject.getBytes();
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            obj = (SimulationStrategies) si.readObject();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return obj;
    }

    /** Write the object to a string. */
    private static String toString(Serializable o) {
        ByteArrayOutputStream bo =new ByteArrayOutputStream();
        try {
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(o);
            so.flush();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return bo.toString();
    }
}
