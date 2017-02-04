package com.willbiddy.tapcounter;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

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
    }
}