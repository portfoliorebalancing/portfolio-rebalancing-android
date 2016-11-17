package com.uwaterloo.portfoliorebalancing.model;

import com.orm.SugarRecord;

/**
 * Created by lucas on 16/11/16.
 */

public class SimulationStrategy extends SugarRecord<SimulationStrategy> {
    private Simulation simulation;

    private int strategy;
    private double floor;
    private double multiplier;
    private double optionPrice;
    private double strike;

    public SimulationStrategy() {}

    public SimulationStrategy(Simulation simulation, int strategy, double floor, double multiplier, double optionPrice, double strike) {
        this.simulation = simulation;
        this.strategy = strategy;
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

    public int getStrategy() {
        return strategy;
    }
}
