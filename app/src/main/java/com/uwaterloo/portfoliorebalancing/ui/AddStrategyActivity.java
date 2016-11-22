package com.uwaterloo.portfoliorebalancing.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

/**
 * Created by lucas on 16/11/16.
 */

public class AddStrategyActivity extends AppCompatActivity {

    private int simulationStrategy;

    public static final String ADDED_STRATEGY = "strategyAdded";
    public static final int ADD_STRATEGY = 1003;
    public static final String STRATEGY = "addedStrategy";
    public static final String MULTIPLIER = "multiplier";
    public static final String FLOOR = "floor";
    public static final String OPTION_PRICE = "optionPrice";
    public static final String STRIKE = "strike";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.simulation_selector_activity, null);

        setContentView(view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Pick Strategy");
        AddStrategyFragment fragment = new AddStrategyFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragment_holder, fragment);
        transaction.commit();
    }

    public void strategySelected(int strategy) {
        simulationStrategy = strategy;

        if (strategy == SimulationConstants.CONSTANT_PROPORTIONS) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(STRATEGY, simulationStrategy);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            Bundle args = new Bundle();
            args.putInt(ADDED_STRATEGY, simulationStrategy);

            getSupportActionBar().setTitle("Select Strategy Info");
            AddStrategyInfoFragment fragment = new AddStrategyInfoFragment();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
            transaction.replace(R.id.fragment_holder, fragment);
            transaction.commit();
        }
    }

    public void infoSelected(double floorValue, double multiplierValue, double optionPriceValue, double strikeValue) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(STRATEGY, simulationStrategy);
        resultIntent.putExtra(FLOOR, floorValue);
        resultIntent.putExtra(MULTIPLIER, multiplierValue);
        resultIntent.putExtra(OPTION_PRICE, optionPriceValue);
        resultIntent.putExtra(STRIKE, strikeValue);
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
