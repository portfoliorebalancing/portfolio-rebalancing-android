package com.uwaterloo.portfoliorebalancing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.uwaterloo.portfoliorebalancing.model.Stock;
import com.uwaterloo.portfoliorebalancing.ui.PrefsFragment;
import com.uwaterloo.portfoliorebalancing.ui.SimulationFragment;
import com.uwaterloo.portfoliorebalancing.ui.StockActivityFragment;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private MainPagerAdapter mViewPagerAdapter;

    private static StockActivityFragment mStockActivityFragment = new StockActivityFragment();
    private static SimulationFragment mSimulationFragment = new SimulationFragment();
    private static PrefsFragment mSettings = new PrefsFragment();
    private static String TAB_INDEX = "mainActivityTabIndex";
    private static String MY_PREFS_NAME = "myPreferences";

    private static int NUM_ITEMS = 3;
    private int[] mIcons = {R.layout.portfolio_tab_icon, R.layout.simulation_tab_icon, R.layout.custom_rebalancing_tab_icon};
    private int[] mTitles = {R.string.portfolio, R.string.simulations, R.string.settings};

    private TabLayout tabLayout;

    public List<Stock> getStockList() {
        return mStockActivityFragment.getStockList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "On Create", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        MainPagerAdapter mViewPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(mViewPagerAdapter);
        vpPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                setUpActionBar(position);
            }
        });

        tabLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_blue));
        tabLayout.setSelectedTabIndicatorHeight(12);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getApplicationContext(), R.color.tab_indicator));
        tabLayout.setupWithViewPager(vpPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            ImageView myCustomIcon = (ImageView) LayoutInflater.from(tabLayout.getContext()).inflate(mIcons[i], null);
            tabLayout.getTabAt(i).setCustomView(myCustomIcon);
        }

        /*if (savedInstanceState != null) {
            Toast.makeText(this, "Not Null", Toast.LENGTH_SHORT).show();
            int tabIndex = savedInstanceState.getInt(TAB_INDEX);
            TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
            tab.select();
        }*/

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int restoredText = prefs.getInt(TAB_INDEX, 0);
        tabLayout.getTabAt(restoredText).select();

        setUpActionBar(0);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onPause() {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(TAB_INDEX, tabLayout.getSelectedTabPosition());
        editor.commit();

        super.onPause();
    }

    /**
     * @param position The position of the currently selected tab.
     */
    public void setUpActionBar(int position) {
        getSupportActionBar().setTitle(mTitles[position]);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            ImageView view = (ImageView) tabLayout.getTabAt(i).getCustomView();
            if (i == position) {
                int padding = 40;//30
                view.setPadding(padding, padding, padding, padding);
            }
            else {
                int padding = 40;//50
                view.setPadding(padding, padding, padding, padding);
            }
        }
    }

    public static class MainPagerAdapter extends FragmentPagerAdapter {
        public MainPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mStockActivityFragment;
                case 1:
                    return mSimulationFragment;
                case 2:
                    return mSettings;
                default:
                    return null;
            }
        }
    }

    public void updateSimulationFragment() {
        if (mSimulationFragment != null) {
            mSimulationFragment.refresh();
        }
    }

    public void addStock(String s) {
        mStockActivityFragment.addStock(s);
    }
}
