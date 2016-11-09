package com.uwaterloo.portfoliorebalancing.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

import java.util.ArrayList;
import java.util.List;

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

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.next_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> list = new ArrayList<>();
                int type = 0;
                if (constantProportionsRadio.isChecked()) {
                    list.add(SimulationConstants.CONSTANT_PROPORTIONS);
                }
                if (cppiRadio.isChecked()) {
                    list.add(SimulationConstants.CPPI);
                }
                if (coveredCallRadio.isChecked()) {
                    list.add(SimulationConstants.CoveredCallWriting);
                }
                if (stopLossRadio.isChecked()) {
                    list.add(SimulationConstants.StopLoss);
                }

                if (historicalDataRadio.isChecked()) {
                    type = SimulationConstants.HISTORICAL_DATA;
                } else if (realTimeDataRadio.isChecked()) {
                    type = SimulationConstants.REAL_TIME_DATA;
                }
                activity.strategySelected(list, type);
            }
        });

        return view;
    }
}