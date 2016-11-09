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
        //final RadioButton leveragedTwoTimesRadio = (RadioButton)view.findViewById(R.id.leveraged_two_times_radio);
        final RadioButton cppiRadio = (RadioButton)view.findViewById(R.id.cppi_radio);
        final RadioButton coveredCallRadio = (RadioButton)view.findViewById(R.id.covered_call_radio);
        final RadioButton stopLossRadio = (RadioButton)view.findViewById(R.id.stop_loss_radio);
        final RadioButton historicalDataRadio = (RadioButton)view.findViewById(R.id.historical_data_radio);
        final RadioButton realTimeDataRadio = (RadioButton)view.findViewById(R.id.real_time_data_radio);
/*
        builder.setView(rootView)
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int strategy = 0, type = 0;
                        if (constantProportionsRadio.isChecked()) {
                            strategy = SimulationConstants.CONSTANT_PROPORTIONS;
                        } else if (leveragedTwoTimesRadio.isChecked()) {
                            // set strategy
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

                        mSimulation.setStrategy(strategy);
                        mSimulation.setType(type);
                        mSimulation.save();

                        if (type == SimulationConstants.HISTORICAL_DATA) {
                            HistoricalSimulationDialogFragment fragment = new HistoricalSimulationDialogFragment();
                            Bundle args = new Bundle();
                            args.putLong("simulationId", simulationId);
                            fragment.setArguments(args);
                            fragment.show(getActivity().getSupportFragmentManager(), "simulation_details");
                        }
                        else if (type == SimulationConstants.REAL_TIME_DATA) {
                            RealTimeSimulationDialogFragment fragment = new RealTimeSimulationDialogFragment();
                            Bundle args = new Bundle();
                            args.putLong("simulationId", simulationId);
                            fragment.setArguments(args);
                            fragment.show(getActivity().getSupportFragmentManager(), "simulation_details");
                        }
                        AddSimulationDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.cancel_simulation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel
                        AddSimulationDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();*/

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.next_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int strategy = 0, type = 0;
                if (constantProportionsRadio.isChecked()) {
                    strategy = SimulationConstants.CONSTANT_PROPORTIONS;
                //} else if (leveragedTwoTimesRadio.isChecked()) {
                    // set strategy
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