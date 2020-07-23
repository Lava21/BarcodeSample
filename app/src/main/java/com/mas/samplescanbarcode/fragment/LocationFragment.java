package com.mas.samplescanbarcode.fragment;

import android.os.Bundle;
import androidx.preference.PreferenceFragment;
import com.mas.samplescanbarcode.R;

public class LocationFragment extends PreferenceFragment {

    private final static String beep = "beep";
    private final static String frontCamera = "frontCamera";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

}
