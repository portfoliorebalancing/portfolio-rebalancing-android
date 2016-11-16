package com.uwaterloo.portfoliorebalancing.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Stock;

import java.util.List;

/**
 * Created by yuweixu on 2015-10-05.
 */
public class StockActivityAdapter extends RecyclerView.Adapter<StockActivityItemHolder> implements StockActivityDelegate {

    private List<Stock> mStockList;
    private Context mContext;

    public StockActivityAdapter(List<Stock> stockList, Context context) {
        mStockList = stockList;
        mContext = context;
    }

    public void updateStockList(List<Stock> stockList) {
        mStockList = stockList;
        notifyDataSetChanged();
    }

    @Override
    public StockActivityItemHolder onCreateViewHolder(ViewGroup parent, int i) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View stockView = inflater.inflate(R.layout.stock_item, parent, false);
        StockActivityItemHolder viewHolder = new StockActivityItemHolder(stockView, mContext);
        viewHolder.setDelegate(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StockActivityItemHolder holder, int i) {
        holder.setStock(mStockList.get(i));
    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }

    @Override
    public void notifyStockDeleted(long stockId) {
        Stock stockToRemove = null;
        for (Stock stock : mStockList) {
            if (stock.getId() == stockId) {
                stockToRemove = stock;
                break;
            }
        }
        mStockList.remove(stockToRemove);
        notifyDataSetChanged();
    }
}

interface StockActivityDelegate {
    void notifyStockDeleted(long stockId);
}