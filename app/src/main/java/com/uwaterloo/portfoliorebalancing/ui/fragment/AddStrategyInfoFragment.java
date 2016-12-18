package com.uwaterloo.portfoliorebalancing.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.ui.activity.AddStrategyActivity;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

/**
 * Created by lucas on 22/11/16.
 */

public class AddStrategyInfoFragment extends Fragment {

    private AddStrategyActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddStrategyActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_strategy_info_fragment, null);

        final TextView errorMessage = (TextView)view.findViewById(R.id.dialog_error_message);

        final LinearLayout floor = (LinearLayout)view.findViewById(R.id.floor);
        final LinearLayout multiplier = (LinearLayout)view.findViewById(R.id.multiplier);
        final LinearLayout optionPrice = (LinearLayout)view.findViewById(R.id.option_price);
        final LinearLayout strike = (LinearLayout)view.findViewById(R.id.strike);

        final EditText floorText = (EditText)view.findViewById(R.id.floor_input);
        final EditText multiplierText = (EditText)view.findViewById(R.id.multiplier_input);
        final EditText optionPriceText = (EditText)view.findViewById(R.id.option_price_input);
        final EditText strikeText = (EditText)view.findViewById(R.id.strike_input);

        Bundle args = getArguments();
        final int strategy = args.getInt(AddStrategyActivity.ADDED_STRATEGY);
        if (strategy == SimulationConstants.CPPI) {
            floor.setVisibility(View.VISIBLE);
            multiplier.setVisibility(View.VISIBLE);
        } else if (strategy == SimulationConstants.CoveredCallWriting || strategy == SimulationConstants.StopLoss) {
            optionPrice.setVisibility(View.VISIBLE);
            strike.setVisibility(View.VISIBLE);
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.next_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String floorString = floorText.getText().toString();
                String multiplierString = multiplierText.getText().toString();
                String optionPriceString = optionPriceText.getText().toString();
                String strikeString = strikeText.getText().toString();
                double floorValue = 0.0;
                double multiplierValue = 0.0;
                double optionPriceValue = 0.0;
                double strikeValue = 0.0;

                if (strategy == SimulationConstants.CPPI) {
                    try {
                        floorValue = Double.parseDouble(floorString);
                    }
                    catch (NumberFormatException e) {
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText("Please enter a floor.");
                        return;
                    }
                    try {
                        multiplierValue = Double.parseDouble(multiplierString);
                    }
                    catch (NumberFormatException e) {
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText("Please enter a multiplier.");
                        return;
                    }
                } else if (strategy == SimulationConstants.CoveredCallWriting || strategy == SimulationConstants.StopLoss) {
                    try {
                        optionPriceValue = Double.parseDouble(optionPriceString);
                    }
                    catch (NumberFormatException e) {
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText("Please enter an option price.");
                        return;
                    }
                    try {
                        strikeValue = Double.parseDouble(strikeString);
                    }
                    catch (NumberFormatException e) {
                        errorMessage.setVisibility(View.VISIBLE);
                        errorMessage.setText("Please enter a strike.");
                        return;
                    }
                }

                activity.infoSelected(floorValue, multiplierValue, optionPriceValue, strikeValue);
            }
        });

        return view;
    }
}