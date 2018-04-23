package com.example.mathwiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    public String gameDifficulty = "Easy";
    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.medButton);
        hardButton = findViewById(R.id.hardButton);

        easyButton.setOnClickListener(this);
        mediumButton.setOnClickListener(this);
        hardButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.easyButton){
            // sets the game difficulty to easy
            gameDifficulty = "Easy";
            // sets the easy button to the selected button colour
            easyButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
            // sets the other buttons to the non-selected button colour
            mediumButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
            hardButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
        }
        if (v.getId() == R.id.medButton){
            // sets the game difficulty to medium
            gameDifficulty = "Medium";
            // sets the easy button to the selected button colour
            mediumButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
            // sets the other buttons to the non-selected button colour
            easyButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
            hardButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
        }
        if (v.getId() == R.id.hardButton){
            // sets the game difficulty to hard
            gameDifficulty = "Hard";
            hardButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
            // sets the other buttons to the non-selected button colour
            easyButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
            mediumButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
        }
    }
}
