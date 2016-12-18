
package com.uwaterloo.portfoliorebalancing.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.util.PreferenceHelper;
import com.uwaterloo.portfoliorebalancing.util.StockHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lucas on 15/10/16.
 */

public class StockSelectorActivity extends AsyncTaskActivity {

    private ListView listView;
    private ArrayList<String> stocksToAdd = new ArrayList<>();
    private ArrayList<StockData> stockDataArrayList = new ArrayList<>();
    private StockSelectorAsyncTask loadData = null;

    @Override
    protected void stopAsyncTask() {
        if (loadData != null) {
            loadData.cancel(true);
            loadData = null;
        }
    }

    private void showStocksInList() {
        //Based on preferences, sort the list in a particular way.
        Comparator<StockData> comparator = PreferenceHelper.getStockSelectorComparator(getApplicationContext());
        if (comparator != null) {
            Collections.sort(stockDataArrayList, comparator);
        }

        findViewById(R.id.loading).setVisibility(View.GONE);
        listView.setAdapter(new StockSelectorAdapter(this, stockDataArrayList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String symbol = stockDataArrayList.get(position).getSymbol();
                if (stocksToAdd.contains(symbol)) {
                    stocksToAdd.remove(symbol);
                    view.setBackground(getResources().getDrawable(R.drawable.stock_item_background));
                } else {
                    stocksToAdd.add(symbol);
                    view.setBackground(getResources().getDrawable(R.drawable.stock_item_selected_background));
                }
            }
        });
    }

    public static class StockData {
        private String name;
        private String symbol;
        private double price;

        public StockData(String n, String s, String p) {
            name = n;
            symbol = s;
            try {
                price = Double.parseDouble(p);
            } catch (Exception e) {
                price = 0.0;
            }
        }

        public String getName() {
            return name;
        }

        public String getSymbol() {
            return symbol;
        }

        public double getPrice() {
            return price;
        }
    }

    public class StockSelectorAdapter extends ArrayAdapter<StockData> {
        private final Context context;
        private final List<StockData> values;

        public StockSelectorAdapter(Context context, List<StockData> values) {
            super(context, R.layout.stock_description, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.stock_description, parent, false);
            }

            TextView circleItem = (TextView)convertView.findViewById(R.id.circle_item);
            TextView nameItem = (TextView)convertView.findViewById(R.id.name);
            TextView symbolItem = (TextView)convertView.findViewById(R.id.symbol);
            TextView priceItem = (TextView)convertView.findViewById(R.id.price);

            StockData data = values.get(position);
            nameItem.setText(data.getName());
            symbolItem.setText(data.getSymbol());
            priceItem.setText("$" + data.getPrice());
            circleItem.setText(StockHelper.getFirstCharacter(data.getName()));

            GradientDrawable bgShape = (GradientDrawable)circleItem.getBackground();
            bgShape.setColor(ContextCompat.getColor(getApplicationContext(), StockHelper.getIndicatorColorResource(data.getSymbol())));

            //In a list view, child views are recycled. Thus, we have to reset the background color accordingly.
            if (stocksToAdd.contains(data.getSymbol())) {
                convertView.setBackground(getResources().getDrawable(R.drawable.stock_item_selected_background));
            } else {
                convertView.setBackground(getResources().getDrawable(R.drawable.stock_item_background));
            }

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getLayoutInflater().inflate(R.layout.stock_selector_activity, null);
        listView = (ListView)view.findViewById(R.id.list_view);

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.add_stocks_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAsyncTask();

                Intent resultIntent = new Intent();
                resultIntent.putStringArrayListExtra(StockActivityFragment.STOCK_SYMBOLS, stocksToAdd);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        setContentView(view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData = new StockSelectorAsyncTask();
        loadData.execute();
    }

    public class StockSelectorAsyncTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                //Based on preferences, choose stock market in a particular way.
                URL yahoo = new URL("http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=" +
                        PreferenceHelper.getStockMarketString(getApplicationContext()) + "&render=download");
                URLConnection connection = yahoo.openConnection();
                InputStreamReader is = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(is);

                br.readLine(); //first line is table headings
                String line = br.readLine();

                while (line != null) {
                    String[] stocksInfo = line.split("\"");

                    final String symbol = stocksInfo[1];
                    final String name = stocksInfo[3];
                    final String price = stocksInfo[5];

                    stockDataArrayList.add(new StockData(name, symbol, price));

                    line = br.readLine();
                    if (isCancelled()) {
                        return 1;
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
            if (result == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showStocksInList();
                    }
                });
            }
            else if (result == 1){
                Log.e("Stock Scraping Error", "Failed to fetch data!");
            }
        }
    }
}