package com.uwaterloo.portfoliorebalancing.util;

import android.util.Log;

import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.model.Tick;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yuweixu on 2015-11-01.
 */
public class PortfolioRebalanceUtil {
    public static List<Tick> calculatePortfolioValue(List<Tick>[] stockData, double[] stockWeights,
                                                     int strategy, double bankBalance,
                                                     double accountBalance, int startingIndex,
                                                     double cppi_floor, double multiplier, double optionPrice, double strike) {
        Log.v("SIMULATION STOCKWEIGHTS", Arrays.toString(stockWeights));
        int portfolioSize = stockData.length - 1;
        int numTicks = stockData[0].size();
        double bankValue = bankBalance;
        double portfolioValue = accountBalance;
        long simulationId = stockData[0].get(numTicks - 1).getSimulationId();
        String startDate = stockData[0].get(0).getDate();
        List<Tick> portfolioTicks;
        double floor = accountBalance * cppi_floor;
        if (startingIndex == 0) {
            portfolioTicks = new ArrayList<>();
        }
        else {
            portfolioTicks = stockData[portfolioSize];
        }

        if (strategy == SimulationConstants.CONSTANT_PROPORTIONS) {
            double [] shareSizes = new double [portfolioSize];
            if (startingIndex == 0) {
                rebalance(strategy, portfolioValue, shareSizes, stockWeights, stockData, 0);
                Tick start = new Tick("portfolio", portfolioValue, startDate, simulationId, 0);
                start.save();
                portfolioTicks.add(start);
                startingIndex++;
            }

            for (int i=startingIndex; i<numTicks; i++) {
                portfolioValue = 0;
                for (int j = 0; j < portfolioSize; j++) {
                    portfolioValue += shareSizes[j] * stockData[j].get(i).getPrice();
                }
                Log.v("PORTFOLIO VALUE", portfolioValue + "");
                rebalance(strategy, portfolioValue, shareSizes, stockWeights, stockData, i);
                Tick tick = new Tick("portfolio", portfolioValue, stockData[0].get(i).getDate(), simulationId, i);
                tick.save();
                portfolioTicks.add(tick);
            }
        } else if (strategy == SimulationConstants.CPPI || strategy == SimulationConstants.StopLoss) {
            double [] shareSizes = new double [portfolioSize];
            double arg1 = (strategy == SimulationConstants.CPPI) ? floor : strike;
            double arg2 = (strategy == SimulationConstants.CPPI) ? multiplier : optionPrice;

            if (startingIndex == 0) {
                bankValue = rebalance(strategy, portfolioValue, shareSizes, stockWeights, stockData, bankValue, 0, arg1, arg2);
                Tick start = new Tick("portfolio", portfolioValue, startDate, simulationId, 0);
                start.save();
                portfolioTicks.add(start);
                startingIndex++;
            }

            for (int i=startingIndex; i<numTicks; i++) {
                portfolioValue = 0;
                for (int j = 0; j < portfolioSize; j++) {
                    if (strike == 0) {
                        portfolioValue += shareSizes[j] * stockData[j].get(i).getPrice();
                    } else {
                        portfolioValue += shareSizes[j] * Math.min(strike, stockData[j].get(i).getPrice());
                    }
                }
                portfolioValue += bankValue;
                Log.v("PORTFOLIO VALUE", portfolioValue + "");
                bankValue = rebalance(strategy, portfolioValue, shareSizes, stockWeights, stockData, bankValue, i, arg1, arg2);
                if (strategy == SimulationConstants.StopLoss) {
                    portfolioValue = 0;
                    for (int j = 0; j < portfolioSize; j++) {
                        if (strike == 0) {
                            portfolioValue += shareSizes[j] * stockData[j].get(i).getPrice();
                        } else {
                            portfolioValue += shareSizes[j] * Math.min(strike, stockData[j].get(i).getPrice());
                        }
                    }
                    portfolioValue += bankValue;
                }
                Tick tick = new Tick("portfolio", portfolioValue, stockData[0].get(i).getDate(), simulationId, i);
                tick.save();
                portfolioTicks.add(tick);
            }
        } else if (strategy == SimulationConstants.CoveredCallWriting) {
            double [] shareSizes = new double [portfolioSize];

            if (startingIndex == 0) {
                bankValue = rebalance(strategy, portfolioValue, shareSizes, stockWeights, stockData, bankValue, 0, strike, optionPrice);
                portfolioValue = 0;
                for (int j = 0; j < portfolioSize; j++) {
                    portfolioValue += shareSizes[j] * Math.min(strike, stockData[j].get(0).getPrice());
                }
                portfolioValue += bankValue;
                Tick start = new Tick("portfolio", portfolioValue, startDate, simulationId, 0);
                start.save();
                portfolioTicks.add(start);
                startingIndex++;
            }

            for (int i=startingIndex; i<numTicks; i++) {
                portfolioValue = 0;
                for (int j = 0; j < portfolioSize; j++) {
                    portfolioValue += shareSizes[j] * Math.min(strike, stockData[j].get(i).getPrice());
                }
                portfolioValue += bankValue;
                Log.v("PORTFOLIO VALUE", portfolioValue + "");
                Tick tick = new Tick("portfolio", portfolioValue, stockData[0].get(i).getDate(), simulationId, i);
                tick.save();
                portfolioTicks.add(tick);
            }
        }
        // TODO delete - these are just for debugging
        List<Double> result = new ArrayList<>();
        for (Tick t : portfolioTicks) {
            result.add(t.getPrice());
        }
        List<String> time = new ArrayList<>();
        for (Tick t : portfolioTicks) {
            time.add(t.getDate());
        }
        List<Double> stock = new ArrayList<>();
        for (Tick t : stockData[0]) {
            stock.add(t.getPrice());
        }
        return portfolioTicks;
    }

    public static void rebalance(int strategy, double portfolioValue, double[] shareSizes,
                                   double[] stockWeights, List<Tick>[] stockData, int index) {
        if (strategy == SimulationConstants.CONSTANT_PROPORTIONS) {
            for (int i = 0; i < shareSizes.length; i++) {
                double investAmount = stockWeights[i] * portfolioValue;
                shareSizes[i] = investAmount / stockData[i].get(index).getPrice();
            }
        }
    }

    public static double rebalance(int strategy, double portfolioValue, double[] shareSizes,
                                 double[] stockWeights, List<Tick>[] stockData, double bankBalance, int index, double arg1, double arg2) {

         if (strategy == SimulationConstants.CPPI) {

             double floor = arg1;
             double multiplier = arg2;
             if (index == 0) {
                 double riskyAsset = portfolioValue - floor;
                 for (int i = 0; i < shareSizes.length; i++) {
                     double currentPrice = stockData[i].get(index).getPrice();
                     double investAmount = stockWeights[i] * riskyAsset;
                     double shareSize = multiplier * investAmount / currentPrice;
                     shareSizes[i] = shareSize;
                 }
                 bankBalance = portfolioValue - multiplier * riskyAsset;
             } else {
                 double riskyAsset = Math.max(0, portfolioValue - floor);
                 for (int i = 0; i < shareSizes.length; i++) {
                     double currentPrice = stockData[i].get(index).getPrice();
                     double investAmount = stockWeights[i] * riskyAsset;
                     double shareSize = multiplier * investAmount / currentPrice;
                     bankBalance -= (shareSize - shareSizes[i]) * currentPrice;
                     shareSizes[i] = shareSize;
                 }
             }

         } else if (strategy == SimulationConstants.CoveredCallWriting) {

             double optionPrice = arg2;

             if (index == 0) {
                 bankBalance = 0;
                 for (int i = 0; i < shareSizes.length; i++) {
                     double currentPrice = stockData[i].get(index).getPrice();
                     double investmentAmount = stockWeights[i] * portfolioValue;
                     double shareSize = investmentAmount / currentPrice;
                     shareSizes[i] = shareSize;
                     bankBalance += (shareSizes[i] * optionPrice);
                 }
             }

         } else if (strategy == SimulationConstants.StopLoss) {

             double strike = arg1;
             double optionPrice = arg2;

             if (index == 0) {
                 bankBalance = 0;
                 for (int i = 0; i < shareSizes.length; i++) {
                     double currentPrice = stockData[i].get(index).getPrice();
                     double investmentAmount = stockWeights[i] * portfolioValue;
                     double shareSize = investmentAmount / strike;
                     if (currentPrice > strike) {
                         shareSizes[i] = shareSize;
                         bankBalance += (shareSizes[i] * optionPrice);
                     } else {
                         shareSizes[i] = 0;
                         bankBalance += (shareSize * optionPrice + investmentAmount);
                     }
                 }
             } else {
                 for (int i = 0; i < shareSizes.length; i++) {
                     double lastPrice = stockData[i].get(index-1).getPrice();
                     double currentPrice = stockData[i].get(index).getPrice();
                     if (stockWeights[i] != 0) {
                         if (lastPrice < strike && currentPrice > strike) { // buy stock
                             double investmentAmount = stockWeights[i] * bankBalance;
                             double sharesToBuy = investmentAmount / currentPrice;
                             shareSizes[i] = sharesToBuy;
                             bankBalance -= investmentAmount;
                         } else if (lastPrice > strike && currentPrice < strike) { // sell stock
                             double sharesToSell = shareSizes[i];
                             bankBalance += (sharesToSell * currentPrice);
                             shareSizes[i] = 0;
                         }
                     }
                 }
             }

         }
         return bankBalance;
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public static float[] getChartScale(List<Tick>[] stockData) {
        float[] floats = new float[4];
        floats[0] = Float.MAX_VALUE;
        floats[1] = Float.MIN_VALUE;
        floats[2] = Float.MAX_VALUE;
        floats[3] = Float.MIN_VALUE;
        for (int i=0; i<stockData.length-1; i++) {
            for (int j=0; j<stockData[i].size(); j++) {
                float price = (float) stockData[i].get(j).getPrice();
                if (price < floats[0]) {
                    floats[0] = price;
                }
                else if (price > floats[1]) {
                    floats[1] = price;
                }
            }
        }
        for (int j=0; j<stockData[stockData.length - 1].size(); j++) {
            float price = (float) stockData[stockData.length - 1].get(j).getPrice();
            if (price < floats[2]) {
                floats[2] = price;
            }
            else if (price > floats[3]) {
                floats[3] = price;
            }
        }

        float diff = floats[1] - floats[0];
        float diff2 = floats[3] - floats[2];
        floats[0] -= diff/3;
        floats[1] += diff/3;
        floats[2] -= diff2/3;
        floats[3] += diff2/3;
        return floats;
    }
}
