package com.uwaterloo.portfoliorebalancing.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.uwaterloo.portfoliorebalancing.R;

import java.math.BigDecimal;

import com.uwaterloo.portfoliorebalancing.util.PortfolioRebalanceUtil;

/**
 * Created by yuweixu on 2015-11-02.
 */
public class PriceMarkerView extends MarkerView {

    private TextView tvContent;

    public PriceMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.marker_text_view);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;
            BigDecimal result;
            result= PortfolioRebalanceUtil.round(ce.getHigh(), 2);
            tvContent.setText("$" + result);
//            tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            BigDecimal result;
            result= PortfolioRebalanceUtil.round(e.getVal(), 2);
            tvContent.setText("$" + result);
//            tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
        }
    }



    @Override
    public int getYOffset() {
        return -getHeight();
    }

    @Override
    public int getXOffset() {
        return -(getWidth() / 2);
    }

    @Override
    public void draw(Canvas canvas, float posx, float posy) {
        super.draw(canvas, posx, posy);
    }
}