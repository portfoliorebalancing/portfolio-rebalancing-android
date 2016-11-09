package com.uwaterloo.portfoliorebalancing.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.uwaterloo.portfoliorebalancing.MainActivity;
import com.uwaterloo.portfoliorebalancing.R;

/**
 * Created by lucas on 28/09/16.
 */
public class AddStockDialogFragment extends DialogFragment {

    private MainActivity mainActivity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        mainActivity = (MainActivity) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.stock_dialog, null);

        final EditText stockSymbol = (EditText) rootView.findViewById(R.id.stock_symbol_edit);

        builder.setView(rootView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mainActivity.addStock(stockSymbol.getText().toString());
                        AddStockDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.cancel_simulation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel
                        AddStockDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}