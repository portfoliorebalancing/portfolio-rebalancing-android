package com.uwaterloo.portfoliorebalancing.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Stock;
import com.uwaterloo.portfoliorebalancing.util.StockHelper;

/**
 * Created by yuweixu on 2015-10-05.
 */
public class StockActivityItemHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

    protected final TextView mPrice;
    protected final TextView mSymbol;
    protected final TextView mName;
    protected final TextView mCircleItem;
    protected Stock mStock;
    StockActivityDelegate mDelegate;

    private Context mContext;

    public StockActivityItemHolder(View itemView, Context context) {
        super(itemView);
        mPrice = (TextView)itemView.findViewById(R.id.data3);
        mSymbol = (TextView)itemView.findViewById(R.id.data2);
        mName = (TextView)itemView.findViewById(R.id.data1);
        mCircleItem = (TextView)itemView.findViewById(R.id.circle_item);

        mContext = context;

        itemView.setOnCreateContextMenuListener(this);
    }

    public void setStock(Stock stock) {
        mStock = stock;
        mSymbol.setText(stock.getSymbol());
        mName.setText(stock.getName());
        mPrice.setText(StockHelper.formatDouble(stock.getPrice()));
        mCircleItem.setText(StockHelper.getFirstCharacter(stock.getName()));

        GradientDrawable bgShape = (GradientDrawable)mCircleItem.getBackground();
        bgShape.setColor(ContextCompat.getColor(mContext, StockHelper.getIndicatorColorResource(stock.getSymbol())));
    }

    public void setDelegate(StockActivityAdapter adapter) {
        mDelegate = adapter;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 0, "Delete");

        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mStock.delete();
                mDelegate.notifyStockDeleted(mStock.getId());

                return true;
            }
        });
    }
}
