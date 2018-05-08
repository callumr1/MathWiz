package com.example.mathwiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    public int gameDifficulty;
    private Button easyButton, mediumButton, hardButton;
    private ToggleButton musicToggle, soundEffectsToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.medButton);
        hardButton = findViewById(R.id.hardButton);
        musicToggle = findViewById(R.id.musicToggle);
        soundEffectsToggle = findViewById(R.id.soundEffectsToggle);

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
}
