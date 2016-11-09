package com.uwaterloo.portfoliorebalancing.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.uwaterloo.portfoliorebalancing.MainActivity;
import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jocye on 2016-06-16.
 */
public class CoveredCallWritingDialogFragment extends DialogFragment {
    private long simulationId;
    private Simulation mSimulation;
    private MainActivity mainActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        simulationId = getArguments().getLong("simulationId");
        mSimulation = Simulation.findById(Simulation.class, simulationId);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.strike_and_option_price_fragment, null);

        //final RecyclerView weightsView = (RecyclerView) rootView.findViewById(R.id.covered_call_simulation_weights_list);
        final EditText strikeView = (EditText) rootView.findViewById(R.id.covered_call_strike_input);
        final EditText optionPriceView = (EditText) rootView.findViewById(R.id.covered_call_option_price_input);
        mainActivity = (MainActivity) getActivity();
        //weightsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //weightsView.setAdapter(new StockWeightAdapter(mSimulation.getSymbolsList(), mainActivity));

        builder.setView(rootView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        mSimulation.setStrike(Double.parseDouble(strikeView.getText().toString()));
                        mSimulation.setOptionPrice(Double.parseDouble(optionPriceView.getText().toString()));
                        List<Double> weights = new ArrayList<>();
          //              for (int i=0; i<weightsView.getChildCount(); i++) {
            //                EditText weightText = (EditText) weightsView.getChildAt(i).findViewById(R.id.stock_weight);
              //              String weightString = weightText.getText().toString();
                //            weights.add(weightString.equals("") ? 0 : Double.parseDouble(weightString));
                  //      }
                        // TODO: Check if date is valid instead of matching regex
                        //mSimulation.setWeights(weights);
                        mSimulation.save();
                        mainActivity.updateSimulationFragment();
                        if (mSimulation.isRealTime()) {
                            Intent intent = new Intent(getContext(), DetailRealTimeSimulationActivity.class);
                            intent.putExtra("newSimulation", true);
                            intent.putExtra("simulationId", simulationId);
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(getContext(), DetailHistoricalSimulationActivity.class);
                            intent.putExtra("newSimulation", true);
                            intent.putExtra("simulationId", simulationId);
                            startActivity(intent);
                        }
                    }

                })
                .setNegativeButton(R.string.cancel_simulation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel
                        mSimulation.delete();
                        CoveredCallWritingDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog =  builder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }
}
