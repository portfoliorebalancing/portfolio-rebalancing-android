package com.uwaterloo.portfoliorebalancing.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.util.AppUtils;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

import java.util.Collections;
import java.util.Date;

/**
 * Created by lucas on 31/10/16.
 */

public class SimulationSelectorActivity extends AppCompatActivity {

    private String simulationStockSymbol;
    private int simulationStrategy;
    private int simulationType;

    public static final String SIMULATION_STRATEGY = "simulationStrategy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.simulation_selector_activity, null);

        setContentView(view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.add_button);

        getSupportActionBar().setTitle("Pick Stock From Portfolio");
        PickStockSimulationFragment fragment = new PickStockSimulationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.add(R.id.fragment_holder, fragment);
        transaction.commit();
    }

    public void stockSelected(String symbol) {
        simulationStockSymbol = symbol;
        getSupportActionBar().setTitle("Pick Strategy");
        PickStrategySimulationFragment fragment = new PickStrategySimulationFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_holder, fragment);
        transaction.commit();
    }

    public void strategySelected(int strategy, int type) {
        simulationStrategy = strategy;
        simulationType = type;

        if (type == SimulationConstants.HISTORICAL_DATA) {
            HistoricalSimulationFragment fragment = new HistoricalSimulationFragment();
            getSupportActionBar().setTitle("Enter Simulation Info");

            Bundle args = new Bundle();
            args.putInt(SIMULATION_STRATEGY, strategy);
            fragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            transaction.replace(R.id.fragment_holder, fragment);
            transaction.commit();
        } else if (type == SimulationConstants.REAL_TIME_DATA) {
            RealTimeSimulationFragment fragment = new RealTimeSimulationFragment();
            getSupportActionBar().setTitle("Enter Simulation Info");

            Bundle args = new Bundle();
            args.putInt(SIMULATION_STRATEGY, strategy);
            fragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            transaction.replace(R.id.fragment_holder, fragment);
            transaction.commit();
        }
    }

    public void infoSelectedHistorical(String name, double account, Date begin, Date end,
                                       double floor, double multiplier, double optionPrice, double strike) {
        Simulation simulation = new Simulation(simulationStockSymbol, simulationStrategy, simulationType,
                name, account, begin, end, floor, multiplier, optionPrice, strike);

        simulation.save();

        Intent resultIntent = new Intent();
        resultIntent.putExtra(SimulationFragment.SIMULATION_ID, simulation.getId());
        resultIntent.putExtra(SimulationFragment.SIMULATION_TYPE, simulation.getType());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void infoSelectedRealTime(String name, double account, Date begin,
                                     double floor, double multiplier, double optionPrice, double strike) {
        Simulation simulation = new Simulation(simulationStockSymbol, simulationStrategy, simulationType,
                name, account, begin, null, floor, multiplier, optionPrice, strike);

        simulation.save();

        Intent resultIntent = new Intent();
        resultIntent.putExtra(SimulationFragment.SIMULATION_ID, simulation.getId());
        resultIntent.putExtra(SimulationFragment.SIMULATION_TYPE, simulation.getType());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // click on 'up' button in the action bar, handle it here

                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
