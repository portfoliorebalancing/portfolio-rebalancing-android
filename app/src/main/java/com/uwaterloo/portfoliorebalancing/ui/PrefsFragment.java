package com.uwaterloo.portfoliorebalancing.ui;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.uwaterloo.portfoliorebalancing.R;

/**
 * Created by lucas on 22/11/16.
 */

public class PrefsFragment extends PreferenceFragmentCompat {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = (RecyclerView)view.findViewById(R.id.list);
        list.setBackgroundResource(R.drawable.list_view_border);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
