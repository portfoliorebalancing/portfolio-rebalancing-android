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
import com.uwaterloo.portfoliorebalancing.ui.activity.AddStrategyActivity;
import com.uwaterloo.portfoliorebalancing.util.PreferenceHelper;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

/**
 * Created by lucas on 16/11/16.
 */

public class AddStrategyFragment extends Fragment {

    private AddStrategyActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddStrategyActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_strategy_fragment, null);

        final RadioButton constantProportionsRadio = (RadioButton)view.findViewById(R.id.constant_proportions_radio);
        final RadioButton cppiRadio = (RadioButton)view.findViewById(R.id.cppi_radio);
        final RadioButton coveredCallRadio = (RadioButton)view.findViewById(R.id.covered_call_radio);
        final RadioButton stopLossRadio = (RadioButton)view.findViewById(R.id.stop_loss_radio);

        final FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.next_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int strategy = 0;
                if (constantProportionsRadio.isChecked()) {
                    strategy = SimulationConstants.CONSTANT_PROPORTIONS;
                } else if (cppiRadio.isChecked()) {
                    strategy = SimulationConstants.CPPI;
                } else if (coveredCallRadio.isChecked()) {
                    strategy = SimulationConstants.CoveredCallWriting;
                } else if (stopLossRadio.isChecked()) {
                    strategy = SimulationConstants.StopLoss;
                }

                activity.strategySelected(strategy);
            }
        });

        //Based on preferences, select strategy.
        int strategy = PreferenceHelper.getPreferredStrategy(getContext());
        switch (strategy) {
            case 1:
                constantProportionsRadio.setChecked(true);
                floatingActionButton.setImageResource(R.drawable.check_mark);
                break;
            case 2:
                cppiRadio.setChecked(true);
                floatingActionButton.setImageResource(R.drawable.next_arrow);
                break;
            case 3:
                coveredCallRadio.setChecked(true);
                floatingActionButton.setImageResource(R.drawable.next_arrow);
                break;
            case 4:
                stopLossRadio.setChecked(true);
                floatingActionButton.setImageResource(R.drawable.next_arrow);
                break;
            default:
                break;
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (constantProportionsRadio.isChecked()) {
                    floatingActionButton.setImageResource(R.drawable.check_mark);
                } else {
                    floatingActionButton.setImageResource(R.drawable.next_arrow);
                }
            }
        };
        constantProportionsRadio.setOnClickListener(listener);
        cppiRadio.setOnClickListener(listener);
        stopLossRadio.setOnClickListener(listener);
        coveredCallRadio.setOnClickListener(listener);

        return view;
    }
}