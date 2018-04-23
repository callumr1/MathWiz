package com.example.mathwiz;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends Activity implements View.OnClickListener {

    private int num1;
    private int num2;
    private String question;
    private int gameDifficulty = 0;
    private String symbol;
    private int scoreMultiplier;
    private TextView questionText;
    private Button answerButton1;
    private Button answerButton2;
    private Button answerButton3;
    private Button answerButton4;
    private int answer;
    private String correctButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        questionText = findViewById(R.id.questionText);
        answerButton1 = findViewById(R.id.answerButton1);
        answerButton2 = findViewById(R.id.answerButton2);
        answerButton3 = findViewById(R.id.answerButton3);
        answerButton4 = findViewById(R.id.answerButton4);

        answerButton1.setOnClickListener(this);
        answerButton2.setOnClickListener(this);
        answerButton3.setOnClickListener(this);
        answerButton4.setOnClickListener(this);

        if(gameDifficulty == 0){
            // if the game mode is Easy only allows addition questions
            symbol = "+";
            scoreMultiplier = 1;
        }
        else if(gameDifficulty == 1){
            // if the game mode is Medium only allows multiplication questions
            symbol = "x";
            scoreMultiplier = 2;
        }
        else if (gameDifficulty == 2){
            // if the game mode is Hard only allows division questions
            symbol = "/";
            scoreMultiplier = 3;
        }

        setQuestion();
        setAnswerButtons();
    }

    private void setQuestion() {

        // sets the two numbers that will be used in the question
        Random random = new Random();
        num1 = random.nextInt(50);
        num2 = random.nextInt(10);

        // calculates the answer based on the gameDifficulty
        if (gameDifficulty == 0){
            answer = num1 + num2;
        }
        else if (gameDifficulty == 1){
            answer = num1 * num2;
        }
        else if (gameDifficulty == 2){
            answer = num1 / num2;
        }

        // sets the question and displays it
        question = String.valueOf(num1) + symbol + String.valueOf(num2);
        questionText.setText(question);
    }

    private void setAnswerButtons(){
        try {
            // randomise which button displays that actual answer
            Random random = new Random();
            int n = random.nextInt(3);
            // creates random false answers
            int n1 = random.nextInt(answer * 2);
            int n2 = random.nextInt(answer * 2);
            int n3 = random.nextInt(answer * 2);

            if (n1 == n || n2 == n || n3 == n){
                n1 += 1;
                n2 += 1;
                n3 += 1;
                System.out.println("Same number");
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
            }
            else {
                Toast toast = Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else if(v.getId() == R.id.answerButton2){
            if(correctButton.equals(String.valueOf(answerButton2.getId()))){
                // if the button is the correct answer then give the user points and get a new question
                Toast toast = Toast.makeText(this, "Correct", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                Toast toast = Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else if(v.getId() == R.id.answerButton3){
            if(correctButton.equals(String.valueOf(answerButton3.getId()))){
                // if the button is the correct answer then give the user points and get a new question
                Toast toast = Toast.makeText(this, "Correct", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                Toast toast = Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else if(v.getId() == R.id.answerButton4){
            if(correctButton.equals(String.valueOf(answerButton4.getId()))){
                // if the button is the correct answer then give the user points and get a new question
                Toast toast = Toast.makeText(this, "Correct", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                Toast toast = Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
