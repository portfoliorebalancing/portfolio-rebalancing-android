package com.uwaterloo.portfoliorebalancing.util;

import com.uwaterloo.portfoliorebalancing.model.SimulationStrategy;

/**
 * Created by yuweixu on 15-11-08.
 */
public class SimulationConstants {
    public static final int CONSTANT_PROPORTIONS = 1;
    public static final int CPPI = 2;
    public static final int CoveredCallWriting = 3;
    public static final int StopLoss = 4;
    public static final int HISTORICAL_DATA = 100;
    public static final int REAL_TIME_DATA = 101;

    public static String getStrategyShortName(int strategy) {
        switch (strategy) {
            case CONSTANT_PROPORTIONS:
                return "CP";
            case CPPI:
                return "CPPI";
            case CoveredCallWriting:
                return  "CCW";
            case StopLoss:
                return "SL";
            default:
                throw new IllegalArgumentException("Invalid strategy constant");
        }
    }

    public static String getSimulationStrategyInfoShort(SimulationStrategy strategy) {
        switch (strategy.getStrategy()) {
            case CONSTANT_PROPORTIONS:
                return "CP";
            case CPPI:
                return "CPPI: F(" + strategy.getFloor() + "), M(" + strategy.getMultiplier() + ")";
            case CoveredCallWriting:
                return "CCW: O(" + strategy.getOptionPrice() + "), S(" + strategy.getStrike() + ")";
            case StopLoss:
                return "SL: O(" + strategy.getOptionPrice() + "), S(" + strategy.getStrike() + ")";
            default:
                throw new IllegalArgumentException("Invalid strategy constant");
        }
    }
}
