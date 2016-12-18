package com.uwaterloo.portfoliorebalancing.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uwaterloo.portfoliorebalancing.ui.activity.MainActivity;
import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Stock;
import com.uwaterloo.portfoliorebalancing.model.Tick;
import com.uwaterloo.portfoliorebalancing.ui.StockActivityAdapter;
import com.uwaterloo.portfoliorebalancing.ui.activity.StockSelectorActivity;
import com.uwaterloo.portfoliorebalancing.util.ActivityResult;
import com.uwaterloo.portfoliorebalancing.util.PreferenceHelper;
import com.uwaterloo.portfoliorebalancing.util.StockHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by yuweixu on 2015-10-05.
 */
public class StockActivityFragment extends Fragment {
    public static final String STOCK_SYMBOLS = "StockSymbols";

    private MainActivity mainActivity;
    private StockActivityAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<Stock> stockList = new ArrayList<>();
    private List<Tick> tickList;
    private final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (ActivityResult.STOCK_SELECTED) : {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> list = data.getExtras().getStringArrayList(STOCK_SYMBOLS);
                    String[] array = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        array[i] = list.get(i);
                    }
                    new StockAsyncTask().execute(array);
                }
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();

        View view = inflater.inflate(R.layout.fragment_stock, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.stock_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tickList = new ArrayList<>();
        List<String> addTickList = new ArrayList<>();

        stockList = Stock.listAll(Stock.class);

        //Based on preferences, sort the list in a particular way.
        Comparator<Stock> comparator = PreferenceHelper.getStockComparator(getContext());
        if (comparator != null) {
            Collections.sort(stockList, comparator);
        }

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        for (Stock s: stockList) {
            List<Tick> ticks = Tick.find(Tick.class, "symbol = ? and timestamp = ?",
                    s.getSymbol(), timestamp);
            if (ticks.size() < 1) {
                addTickList.add(s.getSymbol());
            } else {
                tickList.add(ticks.get(0));
            }
        }

        if (addTickList.size() > 0) {
            new TickAsyncTask().execute(addTickList);
        }
        mAdapter = new StockActivityAdapter(stockList, getContext());
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton addStockButton = (FloatingActionButton) view.findViewById(R.id.add_stock_button);
        addStockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StockSelectorActivity.class);
                startActivityForResult(intent, ActivityResult.STOCK_SELECTED);
            }
        });
        return view;
    }

    public List<Stock> getStockList() {
        return stockList;
    }

    public void addStock(String s) {
        if (s.length() > 1) {
            for (Stock stock: stockList) {
                if (stock.getSymbol().equals(s.toUpperCase())) {
                    Toast toast = Toast.makeText(getContext(), "You have already added this stock.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
            String [] stocks = {s};
            new StockAsyncTask().execute(stocks);
        }
        else {
            Toast toast = Toast.makeText(getContext(), "Symbol must be valid.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public class TickAsyncTask extends AsyncTask<List<String>, Void, Integer> {
        @Override
        protected Integer doInBackground(List<String>... params) {
            List<String> addTickList = params[0];
            String symbols = TextUtils.join("+", addTickList).toUpperCase();
            Integer result = 0;
            try {
                URL yahoo = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + symbols + "&f=abp");
                URLConnection connection = yahoo.openConnection();
                InputStreamReader is = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(is);
                DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                Date date = new Date();
                String timestamp = dateFormat.format(date);
                for (int i=0; i<addTickList.size(); i++) {
                    String line = br.readLine();
                    String[] stocksInfo = line.split(",");
                    String symbol = addTickList.get(i).toUpperCase();
                    float ask = StockHelper.handleFloat(stocksInfo[0]);
                    float bid = StockHelper.handleFloat(stocksInfo[1]);
                    double prevClose = StockHelper.handleDouble(stocksInfo[2]);

                    if (prevClose != 0.0) {
                        Tick tick = new Tick(symbol, prevClose, timestamp);
                        tick.save();
                        tickList.add(tick);
                    }
                }
                for (Tick t: tickList) {
                    for (Stock s: stockList) {
                        if (t.getSymbol().equals(s.getSymbol())) {
                            s.setPrice(t.getPrice());
                        }
                    }
                }
            }
            catch (Exception e) {
                result = 1;
            }
            return result;
        }
        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0) {
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
            else {
                Log.e("Stock Scraping Error", "Failed to fetch data!");
            }
        }
    }

    public class StockAsyncTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String symbols = TextUtils.join("+", params).toUpperCase();
            Integer result = 1;
            try {
                //URL yahoo = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + symbols + "&f=abp");
                // The snabp at the end of the URL stands for "symbol, name, ask, bid, previous close"
                URL yahoo = new URL("http://finance.yahoo.com/d/quotes.csv?s=" + symbols + "&f=snabp");
                URLConnection connection = yahoo.openConnection();
                InputStreamReader is = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(is);
                DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                Date date = new Date();
                String timestamp = dateFormat.format(date);
                for (int i=0; i<params.length; i++) {
                    String line = br.readLine();
                    System.out.println(line);
                    //String[] stocksInfo = line.split(",");
                    String[] stocksInfo = line.split("\"");
                    Log.v("Stock Info", Arrays.toString(stocksInfo));

                    //String symbol = params[i].toUpperCase();
                    String symbol = stocksInfo[1];
                    String name = stocksInfo[3];
                    String numbers = stocksInfo[4];
                    String[] nums = numbers.split(",");
                    float ask = StockHelper.handleFloat(nums[1]);
                    float bid = StockHelper.handleFloat(nums[2]);
                    double prevClose = StockHelper.handleDouble(nums[3]);
                    boolean flag = true;
                    if (prevClose != 0.0) {
                        Stock stock = new Stock(symbol, name, prevClose);
                        Tick tick = new Tick(symbol, prevClose, timestamp);
                        stock.save();
                        tick.save();
                        stockList.add(stock);
                        tickList.add(tick);
                        result = 2;
                    }
                }
            }
            catch (Exception e) {
                Log.e("Exception", e.toString());
                result = 1;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 2) {
                refresh();
                Log.v("Data Set Changed", "Notifying data set changed.");
            }
            else if (result == 1){
                Toast.makeText(mainActivity, "Failure", Toast.LENGTH_SHORT).show();
                Log.e("Stock Scraping Error", "Failed to fetch data!");

                //Some stocks might have been added successfully, so update list to show them.
                refresh();
            }
        }
    }

    public void refresh() {
        stockList = Stock.listAll(Stock.class);

        //Based on preferences, sort the list in a particular way.
        Comparator<Stock> comparator = PreferenceHelper.getStockComparator(getContext());
        if (comparator != null) {
            Collections.sort(stockList, comparator);
        }

        if (mAdapter != null) {
            mAdapter.setStockList(stockList);
            mAdapter.notifyDataSetChanged();
        }
    }
}