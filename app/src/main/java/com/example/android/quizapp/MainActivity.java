package com.example.android.quizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {

    //Initialize global variables
    Editable userName;
    Editable userEmail;
    String difficulty;
    Boolean inputsReceived = false;
    int question = 1;
    String[] questionAry;
    String[] typeAry;
    String[][] optionAry;
    String[][] answerAry;
    String[] userAnswers;

    //On create method. Loads the initial layout.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_layout);
    }

    //Receives the user input for name and email into global vars and switches to the main layout.
    private void setUserInfo () {
        EditText getUserName = findViewById(R.id.userName);
        userName = getUserName.getText();

        EditText getUserEmail = findViewById(R.id.userEmail);
        userEmail = getUserEmail.getText();

        //Check to ensure both fields have been filled out.
        if (getUserName.length() != 0 && getUserEmail.length() !=0) {
            inputsReceived = true;
        } else {
            Toast.makeText(this,"Name and email are both required to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * This method assesses the questionAry, finds the values that correspond with the current
    * question, and updates the views in the activity_main layout accordingly.
     */
    public void setQuestionDisplay() {
        TextView questionHeader = findViewById(R.id.header_text_view);
        questionHeader.setText(String.valueOf("Question " + question));
    }

    public void nextQuestion (View view) {
        question = question + 1;
    }

    public void setEasyQuizParams(View view) {
        setUserInfo();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }

        //Build easy question, type, option, and answer arrays.
        difficulty="easy";

        //The question array is a one-dimensional string array defined in the arrays resource
        questionAry = getResources().getStringArray(R.array.easy_questions);
        //The type array is a one-dimensional string array defined in the arrays resource
        typeAry = getResources().getStringArray(R.array.easy_types);

        /*For both the optionAry and answerAry:
        * The string array is initially defined as a one-dimensional array stored in the arrays resource,
        * but it needs to be a two dimensional array. Loop over the values in the original array
        * and create the secondary arrays. Strings forming the secondary arrays use '@' as the delimiter
        * between elements.
         */
        String[] singleDimensionalOptionArray = getResources().getStringArray(R.array.easy_options);
        for (int i = 0 ; i <= singleDimensionalOptionArray.length ; i++) {
            String[] tempAry = singleDimensionalOptionArray[i].split("@");
            optionAry[i][0] = null;  //I want the key to line up with the question #, so skip 0
            for (int line = 1 ; line < tempAry.length ; line++) {
                optionAry[i][line] = tempAry[line];
            }
        }

        String[] singleDimensionalAnswerArray = getResources().getStringArray(R.array.easy_answers);
        for (int i = 0 ; i <= singleDimensionalAnswerArray.length ; i++) {
            String[] tempAry = singleDimensionalAnswerArray[i].split("@");
            for (int line = 0; line <= tempAry.length; line++) {
                answerAry[i][line] = tempAry[line];
            }
        }

        //Change the layout to activity_main
        setContentView(R.layout.activity_main);
    }

    public void setMediumQuizParams(View view) {
        setUserInfo();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }

        difficulty="medium";
        //Change the layout to activity_main
        setContentView(R.layout.activity_main);
    }

    public void setHardQuizParams(View view) {
        setUserInfo();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }

        difficulty="hard";
        //Change the layout to activity_main
        setContentView(R.layout.activity_main);
    }

}
