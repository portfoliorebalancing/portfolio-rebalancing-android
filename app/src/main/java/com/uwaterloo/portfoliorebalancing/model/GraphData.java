package com.uwaterloo.portfoliorebalancing.model;

import java.util.List;

/**
 * Created by lucas on 16/11/16.
 */

public class GraphData {
    //For each simulation strategy, there is a List<Tick> for the graph
    private SimulationStrategies simulationStrategies;
    private List<List<Tick>> simulationTicks;

    private List<List<Tick>> stockTicks;

    public GraphData(SimulationStrategies strategies, List<List<Tick>> simTicks, List<List<Tick>> sTicks) {
        simulationStrategies = strategies;
        simulationTicks = simTicks;
        stockTicks = sTicks;
    }

    public SimulationStrategies getSimulationStrategies() {
        return simulationStrategies;
    }

    public List<List<Tick>> getSimulationTicks() {
        return simulationTicks;
    }

    public List<List<Tick>> getStockTicks() {
        return stockTicks;
    }
}
