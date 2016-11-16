package com.uwaterloo.portfoliorebalancing.ui;

/**
 * Created by lucas on 31/10/16.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.uwaterloo.portfoliorebalancing.R;
import com.uwaterloo.portfoliorebalancing.model.Simulation;
import com.uwaterloo.portfoliorebalancing.model.Stock;
import com.uwaterloo.portfoliorebalancing.util.StockHelper;

import java.util.List;

public class PickStockSimulationFragment extends Fragment {

    private SimulationSelectorActivity activity;
    private List<Stock> stockList;
    private String stock;
    private int selectedItem;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (SimulationSelectorActivity)getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pick_stock_simulation_fragment, null);
        final ListView listView = (ListView)view.findViewById(R.id.stock_list);
        stockList = Stock.listAll(Stock.class);
        stock = stockList.get(0).getSymbol();
        final ArrayAdapter adapter = new SimulationSelectorAdapter(getContext(), stockList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stock = stockList.get(position).getSymbol();
                selectedItem = position;
                adapter.notifyDataSetChanged();

//                View previousSelection = listView.getSelectedView();
  //              previousSelection.setSelected(false);
                //view.setBackground(getResources().getDrawable(R.drawable.stock_item_background));
    //            view.setSelected(true);
      //          view.setBackground(getResources().getDrawable(R.drawable.stock_item_selected_background));
            }
        });
        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.next_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.stockSelected(stock);
            }
        });

        return view;
    }

    public class SimulationSelectorAdapter extends ArrayAdapter<Stock> {
        private final Context context;
        private final List<Stock> values;

        public SimulationSelectorAdapter(Context context, List<Stock> values) {
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

            Stock data = values.get(position);
            nameItem.setText(data.getName());
            symbolItem.setText(data.getSymbol());
            priceItem.setText("$" + data.getPrice());
            circleItem.setText(StockHelper.getFirstCharacter(data.getName()));

            GradientDrawable bgShape = (GradientDrawable)circleItem.getBackground();
            bgShape.setColor(ContextCompat.getColor(getContext(), StockHelper.getColorResource(data.getSymbol())));

            if (position == selectedItem) {
                convertView.setSelected(true);
                convertView.setBackground(getResources().getDrawable(R.drawable.stock_item_selected_background));
            } else {
                convertView.setBackground(getResources().getDrawable(R.drawable.stock_item_background));
            }

            return convertView;
        }
    }
}

