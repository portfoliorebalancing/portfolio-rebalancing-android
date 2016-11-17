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
import com.uwaterloo.portfoliorebalancing.model.SimulationStrategies;
import com.uwaterloo.portfoliorebalancing.util.AppUtils;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

import java.util.Collections;
import java.util.Date;

/**
 * Created by lucas on 16/11/16.
 */

public class AddStrategyActivity extends AppCompatActivity {

    private int simulationStrategy;

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
        getSupportActionBar().setIcon(R.drawable.add_button);

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
        }

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
