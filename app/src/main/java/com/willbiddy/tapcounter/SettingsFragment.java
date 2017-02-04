package com.willbiddy.tapcounter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

    private static final String KEY_THEME = "theme";
    private static final String THEME_DARK = "dark";
    private static final String THEME_LIGHT = "light";

    private SharedPreferences mSharedPref;
    private String mTheme;
    private SettingsFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference colorPickerPreference = findPreference("COLOR_PICKER_KEY");

        colorPickerPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                // Opening MyPreferencesActivity,
                // which calls onNewIntent(Intent intent)
                // and starts showColorDialog

                Intent intent = new Intent(getActivity(), MyPreferencesActivity.class);
                intent.putExtra("methodName", "showColorDialog");
                startActivity(intent);

                return true;
            }
        });

        Preference ratePreference = findPreference("RATE_PLAY_STORE");

        ratePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                String url = "https://play.google.com/store/apps/details?id=com.willbiddy.tapcounter";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                return true;
            }
        });


        Preference githubPreference = findPreference("FORK_GITHUB");

        githubPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                String url = "https://github.com/willbiddy/TapCounter";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                return true;
            }
        });




    }




}