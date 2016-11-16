package com.uwaterloo.portfoliorebalancing.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.util.AppUtils;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lucas on 01/11/16.
 */

public class HistoricalSimulationFragment extends Fragment {

    private Calendar calendar = Calendar.getInstance(Locale.CANADA);

    private Date startDate;
    private Date endDate;

    private SimulationSelectorActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SimulationSelectorActivity)getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startDate = AppUtils.getFirstOfMonth();
        endDate = AppUtils.getCurrentDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.historical_simulation_fragment, null);

        final EditText nameText = (EditText)view.findViewById(R.id.simulation_name_edit);
        final EditText accountText = (EditText)view.findViewById(R.id.simulation_account_balance);
//        final EditText bankText = (EditText) rootView.findViewById(R.id.simulation_bank_balance);
        final TextView startEditText = (TextView)view.findViewById(R.id.simulation_start_date);
        final TextView endEditText = (TextView)view.findViewById(R.id.simulation_end_date);
        final TextView errorMessage = (TextView)view.findViewById(R.id.dialog_error_message);
        ImageView startDateCalendar = (ImageView)view.findViewById(R.id.start_date_calendar);
        ImageView endDateCalendar = (ImageView)view.findViewById(R.id.end_date_calendar);

        final LinearLayout floor = (LinearLayout)view.findViewById(R.id.floor);
        final LinearLayout multiplier = (LinearLayout)view.findViewById(R.id.multiplier);
        final LinearLayout optionPrice = (LinearLayout)view.findViewById(R.id.option_price);
        final LinearLayout strike = (LinearLayout)view.findViewById(R.id.strike);

        final EditText floorText = (EditText)view.findViewById(R.id.floor_input);
        final EditText multiplierText = (EditText)view.findViewById(R.id.multiplier_input);
        final EditText optionPriceText = (EditText)view.findViewById(R.id.option_price_input);
        final EditText strikeText = (EditText)view.findViewById(R.id.strike_input);

        Bundle args = getArguments();
        final int strategy = args.getInt(SimulationSelectorActivity.SIMULATION_STRATEGY);
        if (strategy == SimulationConstants.CPPI) {
            floor.setVisibility(View.VISIBLE);
            multiplier.setVisibility(View.VISIBLE);
        } else if (strategy == SimulationConstants.CoveredCallWriting || strategy == SimulationConstants.StopLoss) {
            optionPrice.setVisibility(View.VISIBLE);
            strike.setVisibility(View.VISIBLE);
        }

        startEditText.setText(AppUtils.formatDisplayDate(startDate));
        endEditText.setText(AppUtils.formatDisplayDate(endDate));

        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate = AppUtils.getDate(year, month, dayOfMonth);
                startEditText.setText(AppUtils.formatDisplayDate(startDate));
            }
        };

        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDate = AppUtils.getDate(year, month, dayOfMonth);
                endEditText.setText(AppUtils.formatDisplayDate(endDate));
            }
        };

        startDateCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setTime(startDate);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), startDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        endDateCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setTime(endDate);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), endDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.next_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String accountString = accountText.getText().toString();
                double account = 0.0;
                try {
                    account = Double.parseDouble(accountString);
                }
                catch (NumberFormatException e) {
                    //mSimulation.delete();
                    //Toast toast = Toast.makeText(getContext(), "Account balance must be valid.", Toast.LENGTH_SHORT);
                    //toast.show();
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.account_balance_error);
                    return;
                }
                //                        double bank = Double.parseDouble(bankText.getText().toString());

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

                if (startDate.before(endDate) && account > 0.0) {
                    activity.infoSelectedHistorical(name, account, startDate, endDate, floorValue, multiplierValue, optionPriceValue, strikeValue);
                }
                else if (account <= 0.0){
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.negative_account_balance_error);
                }
                else {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.historical_date_error);
                }
            }
        });

        return view;
    }
}
