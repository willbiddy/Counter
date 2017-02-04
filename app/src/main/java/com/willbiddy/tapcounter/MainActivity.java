package com.willbiddy.tapcounter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    // variables
    private static final String APP_PREFERENCES = "PreferencesFile";
    private SharedPreferences sharedPreferences;
    private int counter;
    private TickerView counterText;

    private Toolbar toolbar;

    // for color picker
    private int toolbarColor;
    private List<String> colorPrimaryList;
    private List<String> colorPrimaryDarkList;
    private SharedPreferences preferences;
    private final String TOOLBAR_COLOR_KEY = "toolbar-key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean("dark_theme_checkbox", true)) {
            setTheme(R.style.AppTheme_NoActionBar);
        } else {
            setTheme(R.style.AppTheme_NoActionBar);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Tap Counter");

        // main counter number
        counterText = (TickerView) findViewById(R.id.tickerView);
        counterText.setCharacterList(TickerUtils.getDefaultNumberList());
        counterText.setAnimationInterpolator(new AccelerateDecelerateInterpolator());

        // restore preferences
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, 0);
        counter = sharedPreferences.getInt("counter_value", 0);
        counterText.setText(Integer.toString(counter));

        checkSettings();

        colorPrimaryList = Arrays.asList(getResources().getStringArray(R.array.color_choices));
        colorPrimaryDarkList = Arrays.asList(getResources().getStringArray(R.array.color_choices_700));

        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        toolbarColor = preferences.getInt(TOOLBAR_COLOR_KEY, ContextCompat.getColor(this, R.color.primary));

        toolbar.setBackgroundColor(toolbarColor);
        updateStatusBarColor(toolbarColor);
    }

    @Override
    public void onResume() {

        super.onResume();
        checkSettings();

        colorPrimaryList = Arrays.asList(getResources().getStringArray(R.array.color_choices));
        colorPrimaryDarkList = Arrays.asList(getResources().getStringArray(R.array.color_choices_700));

        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        toolbarColor = preferences.getInt(TOOLBAR_COLOR_KEY, ContextCompat.getColor(this, R.color.primary));

        toolbar.setBackgroundColor(toolbarColor);
        updateStatusBarColor(toolbarColor);
    }

    /**
     * Call methods to check for settings changes
     */
    private void checkSettings() {
        turnScreenOnOff();
        checkButtonStatus();
        animationOnOff();
    }

    /**
     * Inflating main counter menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * When menu items pressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reset:
                showResetConfirmationDialog();
                return true;
            case R.id.menu_settings:
                Intent myIntent = new Intent(MainActivity.this, MyPreferencesActivity.class);
                MainActivity.this.startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Showing dialog confirming counter reset
     */
    private void showResetConfirmationDialog() {
        Dialog dialog =
                new AlertDialog.Builder(this).setMessage(getResources().getText(R.string.reset_question))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getText(R.string.reset),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        reset();
                                        checkButtonStatus();
                                    }
                                })
                        .setNegativeButton(getResources().getText(R.string.cancel), null)
                        .create();
        //noinspection ConstantConditions
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    /**
     * On method call, increment counter variable, update screen, and vibrate
     */
    private void incrementCounter() {

        counter++;
        counterText.setText(Integer.toString(counter));

        if (vibrateOn()) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(vibrationLength());
        }

        checkButtonStatus();
    }

    /**
     * On method call, decrement counter variable, update screen, and vibrate
     */
    private void decrementCounter() {

        // if counter can go below zero, or
        // can't go below zero and counter is > 0
        if ((canGoBelowZero()) || (!canGoBelowZero() && counter > 0)) {
            counter--;
            counterText.setText(Integer.toString(counter));

            if (vibrateOn()) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(vibrationLength());
            }
        }

        checkButtonStatus();
    }

    /**
     * Checking if decrement button should
     * be enabled or disabled
     */
    private void checkButtonStatus() {

        // if can't go below zero
        // and counter is at or below zero
        // disable button

        Button decrementButton = (Button) findViewById(R.id.decrementButton);

        if (!canGoBelowZero() && counter <= 0) {
            decrementButton.setAlpha(.35f);
        } else {
            decrementButton.setAlpha(1f);
        }
    }

    /**
     * Handles volume key up and down
     * Checks with useHardwareButtons() and then
     * increments/decrements or does default device action
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (useHardwareButtons()) {
                    incrementCounter();
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (useHardwareButtons()) {
                    decrementCounter();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * Resetting counter to 0
     */
    private void reset() {
        counter = 0;
        counterText.setText(Integer.toString(counter));
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Store data on activity stop
        SharedPreferences settings = getSharedPreferences(APP_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("counter_value", counter);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onStop();
    }

    /**
     * Checks status of hardware_checkbox in preferences.xml
     * returns status as boolean
     */
    private boolean useHardwareButtons() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("hardware_checkbox", true);
    }

    /**
     * Checks status of screen_on_checkbox in preferences.xml
     * returns status as boolean
     */
    private boolean keepScreenOnEvaluate() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("screen_on_checkbox", true);
    }

    /**
     * Checks status of below_zero_checkbox in preferences.xml
     * returns status as boolean
     */
    private boolean canGoBelowZero() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("below_zero_checkbox", true);
    }

    /**
     * Flags or removes flag for keeping screen on
     * Evaluates necessity through keepScreenOnEvaluate()
     */
    private void turnScreenOnOff() {

        if (keepScreenOnEvaluate()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * Returns if the vibration setting is on or off
     */
    private boolean vibrateOn() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("vibrate_checkbox", true);
    }

    /**
     * Returns length for the vibration, based off of vibration_list in preferences.xml
     */
    private int vibrationLength() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.getString("vibration_list", "1");
        return Integer.parseInt(sp.getString("vibration_list", "1"));
    }

    /**
     * Turns the animation
     */
    private void animationOnOff() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("animate_checkbox", true)) {
            counterText.setAnimationDuration(300);
        } else {
            counterText.setAnimationDuration(0);
        }
    }

    public void decrementCounter(@SuppressWarnings("UnusedParameters") View view) {
        decrementCounter();
    }

    public void incrementCounter(@SuppressWarnings("UnusedParameters") View view) {
        incrementCounter();
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
}