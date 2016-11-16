package com.uwaterloo.portfoliorebalancing.ui.extra;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uwaterloo.portfoliorebalancing.R;

import java.util.List;

/**
 * Created by yuweixu on 15-11-09.
 */
public class StockWeightAdapter extends RecyclerView.Adapter<StockWeightItemHolder> {

    private List<String> stockSymbols;
    private Activity mActivity;
    public StockWeightAdapter(List<String> symbols, Activity activity) {
        stockSymbols = symbols;
        mActivity = activity;
    }

    @Override
    public StockWeightItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View stockView = inflater.inflate(R.layout.stock_weight_item, parent, false);
        StockWeightItemHolder viewHolder = new StockWeightItemHolder(stockView, mActivity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StockWeightItemHolder holder, int position) {
        String symbol = stockSymbols.get(position);
        holder.mSymbol.setText(symbol);
    }

    @Override
    public int getItemCount() {
        return stockSymbols.size();
    }
}
