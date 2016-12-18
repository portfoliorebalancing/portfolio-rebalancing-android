package com.uwaterloo.portfoliorebalancing.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.ui.activity.SimulationSelectorActivity;
import com.uwaterloo.portfoliorebalancing.util.PreferenceHelper;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

/**
 * Created by lucas on 01/11/16.
 */

public class PickStrategySimulationFragment extends Fragment {

    private SimulationSelectorActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SimulationSelectorActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pick_strategy_simulation_fragment, null);

        final RadioButton constantProportionsRadio = (RadioButton)view.findViewById(R.id.constant_proportions_radio);
        final RadioButton cppiRadio = (RadioButton)view.findViewById(R.id.cppi_radio);
        final RadioButton coveredCallRadio = (RadioButton)view.findViewById(R.id.covered_call_radio);
        final RadioButton stopLossRadio = (RadioButton)view.findViewById(R.id.stop_loss_radio);
        final RadioButton historicalDataRadio = (RadioButton)view.findViewById(R.id.historical_data_radio);
        final RadioButton realTimeDataRadio = (RadioButton)view.findViewById(R.id.real_time_data_radio);

        //Based on preferences, select strategy.
        int strategy = PreferenceHelper.getPreferredStrategy(getContext());
        switch (strategy) {
            case 1:
                constantProportionsRadio.setChecked(true);
                break;
            case 2:
                cppiRadio.setChecked(true);
                break;
            case 3:
                coveredCallRadio.setChecked(true);
                break;
            case 4:
                stopLossRadio.setChecked(true);
                break;
            default:
                break;
        }

        //Based on preferences, select type.
        int type = PreferenceHelper.getPreferredType(getContext());
        switch (type) {
            case 1:
                historicalDataRadio.setChecked(true);
                break;
            case 2:
                realTimeDataRadio.setChecked(true);
                break;
            default:
                break;
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.next_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int strategy = 0, type = 0;
                if (constantProportionsRadio.isChecked()) {
                    strategy = SimulationConstants.CONSTANT_PROPORTIONS;
                } else if (cppiRadio.isChecked()) {
                    strategy = SimulationConstants.CPPI;
                } else if (coveredCallRadio.isChecked()) {
                    strategy = SimulationConstants.CoveredCallWriting;
                } else if (stopLossRadio.isChecked()) {
                    strategy = SimulationConstants.StopLoss;
                }

                if (historicalDataRadio.isChecked()) {
                    type = SimulationConstants.HISTORICAL_DATA;
                } else if (realTimeDataRadio.isChecked()) {
                    type = SimulationConstants.REAL_TIME_DATA;
                }
                activity.strategySelected(strategy, type);
            }
        });

        return view;
    }
}