package com.uwaterloo.portfoliorebalancing.ui.extra;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

/**
 * Created by joyce on 2016-08-27.
 */
public class ChangeStrategyDialogFragment extends DialogFragment {

    private long simulationId;
    private Simulation mSimulation;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        simulationId = getArguments().getLong("simulationId");
        mSimulation = Simulation.findById(Simulation.class, simulationId);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.change_simulation_dialog, null);

        final RadioButton constantProportionsRadio = (RadioButton) rootView.findViewById(R.id.constant_proportions_radio);
       // final RadioButton leveragedTwoTimesRadio = (RadioButton) rootView.findViewById(R.id.leveraged_two_times_radio);
        final RadioButton cppiRadio = (RadioButton) rootView.findViewById(R.id.cppi_radio);
        final RadioButton coveredCallRadio = (RadioButton) rootView.findViewById(R.id.covered_call_radio);
        final RadioButton stopLossRadio = (RadioButton) rootView.findViewById(R.id.stop_loss_radio);

        final EditText cppiFloorText = (EditText) rootView.findViewById(R.id.cppi_floor_input);
        final EditText multiplierText = (EditText) rootView.findViewById(R.id.cppi_multiplier_input);
        final EditText optionPriceText = (EditText) rootView.findViewById(R.id.option_price_input);
        final EditText strikeText = (EditText) rootView.findViewById(R.id.strike_input);

        constantProportionsRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cppiFloorText.setEnabled(false);
                multiplierText.setEnabled(false);
                optionPriceText.setEnabled(false);
                strikeText.setEnabled(false);
            }
        });

       /* leveragedTwoTimesRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cppiFloorText.setEnabled(false);
                multiplierText.setEnabled(false);
                optionPriceText.setEnabled(false);
                strikeText.setEnabled(false);
            }
        });*/

        cppiRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cppiFloorText.setEnabled(true);
                multiplierText.setEnabled(true);
                optionPriceText.setEnabled(false);
                strikeText.setEnabled(false);
            }
        });

        coveredCallRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cppiFloorText.setEnabled(false);
                multiplierText.setEnabled(false);
                optionPriceText.setEnabled(true);
                strikeText.setEnabled(true);
            }
        });

        stopLossRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cppiFloorText.setEnabled(false);
                multiplierText.setEnabled(false);
                optionPriceText.setEnabled(true);
                strikeText.setEnabled(true);
            }
        });

        builder.setView(rootView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int strategy = 0, type = 0;
                        if (constantProportionsRadio.isChecked()) {
                            strategy = SimulationConstants.CONSTANT_PROPORTIONS;
                        //} else if (leveragedTwoTimesRadio.isChecked()) {
                        //    // set strategy
                        } else if (cppiRadio.isChecked()) {
                            strategy = SimulationConstants.CPPI;
                            mSimulation.setCppiFloor(Double.parseDouble(cppiFloorText.getText().toString()));
                            mSimulation.setCppiMultiplier(Double.parseDouble(multiplierText.getText().toString()));
                        } else if (coveredCallRadio.isChecked()) {
                            strategy = SimulationConstants.CoveredCallWriting;
                            mSimulation.setOptionPrice(Double.parseDouble(optionPriceText.getText().toString()));
                            mSimulation.setStrike(Double.parseDouble(strikeText.getText().toString()));
                        } else if (stopLossRadio.isChecked()) {
                            strategy = SimulationConstants.StopLoss;
                            mSimulation.setOptionPrice(Double.parseDouble(optionPriceText.getText().toString()));
                            mSimulation.setStrike(Double.parseDouble(strikeText.getText().toString()));
                        }

                        //mSimulation.setStrategy(strategy);
                        mSimulation.save();

                        ChangeStrategyDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.cancel_simulation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel
                        ChangeStrategyDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
