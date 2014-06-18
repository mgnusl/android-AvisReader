package com.example.avisreader.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.example.avisreader.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
