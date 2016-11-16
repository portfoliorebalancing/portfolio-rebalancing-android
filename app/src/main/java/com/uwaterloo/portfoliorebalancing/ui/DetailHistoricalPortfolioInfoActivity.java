package com.uwaterloo.portfoliorebalancing.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.framework.SettingsActivity;
import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.model.Tick;
import com.uwaterloo.portfoliorebalancing.util.PortfolioRebalanceUtil;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by joyce on 2016-07-20.
 */
public class DetailHistoricalPortfolioInfoActivity extends AppCompatActivity {
    private int[] mStockColors = {R.color.stock_color1, R.color.stock_color2, R.color.stock_color3,
            R.color.stock_color4, R.color.stock_color5, R.color.stock_color6};
    private final int STARTING_INDEX = 1000000;
    private final String API_KEY = "zrLZruHPruMca17gnA-z";
    protected LineChart mPortfolioChart;
    protected Simulation mSimulation;
    protected Context mContext;
    protected boolean newSimulation;
    protected LinearLayout mMultiplierContainer;
    protected LinearLayout mFloorContainer;
    protected LinearLayout mOptionPriceContainer;
    protected LinearLayout mStrikeContainer;
    protected TextView mStrategy;
    protected TextView mSimulationType;
    protected TextView mOptionPrice;
    protected TextView mStrike;
    protected TextView mTodayValue;
    protected TextView mFloor;
    protected TextView mMultiplier;
    protected TextView mStartingBal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        long simulationId = -1;
        mContext = this;

        mMultiplierContainer = (LinearLayout) findViewById(R.id.multiplier_container);
        mFloorContainer = (LinearLayout) findViewById(R.id.floor_container);
        mOptionPriceContainer = (LinearLayout) findViewById(R.id.option_price_container);
        mStrikeContainer = (LinearLayout) findViewById(R.id.strike_container);
        mStrategy = (TextView) findViewById(R.id.simulation_strategy);
        mSimulationType = (TextView) findViewById(R.id.simulation_type);
        mOptionPrice = (TextView) findViewById(R.id.simulation_option_price);
        mStrike = (TextView) findViewById(R.id.simulation_strike);
        mTodayValue = (TextView) findViewById(R.id.simulation_today);
        mFloor = (TextView) findViewById(R.id.simulation_floor);
        mMultiplier = (TextView) findViewById(R.id.simulation_multiplier);
        mStartingBal = (TextView) findViewById(R.id.simulation_start_bal);

        Intent intent = getIntent();

        if (intent != null) {
            newSimulation = intent.getBooleanExtra("newSimulation", true);
            simulationId = intent.getLongExtra("simulationId", -1);
        }
        if (simulationId != -1) {
            mSimulation = Simulation.findById(Simulation.class, simulationId);
        }

        TextView nameView = (TextView) findViewById(R.id.simulation_name);
        nameView.setText(mSimulation.getName());
        int strategy = mSimulation.getStrategy();
        if (strategy == SimulationConstants.CONSTANT_PROPORTIONS) {
            mStrategy.setText("Constant Proportions");
        } else if (strategy == SimulationConstants.CoveredCallWriting) {
            mStrategy.setText("Covered Call Writing");
        } else if (strategy == SimulationConstants.StopLoss) {
            mStrategy.setText("Stop loss");
        } else if (strategy == SimulationConstants.CPPI) {
            mStrategy.setText("CPPI");
        }
        mMultiplierContainer.setVisibility(strategy == SimulationConstants.CPPI ? View.VISIBLE : View.GONE);
        mFloorContainer.setVisibility(strategy == SimulationConstants.CPPI ? View.VISIBLE : View.GONE);
        mOptionPriceContainer.setVisibility(mSimulation.getOptionPrice() != 0 ? View.VISIBLE : View.GONE);
        mStrikeContainer.setVisibility(mSimulation.getOptionPrice() != 0 ? View.VISIBLE : View.GONE);

        mSimulationType.setText(mSimulation.getType() == SimulationConstants.HISTORICAL_DATA ? "Historical data" : "Real time data");
        mOptionPrice.setText(mSimulation.getOptionPrice() == 0 ? "N/A" : String.valueOf(mSimulation.getOptionPrice()));
        mStrike.setText(mSimulation.getStrike() == 0 ? "N/A" : String.valueOf(mSimulation.getStrike()));
        mFloor.setText(mSimulation.getCppiFloor() == 0 ? "N/A" : String.valueOf(mSimulation.getCppiFloor()));
        mMultiplier.setText(mSimulation.getCppiMultiplier() == 0 ? "N/A" : String.valueOf(mSimulation.getCppiMultiplier()));
        mStartingBal.setText(String.valueOf(mSimulation.getBank()));

        LinearLayout ll = (LinearLayout) findViewById(R.id.layout);
        mPortfolioChart = new LineChart(this);
        ll.addView(mPortfolioChart, 1);
        mPortfolioChart.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f));

        PriceMarkerView mv2 = new PriceMarkerView(this, R.layout.price_marker_view);
        mPortfolioChart.setMarkerView(mv2);

        mPortfolioChart.setDragEnabled(true);
        mPortfolioChart.setScaleEnabled(true);
        mPortfolioChart.setDrawGridBackground(false);
        mPortfolioChart.getAxisRight().setEnabled(false);
        mPortfolioChart.setBackgroundColor(Color.rgb(255, 255, 255));

        XAxis xPortfolioXAxis = mPortfolioChart.getXAxis();
        xPortfolioXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis portfolioYAxis = mPortfolioChart.getAxisLeft();
        portfolioYAxis.setStartAtZero(false);
        portfolioYAxis.setDrawGridLines(false);

        new CalculateHistoricalSimulationAsyncTask().execute(mSimulation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class CalculateHistoricalSimulationAsyncTask extends AsyncTask<Simulation, Void, List<Tick>[]> {
        @Override
        protected List<Tick>[] doInBackground(Simulation... params) {
            Simulation simulation = params[0];
            String startDate = simulation.getStartDate();
            String endDate = simulation.getEndDate();
            String[] symbols = simulation.getSymbols();
            long simulationId = simulation.getId();
            List<Tick>[] stockPrices = new ArrayList[symbols.length + 1];
            List<String> dates = new ArrayList<>();
            for (int i=0; i<symbols.length; i++) {
                String symbol = symbols[i];
                stockPrices[i] = new ArrayList<Tick>();
                int dateCounter = 0;
                try {
                    //String url = "https://quandl.com/api/v3/datasets/WIKI/" + symbol + ".csv?api_key=" + API_KEY + "&start_date=" + startDate + "&end_date=" + endDate + "&order=asc";
                    String url = "https://quandl.com/api/v3/datasets/WIKI/" + symbol + ".csv?api_key=" + API_KEY + "&start_date=" + startDate + "&end_date=" + endDate + "&order=asc";

                    Log.v("SIMULATION url", url);
                    URL quandl = new URL(url);
                    URLConnection connection = quandl.openConnection();
                    InputStreamReader is = new InputStreamReader(connection.getInputStream());
                    BufferedReader br = new BufferedReader(is);
                    String[] labels = br.readLine().split(",");
                    int index = 0;
                    double prevClose = 0;
                    while (true) {
                        String line = br.readLine();
                        if (line == null || line.equals("")) {
                            break;
                        }
                        String[] stockInfo = line.split(",");
                        Log.v("SIMULATION Stock info", Arrays.toString(stockInfo));
                        String date = stockInfo[0];
                        prevClose = Double.parseDouble(stockInfo[4]);

                        if (i == 0) {
                            dates.add(date);
                        }
                        // TODO: Fix the error when the dates do not line up
                        if (i != 0 && !date.equals(dates.get(dateCounter))) {
                            Log.v("SIMULATION date", date + " " + dates.get(dateCounter));
                            int dateIndex = -1;
                            for (int d=dateCounter + 1; d < dates.size(); d++) {
                                if (dates.get(d) == date) {
                                    dateIndex = d;
                                    break;
                                }
                            }
                            if (dateIndex < 0) {
                                continue;
                            }
                            else {
                                for (int d=dateCounter; d<dateIndex; d++) {
                                    Tick copyTick = new Tick(symbol, prevClose, dates.get(d), simulationId, index);
                                    copyTick.save();
                                    stockPrices[i].add(copyTick);
                                    index++;
                                }
                                dateCounter = dateIndex;
                            }
                        }
                        // The stock prices are in reverse chronological order.
                        Tick tick = new Tick(symbol, prevClose, date, simulationId, index);
                        tick.save();
                        stockPrices[i].add(tick);
                        dateCounter ++;
                        index++;
                    }

                } catch (IOException e) {
                    Log.e("IOException", e.toString());
                    return null;
                }
            }
            Log.v("SIMULATION #symbol", symbols.length + "");
            String logNumTicks = "";
            for (int i=0; i<stockPrices.length - 1; i++) {
                logNumTicks += stockPrices[i].size() + ",";
            }
            Log.v("SIMULATION #stock tick", logNumTicks);
            // Perform rebalancing simulation
            List<Tick> portfolioTicks = PortfolioRebalanceUtil.calculatePortfolioValue(
                    stockPrices, simulation.getWeights(), simulation.getStrategy(),
                    simulation.getBank(), simulation.getAccount(), 0, simulation.getCppiFloor(),
                    simulation.getCppiMultiplier(), simulation.getOptionPrice(), simulation.getStrike()
            );
            stockPrices[symbols.length] = portfolioTicks;
            Log.v("SIMULATION #port tick", stockPrices[symbols.length].size() +"");
            return stockPrices;
        }

        @Override
        protected void onPostExecute(List<Tick>[] portfolioData) {
            if (portfolioData != null) {

                int portfolioSize = portfolioData.length - 1;
                int numTicks = portfolioData[0].size();
                List<Tick> portfolioTicks = portfolioData[portfolioSize];

                List<String> xVals = new ArrayList<>();
                for (int i = 0; i < numTicks; i++) {
                    xVals.add(portfolioTicks.get(i).getDate());
                }

                List<LineDataSet> portfolioSets = new ArrayList<>();
                List<LineDataSet> stockSets = new ArrayList<>();
                List<Entry> portfolioList = new ArrayList<>();
                for (int i = 0; i < numTicks; i++) {
                    portfolioList.add(new Entry((float) portfolioTicks.get(i).getPrice(), i));
                }
                LineDataSet balanceSet = new LineDataSet(portfolioList, "Portfolio");
                balanceSet.setColor(ContextCompat.getColor(mContext, R.color.stock_color));
                balanceSet.setCircleSize(2f);
                balanceSet.setDrawHorizontalHighlightIndicator(false);
                balanceSet.setCircleColorHole(ContextCompat.getColor(mContext, R.color.stock_color));
                balanceSet.setCircleColor(ContextCompat.getColor(mContext, R.color.stock_color));
                balanceSet.setDrawValues(false);
                portfolioSets.add(balanceSet);
                // TODO: add a shared preference to allow users to customize number of stocks shown
                int numStocksShown = portfolioSize > 5 ? 5 : portfolioSize;
                for (int i = 0; i<numStocksShown; i++) {
                    List<Entry> stockEntryList = new ArrayList<>();
                    String symbol = portfolioData[i].get(0).getSymbol();
                    for (int j = 0; j < numTicks; j++) {
                        stockEntryList.add(new Entry((float) portfolioData[i].get(j).getPrice(), j));
                    }

                    LineDataSet lineDataSet = new LineDataSet(stockEntryList, symbol);
                    lineDataSet.setColor(ContextCompat.getColor(mContext, mStockColors[i]));
                    lineDataSet.setCircleColor(ContextCompat.getColor(mContext, mStockColors[i]));
                    lineDataSet.setCircleColorHole(ContextCompat.getColor(mContext, mStockColors[i]));
                    lineDataSet.setCircleSize(2f);
                    lineDataSet.setDrawHorizontalHighlightIndicator(false);
                    lineDataSet.setDrawValues(false);
                    stockSets.add(lineDataSet);
                }

                mPortfolioChart.getAxisLeft().setSpaceTop(30f);
                mPortfolioChart.getAxisLeft().setSpaceBottom(30f);
                mPortfolioChart.setData(new LineData(xVals, portfolioSets));
                mPortfolioChart.animateXY(2000, 2000);
                mPortfolioChart.invalidate();
                if (portfolioTicks != null) {
                    mTodayValue.setText(String.format("%.02f", portfolioTicks.get(portfolioTicks.size() - 1).getPrice()));
                }
            }
            else {
                Log.e("SIMULATION error", "Failed to fetch data!");
                Toast toast = Toast.makeText(mContext, "Failed to fetch data!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
