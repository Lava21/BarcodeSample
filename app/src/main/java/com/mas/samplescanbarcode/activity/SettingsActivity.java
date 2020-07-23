package com.mas.samplescanbarcode.activity;

import android.preference.PreferenceActivity;
import android.os.Bundle;

import com.mas.samplescanbarcode.fragment.LocationFragment;

public class SettingsActivity extends PreferenceActivity {

    private final static String TAG = "SettingsActivity";
    public SettingsActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new LocationFragment()).commit();
    }
}
