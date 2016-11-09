package com.uwaterloo.portfoliorebalancing.util;

import android.support.v4.util.Pair;

import com.uwaterloo.portfoliorebalancing.model.Tick;

import java.util.List;

/**
 * Created by lucas on 09/11/16.
 */

public class GraphData {
    private List<Tick> stockTicks;
    //Each element of this list is a strategy int and a list of simulation ticks for that strategy
    private List<Pair<Integer, List<Tick>>> simulationTicks;

    public GraphData(List<Tick> stock, List<Pair<Integer, List<Tick>>> simulation) {
        stockTicks = stock;
        simulationTicks = simulation;
    }

    public List<Tick> getStockTicks() {
        return stockTicks;
    }

    public List<Pair<Integer, List<Tick>>> getSimulationTicks() {
        return simulationTicks;
    }
}