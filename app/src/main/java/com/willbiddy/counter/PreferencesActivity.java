package com.willbiddy.counter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.kizitonwose.colorpreference.ColorDialog;
import com.kizitonwose.colorpreference.ColorShape;

import java.util.Arrays;
import java.util.List;

public class PreferencesActivity extends BaseActivity
        implements ColorDialog.OnColorSelectedListener {


    private final String TOOLBAR_COLOR_KEY = "toolbar-key";
    private final String FAB_COLOR_KEY = "fab-key";
    private Toolbar toolbar;
    private int toolbarColor;
    private int fabColor;
    private List<String> colorPrimaryList;
    private List<String> colorPrimaryDarkList;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Settings");

        colorPrimaryList = Arrays.asList(getResources().getStringArray(R.array.color_choices));
        colorPrimaryDarkList = Arrays.asList(getResources().getStringArray(R.array.color_choices_700));

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        toolbarColor = preferences.getInt(TOOLBAR_COLOR_KEY, ContextCompat.getColor(this, R.color.primary));
        fabColor = preferences.getInt(FAB_COLOR_KEY, ContextCompat.getColor(this, R.color.accent));

        toolbar.setBackgroundColor(toolbarColor);
        updateStatusBarColor(toolbarColor);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commit();

    }

    /**
     * Popup showing color options
     */
    private void showColorDialog(final View view) {
        new ColorDialog.Builder(this).setColorShape(
                view instanceof Toolbar ? ColorShape.CIRCLE : ColorShape.CIRCLE)
                .setColorChoices(R.array.color_choices)
                .setNumColumns(4)
                .setTag(String.valueOf(view instanceof Toolbar ? R.id.toolbar : null))
                .setSelectedColor(view instanceof Toolbar ? toolbarColor : fabColor)
                .show();
    }

    private void updateStatusBarColor(int newColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String newColorString = getColorHex(newColor);
            getWindow().setStatusBarColor(
                    (Color.parseColor(colorPrimaryDarkList.get(colorPrimaryList.indexOf(newColorString)))));
        }
    }

    private String getColorHex(int color) {
        return String.format("#%02x%02x%02x", Color.red(color), Color.green(color), Color.blue(color));
    }

    @Override
    public void onColorSelected(int newColor, String tag) {
        //Find and color the view from the tag we set
        View view = findViewById(Integer.parseInt(tag));
        if (view instanceof Toolbar) {
            toolbar.setBackgroundColor(newColor);
            toolbarColor = newColor;
            preferences.edit().putInt(TOOLBAR_COLOR_KEY, newColor).apply();
            //change the status bar color
            updateStatusBarColor(newColor);
        } else {
            fabColor = newColor;
            preferences.edit().putInt(FAB_COLOR_KEY, newColor).apply();
        }
    }

    /**
     * Showing the color dialog after Intent called
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getStringExtra("methodName").equals("showColorDialog")) {
            showColorDialog(toolbar);
        }
    }

    /**
     * Inflating main counter menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_preferences, menu);
        return true;
    }
}