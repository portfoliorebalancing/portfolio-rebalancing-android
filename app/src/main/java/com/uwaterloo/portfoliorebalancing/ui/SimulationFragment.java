package com.uwaterloo.portfoliorebalancing.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.uwaterloo.portfoliorebalancing.MainActivity;
import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.model.Stock;
import com.uwaterloo.portfoliorebalancing.ui.extra.ChangeStrategyDialogFragment;
import com.uwaterloo.portfoliorebalancing.ui.extra.DividerItemDecoration;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuweixu on 2015-10-19.
 */
public class SimulationFragment extends Fragment implements SimulationDelegate {
    private SimulationAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<Simulation> simulationList;
    private MainActivity mainActivity;

    public static final int SIMULATION_CREATED = 1;
    public static final String SIMULATION_ID = "newSimulationId";
    public static final String SIMULATION_TYPE = "newSimulationType";
    //private List<Stock> stockList = new ArrayList<>();

    /*public void onStockAdded() {
        System.out.println("Stock Added Called with size" + stockList.size());
        stockList = Stock.listAll(Stock.class);
        System.out.println("Stock Added Called with size" + stockList.size());
    }*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (SIMULATION_CREATED) : {
                if (resultCode == Activity.RESULT_OK) {
                    mainActivity.updateSimulationFragment();
                    long simulationId = data.getExtras().getLong(SIMULATION_ID);
                    int simulationType = data.getExtras().getInt(SIMULATION_TYPE);

                    if (simulationType == SimulationConstants.REAL_TIME_DATA) {
                        Intent intent = new Intent(getContext(), DetailRealTimeSimulationActivity.class);
                        intent.putExtra("newSimulation", true);
                        intent.putExtra("simulationId", simulationId);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), DetailHistoricalSimulationActivity.class);
                        intent.putExtra("newSimulation", true);
                        intent.putExtra("simulationId", simulationId);
                        startActivity(intent);
                    }
                }
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();

        View view = inflater.inflate(R.layout.fragment_simulation, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.simulation_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        simulationList = Simulation.listAll(Simulation.class);
        mAdapter = new SimulationAdapter(simulationList);
        mAdapter.setDelegate(this);
        mRecyclerView.setAdapter(mAdapter);

        List<String> addTickList = new ArrayList<>();

        FloatingActionButton addSimulationButton = (FloatingActionButton) view.findViewById(R.id.add_simulation_button);
        addSimulationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                List<Stock> stockList = mainActivity.getStockList();
                if (stockList.size() > 0) {
                    Intent intent = new Intent(getContext(), SimulationSelectorActivity.class);
                    startActivityForResult(intent, SIMULATION_CREATED);

                    /*List<Double> ratios = new ArrayList<Double>();
                    for (int i = 0; i < stockList.size(); i++) {
                        ratios.add(1.0);
                    }
                    Simulation sim = new Simulation(ratios, 1, 10000, 10000, "Simulation");
                    sim.save();
                    Bundle args = new Bundle();
                    args.putLong("simulationId", sim.getId());
                    DialogFragment dialogFragment = new AddSimulationDialogFragment();
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getActivity().getSupportFragmentManager(), "add_simulation");*/
                } else {
                    Toast.makeText(getContext(), "Portfolio is currently empty.", Toast.LENGTH_SHORT).show();
                    Log.e("StockList", "Size is " + stockList.size());
                }
            }
        });
        return view;
    }

    public void refresh() {
        simulationList = Simulation.listAll(Simulation.class);
        if (mAdapter != null) {
            mAdapter.setSimulationList(simulationList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean showChangeStrategyDialog(long id) {
        Bundle args = new Bundle();
        args.putLong("simulationId", id);
        DialogFragment dialogFragment = new ChangeStrategyDialogFragment();
        dialogFragment.setArguments(args);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "change_strategy");
        return true;
    }
}
