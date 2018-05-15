package com.example.mathwiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HighScoreActivity extends AppCompatActivity {
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

    private ArrayList<Integer> scoresList = new ArrayList<>();
    private ArrayList<String> scoresStringList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Cursor cursor;
    private String scoreText;
    private ListView highScoresListView;

    private static final String TAG = "HighScoreActivity";

    DatabaseHelper databaseHelper;

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

        setContentView(R.layout.activity_high_score);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        //highScoresDB = new HighScoresDB(this);
        highScoresListView = findViewById(R.id.highScoresList);
        highScoresListView.setAdapter(adapter);

        databaseHelper = new DatabaseHelper(this);
        // populate the list view with all of the highscores
        populateHighScores();


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
    }

    private void populateHighScores() {
        Log.d(TAG, "populateHighScores: Displaying data in the ListView.");

        // get the data and then append to the highscores list
        Cursor data = databaseHelper.getData();
        // iterates through each row
        if(data != null && data.getCount() > 0){
            while (data.moveToNext()){
                // get the value from the database in column 1
                // then add it to the ArrayList
                scoresList.add(data.getInt(1));
            }
            // sorts the list of high scores so that they are highest to lowest
            Collections.sort(scoresList);
            Collections.reverse(scoresList);

            for(int x : scoresList){
                String y = String.valueOf(x);
                scoresStringList.add(y);
            }
            // set the adapter
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scoresStringList){
                @Override
                public View getView(int poition, View converView, ViewGroup parent){
                    // Get the item from ListView
                    View view = super.getView(poition, converView, parent);

                    // Initialise a TextView for each ListView Item
                    TextView textView = view.findViewById(android.R.id.text1);

                    // Set the text colour of each TextView
                    textView.setTextColor(getResources().getColor(R.color.colorAccentDark));
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    // Generate ListView Item using the TextView
                    return view;
                }

            };
            highScoresListView.setAdapter(adapter);

        }
    }

    public void clearHighScores(View view){
        // delete all of the high scores from the database
        Log.d(TAG, "clearHighScores: Deleting all entries in the database");

        databaseHelper.deleteAll();
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
        // Makes it so that the game content fills the screen when ui is hidden
        mContentView.setFitsSystemWindows(false);

        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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
