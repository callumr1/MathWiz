package com.example.mathwiz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
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

    // variables used in this activity
    private int num1, num2, scoreMultiplier, answer, finalScore;
    private static int gameDifficulty = 0;
    private int score = 0;
    private int correctPoints = 10;
    private int incorrectPoints = 2;
    private int timerLength = 60000;
    private String question, symbol, correctButton;
    private final int MAX_STREAMS = 5;
    private final int CORRECT = 0;
    private final int INCORRECT = 1;
    private int backgroundMusic, correctSound, incorrectSound;
    private boolean soundsLoaded = false;
    private static boolean playBackgroundMusic = true, playSoundEffects = true;

    // interactable elements in this activity
    private Button answerButton1, answerButton2, answerButton3, answerButton4;
    private TextView questionText, scoreText, timerText;
    private SoundManager soundManager;
    private MediaPlayer mediaPlayer;

    // variables for shake detection
    private SensorManager sensorManager;
    private static final int SHAKE_THRESHOLD = 50;
    private static final int TIME_BETWEEN_SHAKES = 1000; // in milliseconds
    private long lastShakeTime; // last time the device was shaken
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastShakeTime) > TIME_BETWEEN_SHAKES) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)) - SensorManager.GRAVITY_EARTH;

                if(acceleration > SHAKE_THRESHOLD){
                    // when the device is shaken, it "skips" that question and generates a new one
                    setQuestion();
                    lastShakeTime = currentTime;
                    System.out.println("Shaking");
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

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

        setContentView(R.layout.activity_game);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // initialise elements in this activity
        questionText = findViewById(R.id.questionText);
        answerButton1 = findViewById(R.id.answerButton1);
        answerButton2 = findViewById(R.id.answerButton2);
        answerButton3 = findViewById(R.id.answerButton3);
        answerButton4 = findViewById(R.id.answerButton4);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timeText);

        // set on click listeners for this activity
        answerButton1.setOnClickListener(this);
        answerButton2.setOnClickListener(this);
        answerButton3.setOnClickListener(this);
        answerButton4.setOnClickListener(this);

        // sets the scoreMultiplier based on the gameDifficulty
        if (gameDifficulty == 0) {
            // if the game mode is Easy only allows addition questions
            symbol = " + ";
            scoreMultiplier = 1;
        } else if (gameDifficulty == 1) {
            // if the game mode is Medium only allows multiplication questions
            symbol = " x ";
            scoreMultiplier = 2;
        } else if (gameDifficulty == 2) {
            // if the game mode is Hard only allows division questions
            symbol = " x ";
            scoreMultiplier = 3;
        }

        // Generates random numbers to create the question
        setQuestion();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Timer that starts at the start of the game, when it finishes, goes to the GameOverActivity
                new CountDownTimer(timerLength, 1000) {

                    public void onTick(long millisUntilFinised) {
                        timerText.setText(String.valueOf(millisUntilFinised / 1000));
                    }

                    @Override
                    public void onFinish() {
                        questionText.setText(R.string.times_up);

                        // disables all of the answer buttons
                        answerButton1.setVisibility(View.GONE);
                        answerButton2.setVisibility(View.GONE);
                        answerButton3.setVisibility(View.GONE);
                        answerButton4.setVisibility(View.GONE);
                        setFinalScore(score);

                        // go to the GameOverActivity
                        startGameOverActivity();

                    }
                }.start();
            }
        });

        // Handles the SensorManager for when the user shakes the device
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Continue playing the background music
        Background.run(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                if(playSoundEffects){
                    // Prepares the sound effects to be played
                    soundManager = new SoundManager(GameActivity.this);
                    correctSound = soundManager.addSound(R.raw.correct);
                    incorrectSound = soundManager.addSound(R.raw.incorrect);
                }

                // Plays the background music if it has not been disabled in setting
                if(playBackgroundMusic){
                    Background.playBackgroundMusic(GameActivity.this);
                }
            }
        });
        // starts listening for shaking
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(playBackgroundMusic){
            // Stop playing the background music
            Background.stopBackgroundMusic(this);
        }

        // stop listening for shaking
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void setQuestion() {

        // sets the two numbers that will be used in the question
        Random random = new Random();

        // calculates the answer based on the gameDifficulty
        if (gameDifficulty == 0){
            num1 = random.nextInt(50);
            num2 = random.nextInt(10);
            answer = num1 + num2;
        }
        else if (gameDifficulty == 1){
            num1 = random.nextInt(10);
            num2 = random.nextInt(10);
            answer = num1 * num2;
        }
        else if (gameDifficulty == 2){
            num1 = random.nextInt(50);
            num2 = random.nextInt(10);
            answer = num1 * num2;
        }

        // sets the question and displays it
        question = String.valueOf(num1) + symbol + String.valueOf(num2);
        questionText.setText(question);

        // sets the answers that the buttons display when the question is changed/set
        setAnswerButtons();
    }

    private void setAnswerButtons(){
        try {
            // randomise which button displays that actual answer
            Random random = new Random();
            int n = random.nextInt(4);
            // creates random false answers
            int n1 = answer + ((1 + num2) * 2);
            int n2 = answer + (1 + num1);
            int n3 = answer - (1 + num2);

            if (n1 == n || n2 == n || n3 == n){
                n1 += 1;
                n2 += 1;
                n3 += 1;
            }

            if (n == 0) {
                answerButton1.setText(String.valueOf(answer));
                correctButton = String.valueOf(answerButton1.getId());

                // sets the value of the other buttons
                answerButton2.setText(String.valueOf(n1));
                answerButton3.setText(String.valueOf(n2));
                answerButton4.setText(String.valueOf(n3));
            } else if (n == 1) {
                answerButton2.setText(String.valueOf(answer));
                correctButton = String.valueOf(answerButton2.getId());

                // sets the value of the other buttons
                answerButton1.setText(String.valueOf(n1));
                answerButton3.setText(String.valueOf(n2));
                answerButton4.setText(String.valueOf(n3));
            } else if (n == 2) {
                answerButton3.setText(String.valueOf(answer));
                correctButton = String.valueOf(answerButton3.getId());

                // sets the value of the other buttons
                answerButton1.setText(String.valueOf(n1));
                answerButton2.setText(String.valueOf(n2));
                answerButton4.setText(String.valueOf(n3));
            } else if (n == 3) {
                answerButton4.setText(String.valueOf(answer));
                correctButton = String.valueOf(answerButton4.getId());

                // sets the value of the other buttons
                answerButton1.setText(String.valueOf(n1));
                answerButton2.setText(String.valueOf(n2));
                answerButton3.setText(String.valueOf(n3));
            }
        }catch(Exception setAnswerButtonsError){
            System.out.println("setAnswerButtonsError");
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.answerButton1){
            if(correctButton.equals(String.valueOf(answerButton1.getId()))){
                // if the button is the correct answer then give the user points and get a new question
                increaseScore(correctPoints);
                playSoundEffect(correctSound);
            }
            else {
                // if the button is not the answer, take away points from the user
                decreaseScore(incorrectPoints);
                // Play the incorrect sound effect
                playSoundEffect(incorrectSound);
            }
            setQuestion();
        }
        else if(v.getId() == R.id.answerButton2){
            if(correctButton.equals(String.valueOf(answerButton2.getId()))){
                // if the button is the correct answer then give the user points and get a new question
                increaseScore(correctPoints);
                // Play the correct sound effect
                playSoundEffect(correctSound);
            }
            else {
                // if the button is not the answer, take away points from the user
                decreaseScore(incorrectPoints);
                // Play the incorrect sound effect
                playSoundEffect(incorrectSound);
            }
            setQuestion();
        }
        else if(v.getId() == R.id.answerButton3){
            if(correctButton.equals(String.valueOf(answerButton3.getId()))){
                // if the button is the correct answer then give the user points and get a new question
                increaseScore(correctPoints);
                // Play the correct sound effect
                playSoundEffect(correctSound);
            }
            else {
                // if the button is not the answer, take away points from the user
                decreaseScore(incorrectPoints);
                // Play the incorrect sound effect
                playSoundEffect(incorrectSound);
            }
            setQuestion();
        }
        else if(v.getId() == R.id.answerButton4){
            if(correctButton.equals(String.valueOf(answerButton4.getId()))){
                // if the button is the correct answer then give the user points and get a new question
                increaseScore(correctPoints);
                playSoundEffect(correctSound);
            }
            else {
                // if the button is not the answer, take away points from the user
                decreaseScore(incorrectPoints);
                // Play the incorrect sound effect
                playSoundEffect(incorrectSound);
            }
            setQuestion();
        }
    }

    private void playSoundEffect(int sound) {
        if(playSoundEffects) {
            // Play the correct sound effect
            soundManager.play(sound, 0);
        }
    }

    private void increaseScore(int points){
        // increases the players score based on the difficulty that they are playing
        score += (points * scoreMultiplier);
        scoreText.setText(String.valueOf(score));
    }

    private void decreaseScore(int points){
        // decreases the players score when they pick the wrong answer
        score -= points;
        scoreText.setText(String.valueOf(score));
    }

    public static void setGameDifficulty(int difficulty){
        gameDifficulty = difficulty;
    }

    public void setFinalScore(int score){
        finalScore = score;
    }

    public static void setPlayBackgroundMusic(boolean play){
        playBackgroundMusic = play;
    }

    public static void setPlaySoundEffects(boolean play){
        playSoundEffects = play;
    }

    private void startGameOverActivity() {
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("final score", finalScore);
        startActivity(intent);
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
        mControlsView.setVisibility(View.VISIBLE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);

        // Makes it so that the game content fills the screen when ui is hidden
        mContentView.setFitsSystemWindows(false);
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
