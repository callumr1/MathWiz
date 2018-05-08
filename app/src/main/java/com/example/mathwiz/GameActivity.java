package com.example.mathwiz;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends Activity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(playBackgroundMusic){
            // Stop playing the background music
            Background.stopBackgroundMusic(this);
        }
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
            int n = random.nextInt(3);
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
            } else if (n == 4) {
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
                Toast toast = Toast.makeText(this, "Correct", Toast.LENGTH_SHORT);
                toast.show();
                increaseScore(correctPoints);
                playSoundEffect(correctSound);
            }
            else {
                // if the button is not the answer, take away points from the user
                Toast toast = Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT);
                toast.show();
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
}