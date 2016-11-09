package com.uwaterloo.portfoliorebalancing.framework;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yuweixu on 2015-10-12.
 */
public class SettingsActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
