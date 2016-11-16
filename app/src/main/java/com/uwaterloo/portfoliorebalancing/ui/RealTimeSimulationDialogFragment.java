package com.uwaterloo.portfoliorebalancing.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.util.AppUtils;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yuweixu on 15-11-16.
 */
public class RealTimeSimulationDialogFragment extends DialogFragment {

    private long simulationId;
    private Simulation mSimulation;
    //private final String dateRegex = "^[0-9]{4}.[0-9]{2}.[0-9]{2}$";

    private Calendar calendar = Calendar.getInstance(Locale.CANADA);
    private Date startDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        simulationId = getArguments().getLong("simulationId");
        mSimulation = Simulation.findById(Simulation.class, simulationId);
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        startDate = AppUtils.getFirstOfMonth();

        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.real_time_simulation_fragment);

        final EditText nameText = (EditText) dialog.findViewById(R.id.simulation_name_edit);
        final EditText accountText = (EditText) dialog.findViewById(R.id.simulation_account_balance);
        final TextView startEditText = (TextView) dialog.findViewById(R.id.simulation_start_date);
        final Button next_button = (Button) dialog.findViewById(R.id.next_button);
        final Button cancel_button = (Button) dialog.findViewById(R.id.cancel_button);
        final TextView errorMessage = (TextView) dialog.findViewById(R.id.dialog_error_message);
        ImageView startDateCalendar = (ImageView) dialog.findViewById(R.id.start_date_calendar);

        startEditText.setText(AppUtils.formatDisplayDate(startDate));

        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate = AppUtils.getDate(year, month, dayOfMonth);
                startEditText.setText(AppUtils.formatDisplayDate(startDate));
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

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSimulation.delete();
                RealTimeSimulationDialogFragment.this.getDialog().cancel();
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String accountString = accountText.getText().toString();
                double account = 0.0;
                try {
                    account = Double.parseDouble(accountString);
                }
                catch (NumberFormatException e) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.account_balance_error);
                    return;
                }

                // TODO: Check if date is valid instead of matching regex
                if (startDate.before(new Date()) && account > 0.0) {
                    mSimulation.setName(name);
                    mSimulation.setAccount(account);
                    mSimulation.setStartDate(AppUtils.formatDate(startDate));
                    mSimulation.setLastDate(AppUtils.formatDate(startDate));
                    mSimulation.setRealTime(true);
                    mSimulation.save();

                    if (mSimulation.getStrategy() == SimulationConstants.CONSTANT_PROPORTIONS) {
                        ConstantProportionsDialogFragment fragment = new ConstantProportionsDialogFragment();
                        Bundle args = new Bundle();
                        args.putLong("simulationId", simulationId);
                        fragment.setArguments(args);
                        fragment.show(getActivity().getSupportFragmentManager(), "simulation_details");
                    } else if (mSimulation.getStrategy() == SimulationConstants.CPPI) {
                        CPPIDialogFragment fragment = new CPPIDialogFragment();
                        Bundle args = new Bundle();
                        args.putLong("simulationId", simulationId);
                        fragment.setArguments(args);
                        fragment.show(getActivity().getSupportFragmentManager(), "simulation_details");
                    } else if (mSimulation.getStrategy() == SimulationConstants.CoveredCallWriting) {
                        CoveredCallWritingDialogFragment fragment = new CoveredCallWritingDialogFragment();
                        Bundle args = new Bundle();
                        args.putLong("simulationId", simulationId);
                        fragment.setArguments(args);
                        fragment.show(getActivity().getSupportFragmentManager(), "simulation_details");
                    } else if (mSimulation.getStrategy() == SimulationConstants.StopLoss) {
                        StopLossDialogFragment fragment = new StopLossDialogFragment();
                        Bundle args = new Bundle();
                        args.putLong("simulationId", simulationId);
                        fragment.setArguments(args);
                        fragment.show(getActivity().getSupportFragmentManager(), "simulation_details");
                    }
                    RealTimeSimulationDialogFragment.this.getDialog().cancel();

                }
                else if (account <= 0.0) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.negative_account_balance_error);
                }
                else {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.real_time_date_error);
                }
            }
        });

        return dialog;
    }
}
