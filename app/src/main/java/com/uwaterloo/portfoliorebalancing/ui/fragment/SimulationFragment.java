package com.uwaterloo.portfoliorebalancing.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uwaterloo.portfoliorebalancing.ui.activity.MainActivity;
import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.model.Stock;
import com.uwaterloo.portfoliorebalancing.ui.SimulationAdapter;
import com.uwaterloo.portfoliorebalancing.ui.activity.SimulationSelectorActivity;
import com.uwaterloo.portfoliorebalancing.ui.activity.DetailHistoricalSimulationActivity;
import com.uwaterloo.portfoliorebalancing.ui.activity.DetailRealTimeSimulationActivity;
import com.uwaterloo.portfoliorebalancing.util.PreferenceHelper;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yuweixu on 2015-10-19.
 */
public class SimulationFragment extends Fragment {
    private SimulationAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<Simulation> simulationList;
    private MainActivity mainActivity;

    public static final int SIMULATION_CREATED = 1;
    public static final String SIMULATION_ID = "newSimulationId";
    public static final String SIMULATION_TYPE = "newSimulationType";

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

        //Based on preferences, sort the list in a particular way.
        Comparator<Simulation> comparator = PreferenceHelper.getSimulationComparator(getContext());
        if (comparator != null) {
            Collections.sort(simulationList, comparator);
        }

        mAdapter = new SimulationAdapter(simulationList);
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

        //Based on preferences, sort the list in a particular way.
        Comparator<Simulation> comparator = PreferenceHelper.getSimulationComparator(getContext());
        if (comparator != null) {
            Collections.sort(simulationList, comparator);
        }

        if (mAdapter != null) {
            mAdapter.setSimulationList(simulationList);
            mAdapter.notifyDataSetChanged();
        }
    }
}
