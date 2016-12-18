package com.uwaterloo.portfoliorebalancing.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.ui.activity.DetailHistoricalSimulationActivity;
import com.uwaterloo.portfoliorebalancing.ui.activity.DetailRealTimeSimulationActivity;
import com.uwaterloo.portfoliorebalancing.util.SimulationConstants;
import com.uwaterloo.portfoliorebalancing.util.StockHelper;

import java.util.List;

/**
 * Created by yuweixu on 2015-10-19.
 */
public class SimulationAdapter extends RecyclerView.Adapter<SimulationAdapter.SimulationItemHolder> {
    private List<Simulation> mSimulationList;
    private Context mContext;

    public SimulationAdapter(List<Simulation> simulationList) {
        mSimulationList = simulationList;
    }

    @Override
    public SimulationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View stockView = inflater.inflate(R.layout.stock_item, parent, false);
        SimulationItemHolder viewHolder = new SimulationItemHolder(stockView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SimulationItemHolder holder, int i) {
        Simulation simulation = mSimulationList.get(i);
        String[] symbols = simulation.getSymbols();
        String list = "";
        for (int j = 0; j < symbols.length; j++) {
            if (j > 0) {
                list += ", ";
            }
            list += symbols[j];
        }
        holder.mName.setText(simulation.getName());
        holder.mStocks.setText(list);
        holder.mBalance.setText(StockHelper.formatDouble(simulation.getAccount()));
        holder.mCircleItem.setText(StockHelper.getFirstCharacter(simulation.getName()));

        GradientDrawable bgShape = (GradientDrawable)holder.mCircleItem.getBackground();
        bgShape.setColor(ContextCompat.getColor(mContext, StockHelper.getIndicatorColorResource(simulation.getName())));
    }

    @Override
    public int getItemCount() {
        return mSimulationList.size();
    }

    public void setSimulationList(List<Simulation> list) {
        mSimulationList = list;
    }

    public class SimulationItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        protected TextView mName, mBalance, mStocks, mCircleItem;

        public SimulationItemHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.data1);
            mStocks = (TextView) itemView.findViewById(R.id.data2);
            mBalance = (TextView) itemView.findViewById(R.id.data3);
            mCircleItem = (TextView) itemView.findViewById(R.id.circle_item);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getPosition();
            Simulation simulation = mSimulationList.get(position);

            Log.v("Simulation Clicked", "Position " + position);

            if (simulation.getType() == SimulationConstants.HISTORICAL_DATA) {
                Intent intent = new Intent(mContext, DetailHistoricalSimulationActivity.class);
                intent.putExtra("newSimulation", false);
                intent.putExtra("simulationId", simulation.getId());
                mContext.startActivity(intent);
            }
            else {
                Intent intent = new Intent(mContext, DetailRealTimeSimulationActivity.class);
                intent.putExtra("newSimulation", false);
                intent.putExtra("simulationId", simulation.getId());
                mContext.startActivity(intent);
            }

        }

        @Override
        public void onCreateContextMenu(final ContextMenu menu,
                                        final View v, final ContextMenu.ContextMenuInfo menuInfo) {
            final int position = getPosition();
            menu.add(0, v.getId(), 0, "Delete");

            menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Simulation simulation = mSimulationList.get(position);
                    simulation.delete();
                    mSimulationList.remove(position);
                    notifyDataSetChanged();

                    return true;
                }
            });
        }
    }
}
