package com.lilo.lilo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created by ashis on 11/26/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);
    }
}
