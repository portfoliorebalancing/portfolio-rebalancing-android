package com.uwaterloo.portfoliorebalancing.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.uwaterloo.portfoliorebalancing.model.GraphData;
import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.model.SimulationStrategy;
import com.uwaterloo.portfoliorebalancing.model.Tick;
import com.uwaterloo.portfoliorebalancing.util.AppUtils;
import com.uwaterloo.portfoliorebalancing.util.PortfolioRebalanceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yuweixu on 15-11-09.
 */
public class DetailRealTimeSimulationActivity extends AppCompatActivity {
    private int[] mStockColors = {R.color.stock_color1, R.color.stock_color2, R.color.stock_color3,
            R.color.stock_color4, R.color.stock_color5, R.color.stock_color6};
    private final String API_KEY = "zrLZruHPruMca17gnA-z";
    protected LineChart mStockChart, mPortfolioChart;
    protected Simulation mSimulation;
    protected Context mContext;
    protected boolean newSimulation;

    private long simulationId;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;

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

        mainLayout = (LinearLayout) findViewById(R.id.layout);
        setUpStockCharts();

        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddStrategyActivity.class);
                startActivityForResult(intent, AddStrategyActivity.ADD_STRATEGY);
            }
        });
    }

    private void setUpStockCharts() {
        mainLayout.removeAllViews();

        mStockChart = new LineChart(this);
        mainLayout.addView(mStockChart);

        mStockChart.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f));

        mPortfolioChart = new LineChart(this);
        mainLayout.addView(mPortfolioChart);
        mPortfolioChart.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f));

        PriceMarkerView mv = new PriceMarkerView(this, R.layout.price_marker_view);
        mStockChart.setMarkerView(mv);
        PriceMarkerView mv2 = new PriceMarkerView(this, R.layout.price_marker_view);
        mPortfolioChart.setMarkerView(mv2);

        // enable scaling and dragging
        mStockChart.setDragEnabled(true);
        mStockChart.setScaleEnabled(true);
        //mStockChart.setScaleXEnabled(false);
        mStockChart.setDrawGridBackground(false);
        mStockChart.getAxisRight().setEnabled(false);
        mStockChart.setBackgroundColor(Color.rgb(255, 255, 255));
        mPortfolioChart.setDragEnabled(true);
        mPortfolioChart.setScaleEnabled(true);
        //mPortfolioChart.setScaleXEnabled(false);
        mPortfolioChart.setDrawGridBackground(false);
        mPortfolioChart.getAxisRight().setEnabled(false);
        mPortfolioChart.setBackgroundColor(Color.rgb(255, 255, 255));

        XAxis xStockAxis = mStockChart.getXAxis();
        xStockAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        XAxis xPortfolioXAxis = mPortfolioChart.getXAxis();
        xPortfolioXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis stockYAxis = mStockChart.getAxisLeft();
        stockYAxis.setStartAtZero(false);
        stockYAxis.setDrawGridLines(false);

        YAxis portfolioYAxis = mPortfolioChart.getAxisLeft();
        portfolioYAxis.setStartAtZero(false);
        portfolioYAxis.setDrawGridLines(false);

        final long finalSimulationId = simulationId;
        mPortfolioChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(mContext, DetailRealTimePortfolioInfoActivity.class);
                intent.putExtra("newSimulation", true);
                intent.putExtra("simulationId", finalSimulationId);
                startActivity(intent);
                return true;
            }
        });

        new CalculateRealTimeSimulationAsyncTask().execute(mSimulation);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (AddStrategyActivity.ADD_STRATEGY) : {
                if (resultCode == Activity.RESULT_OK) {
                    int strategy = data.getIntExtra(AddStrategyActivity.STRATEGY, 1);
                    double floor = data.getDoubleExtra(AddStrategyActivity.FLOOR, 0);
                    double multiplier = data.getDoubleExtra(AddStrategyActivity.MULTIPLIER, 0);
                    double optionPrice = data.getDoubleExtra(AddStrategyActivity.OPTION_PRICE, 0);
                    double strike = data.getDoubleExtra(AddStrategyActivity.STRIKE, 0);
                    SimulationStrategy simulationStrategy = new SimulationStrategy(mSimulation, strategy, floor, multiplier, optionPrice, strike);
                    simulationStrategy.save();

                    //refresh the graphs because a new simulation has been created
                    setUpStockCharts();
                }
                break;
            }
        }
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

    public class CalculateRealTimeSimulationAsyncTask extends AsyncTask<Simulation, Void, GraphData> {
        @Override
        protected GraphData doInBackground(Simulation... params) {
            Simulation simulation = params[0];
            String endDate = AppUtils.getCurrentDateString();
            Log.v("SIMULATION ENDDATE", endDate);
            String[] symbols = simulation.getSymbols();
            long simulationId = simulation.getId();

            List<List<Tick>> simulationTicks = new ArrayList<>();
            List<List<Tick>> stockTicks = new ArrayList<>();

            List<String> dates = new ArrayList<>();

            for (int i=0; i<symbols.length; i++) {
                stockTicks.add(new ArrayList<Tick>());
                String symbol = symbols[i];
                int dateCounter = 0;
                try {
                    String url = "https://quandl.com/api/v3/datasets/WIKI/" + symbol + ".csv?api_key=" + API_KEY + "&start_date=" + simulation.getStartDate() + "&end_date=" + endDate + "&order=asc";
                    Log.v("SIMULATION url", url);
                    URL quandl = new URL(url);
                    URLConnection connection = quandl.openConnection();
                    InputStreamReader is = new InputStreamReader(connection.getInputStream());
                    BufferedReader br = new BufferedReader(is);
                    String[] labels = br.readLine().split(",");
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
                                    Tick copyTick = new Tick(symbol, prevClose, dates.get(d), simulationId);
                                    copyTick.save();
                                    stockTicks.get(i).add(copyTick);
                                }
                                dateCounter = dateIndex;
                            }
                        }
                        // The stock prices are in reverse chronological order.
                        Tick tick = new Tick(symbol, prevClose, date, simulationId);
                        tick.save();
                        stockTicks.get(i).add(tick);
                        dateCounter ++;
                    }
                    mSimulation.save();
                } catch (IOException e) {
                    return null;
                }
            }

            List<SimulationStrategy> strategies = simulation.getSimulationStrategies();
            if (stockTicks.get(0).size() != 0) {
                for (SimulationStrategy strategy : strategies) {
                    List<Tick> portfolioTicks = PortfolioRebalanceUtil.calculatePortfolioValue(
                            stockTicks, simulation.getWeights(), strategy.getStrategy(),
                            simulation.getBank(), simulation.getAccount(), 0, strategy.getFloor(),
                            strategy.getMultiplier(), strategy.getOptionPrice(), strategy.getStrike()
                    );
                    simulationTicks.add(portfolioTicks);
                }
            }

            return new GraphData(strategies, simulationTicks, stockTicks);
        }

        @Override
        protected void onPostExecute(GraphData graphData) {
            if (graphData != null) {
                List<List<Tick>> stockTicks = graphData.getStockTicks();
                List<List<Tick>> simulationTicks = graphData.getSimulationTicks();

                int portfolioSize = stockTicks.size();
                int numTicks = stockTicks.get(0).size();

                if (numTicks == 0) {
                    Toast toast = Toast.makeText(mContext, "No data available since start date!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                List<String> xVals = new ArrayList<>();
                for (int i = 0; i < numTicks; i++) {
                    xVals.add(simulationTicks.get(0).get(i).getDate());
                }

                List<LineDataSet> portfolioSets = new ArrayList<>();
                List<LineDataSet> stockSets = new ArrayList<>();

                for (int j = 0; j < simulationTicks.size(); j++) {
                    List<Tick> simTicks = simulationTicks.get(j);
                    List<Entry> portfolioList = new ArrayList<>();
                    for (int i = 0; i < numTicks; i++) {
                        portfolioList.add(new Entry((float) simTicks.get(i).getPrice(), i));
                    }
                    LineDataSet balanceSet = new LineDataSet(portfolioList, "Portfolio");
                    balanceSet.setColor(ContextCompat.getColor(mContext, R.color.stock_color));
                    balanceSet.setCircleSize(2f);
                    balanceSet.setDrawHorizontalHighlightIndicator(false);
                    balanceSet.setCircleColorHole(ContextCompat.getColor(mContext, R.color.stock_color));
                    balanceSet.setCircleColor(ContextCompat.getColor(mContext, R.color.stock_color));
                    balanceSet.setDrawValues(false);
                    portfolioSets.add(balanceSet);
                }

                // TODO: add a shared preference to allow users to customize number of stocks shown
                int numStocksShown = portfolioSize > 5 ? 5 : portfolioSize;
                for (int i = 0; i<numStocksShown; i++) {
                    List<Entry> stockEntryList = new ArrayList<>();
                    String symbol = stockTicks.get(i).get(0).getSymbol();
                    for (int j = 0; j < numTicks; j++) {
                        stockEntryList.add(new Entry((float) stockTicks.get(i).get(j).getPrice(), j));
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

                mStockChart.getAxisLeft().setSpaceTop(35f);
                mStockChart.getAxisLeft().setSpaceBottom(35f);
                mStockChart.setData(new LineData(xVals, stockSets));
                mStockChart.animateXY(2000, 2000);
                mStockChart.invalidate();
                mPortfolioChart.getAxisLeft().setSpaceTop(30f);
                mPortfolioChart.getAxisLeft().setSpaceBottom(30f);
                mPortfolioChart.setData(new LineData(xVals, portfolioSets));
                mPortfolioChart.animateXY(2000, 2000);
                mPortfolioChart.invalidate();
            }
            else {
                Log.e("SIMULATION error", "Failed to fetch data!");
                Toast toast = Toast.makeText(mContext, "Failed to fetch data!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
