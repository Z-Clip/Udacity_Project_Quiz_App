package com.example.android.quizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    String[] optionAry;
    String[] answerAry;
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

        TextView questionText = findViewById(R.id.question_text);
        questionText.setText(String.valueOf(questionAry[question]));

        String questionType = typeAry[question];
        if (questionType.equals("free text")) {
            EditText freeTextView = findViewById(R.id.free_text_answer);
            freeTextView.setVisibility(View.VISIBLE);  //Make visible

        } else if (questionType.equals("single choice")) {
            RadioGroup singleChoiceView = findViewById(R.id.radio_buttons);
            singleChoiceView.setVisibility(View.VISIBLE);  //Make visible
            //Set the text for the radio buttons
            String[] radioButtonArray = optionAry[question].split(":");
            for (int i = 1; i < radioButtonArray.length; i++) {
                //Get the view ID corresponding with i
                int viewID = getResources().getIdentifier("radio_option_" + i, "id", getPackageName());
                RadioButton buttonText = findViewById(viewID);
                buttonText.setText(radioButtonArray[i]);
            }

        } else if (questionType.equals("multiple choice")) {
            LinearLayout multipleChoiceView = findViewById(R.id.checkboxes);
            multipleChoiceView.setVisibility(View.VISIBLE);
            //Set the text for the checkboxes
            String[] checkBoxArray = optionAry[question].split(":");
            for (int i = 1; i < checkBoxArray.length; i++) {
                //Get the view ID corresponding with i
                int viewID = getResources().getIdentifier("checkbox_option_" + i, "id", getPackageName());
                CheckBox buttonText = findViewById(viewID);
                buttonText.setText(checkBoxArray[i]);
            }
        }
    }

    /* In order for setQuestionDisplay to work correctly, it assumes that all of the option views
     * already have their visibility set to GONE. This is designed to be executed just before the
     * question variable increments, and it clears out the option views.
     */
    public void goneOptionViews () {
        String questionType = typeAry[question];
        if (questionType.equals("free text")) {
            EditText freeTextView = findViewById(R.id.free_text_answer);
            freeTextView.setVisibility(View.GONE);  //Make visible
        } else if (questionType.equals("single choice")) {
            RadioGroup singleChoiceView = findViewById(R.id.radio_buttons);
            singleChoiceView.setVisibility(View.GONE);  //Make visible
        } else if (questionType.equals("multiple choice")) {
            LinearLayout multipleChoiceView = findViewById(R.id.checkboxes);
            multipleChoiceView.setVisibility(View.GONE);
        }
    }

    /*Called when the user selects the "next question" button. "GONE-s" the option view and increments
    * the question variable.
    */
    public void nextQuestion (View view) {
        goneOptionViews();
        question = question + 1;
        setQuestionDisplay();
    }

    public void setEasyQuizParams(View view) {
        setUserInfo();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }

        //Build easy question, type, option, and answer arrays.
        difficulty="easy";

        //Grab the needed string arrays from the arrays resource
        questionAry = getResources().getStringArray(R.array.easy_questions);
        typeAry = getResources().getStringArray(R.array.easy_types);
        optionAry = getResources().getStringArray(R.array.easy_options);
        answerAry = getResources().getStringArray(R.array.easy_answers);

        //Change the layout to activity_main
        setContentView(R.layout.activity_main);
        setQuestionDisplay();
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
