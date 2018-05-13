package com.example.mathwiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    public int gameDifficulty;
    private Button easyButton, mediumButton, hardButton;
    private ToggleButton musicToggle, soundEffectsToggle;

    // creates the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                // User chose the settings item, show the settings activity
                startSettingsActivity();
                return true;
            case R.id.action_highscores:
                // User chose the highscores item, show the highscores activity
                startHighScoreActivity();
                return true;
            case R.id.action_playagain:
                // User chose the playagain item, sow the game activity
                startGameActivity();
                return true;
            default:
                // The users action was not recognized
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.medButton);
        hardButton = findViewById(R.id.hardButton);
        musicToggle = findViewById(R.id.musicToggle);
        soundEffectsToggle = findViewById(R.id.soundEffectsToggle);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.easyButton).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.medButton).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.hardButton).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.musicToggle).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.soundEffectsToggle).setOnTouchListener(mDelayHideTouchListener);

        easyButton.setOnClickListener(this);
        mediumButton.setOnClickListener(this);
        hardButton.setOnClickListener(this);

        musicToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // the toggle is enabled
                    GameActivity.setPlayBackgroundMusic(true);
                    musicToggle.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                } else{
                    // the toggle is disabled
                    GameActivity.setPlayBackgroundMusic(false);
                    musicToggle.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
                }
            }
        });

        soundEffectsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // the toggle is enabled
                    GameActivity.setPlaySoundEffects(true);
                    soundEffectsToggle.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                } else{
                    // the toggle is disabled
                    GameActivity.setPlaySoundEffects(false);
                    soundEffectsToggle.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
                }
            }
        });

        setButtonHighlight();
    }

    private void setButtonHighlight() {
        if(gameDifficulty == 0){
            // sets the easy button to the selected button colour
            easyButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

            // sets the other buttons to the non-selected button colour
            mediumButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
            hardButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
        }
        else if(gameDifficulty == 1){
            // sets the easy button to the selected button colour
            mediumButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

            // sets the other buttons to the non-selected button colour
            easyButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
            hardButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
        }
        else if(gameDifficulty == 2){
            // sets the easy button to the selected button colour
            hardButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));

            // sets the other buttons to the non-selected button colour
            easyButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
            mediumButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.easyButton){
            // sets the game difficulty to easy
            gameDifficulty = 0;
            GameActivity.setGameDifficulty(gameDifficulty);
            // sets the highlights for each button
            setButtonHighlight();
        }
        if (v.getId() == R.id.medButton){
            // sets the game difficulty to medium
            gameDifficulty = 1;
            GameActivity.setGameDifficulty(gameDifficulty);
            // sets the highlights for each button
            setButtonHighlight();
        }
        if (v.getId() == R.id.hardButton){
            // sets the game difficulty to hard
            gameDifficulty = 2;
            GameActivity.setGameDifficulty(gameDifficulty);
            // sets the highlights for each button
            setButtonHighlight();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Makes it so that the game content fills the screen when ui is hidden
        mContentView.setFitsSystemWindows(false);

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Makes it so that the action bar does not cover game content
        mContentView.setFitsSystemWindows(true);

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);

        // Makes it so that the game content fills the screen when ui is hidden
        mContentView.setFitsSystemWindows(false);
    }

    public void startHighScoreActivity() {
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }

    public void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
