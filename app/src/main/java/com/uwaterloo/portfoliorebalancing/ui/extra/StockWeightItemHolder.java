package com.uwaterloo.portfoliorebalancing.ui.extra;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.uwaterloo.portfoliorebalancing.R;

/**
 * Created by yuweixu on 15-11- 09.
 */
public class StockWeightItemHolder extends RecyclerView.ViewHolder {

    protected final TextView mSymbol;
    protected final EditText mWeight;

    private Activity mActivity;
    public StockWeightItemHolder(View itemView, Activity activity) {
        super(itemView);
        mSymbol = (TextView)itemView.findViewById(R.id.stock_symbol);
        mWeight = (EditText)itemView.findViewById(R.id.stock_weight);
        mActivity = activity;
//        mWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (inputMethodManager != null) {
//                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                }
//            }
//        });
    }

}
