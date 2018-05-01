package com.example.mathwiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class GameOverActivity extends AppCompatActivity {

    private static final int AUTHENTICATE = 1;
    private Button twitterButton;
    private TextView gameScore;
    private String scoreText;
    private int score;


    Twitter twitter = TwitterFactory.getSingleton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        score = getIntent().getExtras().getInt("final score", 0);

        twitterButton = findViewById(R.id.twitterButton);
        gameScore = findViewById(R.id.gameOverScore);
        scoreText = String.valueOf(score) + " points";
        gameScore.setText(scoreText);
    }

    public void authorise(View view){
        Intent intent = new Intent(this, TwitterActivity.class);
        startActivityForResult(intent, AUTHENTICATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data){
        if (requestCode == AUTHENTICATE && resultCode == RESULT_OK){
            Background.run(new Runnable() {
                @Override
                public void run() {
                    String token = data.getStringExtra("access token");
                    String secret = data.getStringExtra("access token secret");
                    AccessToken accessToken = new AccessToken(token, secret);
                    twitter.setOAuthAccessToken(accessToken);
                }
            });
        }
    }

    public void startGameAgain(View view){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void returnToHomeScreen(View view){
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}
