package com.example.android.quizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {

    //Global variables
    Editable userName;
    Editable userEmail;
    String difficulty;
    Boolean inputsReceived = false;
    int question = 1;
    //Global Arrays
    String[] questionAry;
    String[] typeAry;
    String[] optionAry;
    String[] answerAry;
    String[] userAnswers;
    //Globally defined objects
    EditText freeTextView;
    TextView questionHeader;
    TextView questionText;
    RadioGroup singleChoiceView;
    LinearLayout multipleChoiceView;
    ImageView picture;

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
        questionHeader.setText(String.valueOf("Question " + question));
        questionText.setText(String.valueOf(questionAry[question]));

        int imageID = getResources().getIdentifier(difficulty+"_"+question ,"drawable",getPackageName());
        if (imageID >= 0) {
            picture.setVisibility(View.VISIBLE);
            picture.setImageResource(imageID);
        }

        String questionType = typeAry[question];
        if (questionType.equals("free text")) {
            freeTextView.setVisibility(View.VISIBLE);  //Make visible

        } else if (questionType.equals("single choice")) {
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
        picture.setVisibility(View.GONE);
        String questionType = typeAry[question];
        if (questionType.equals("free text")) {
            freeTextView.setVisibility(View.GONE);  //Make visible
        } else if (questionType.equals("single choice")) {
            singleChoiceView.setVisibility(View.GONE);  //Make visible
        } else if (questionType.equals("multiple choice")) {
            multipleChoiceView.setVisibility(View.GONE);
        }
    }

    /*Called when the user selects the "next question" button. "GONE-s" the visible views, increments
    * the question variable, and re-sets the display.
    */
    public void nextQuestion (View view) {
        goneOptionViews();
        question = question + 1;
        if (question < questionAry.length) {
            setQuestionDisplay();
        } else {
            calculateScore();
        }
    }

    public void calculateScore() {}

    public void populateArrays() {
        //Grab the needed string arrays from the arrays resource
        questionAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_questions", "array", getPackageName()));
        typeAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_types", "array", getPackageName()));
        optionAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_options", "array", getPackageName()));
        answerAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_answers", "array", getPackageName()));
    }

    public void defineObjects() {
        freeTextView = findViewById(R.id.free_text_answer);
        questionHeader = findViewById(R.id.header_text_view);
        questionText = findViewById(R.id.question_text);
        singleChoiceView = findViewById(R.id.radio_buttons);
        multipleChoiceView = findViewById(R.id.checkboxes);
        picture = findViewById(R.id.image);
    }

    public void setEasyQuizParams(View view) {
        setUserInfo();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }
        difficulty="easy";
        buildQuestionnaire();
    }

    public void setMediumQuizParams(View view) {
        setUserInfo();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }
        difficulty="medium";
        buildQuestionnaire();
    }

    public void setHardQuizParams(View view) {
        setUserInfo();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }
        difficulty="hard";
        buildQuestionnaire();
    }

    public void buildQuestionnaire () {
        //Build easy question, type, option, and answer arrays.
        populateArrays();
        //Change the layout to activity_main
        setContentView(R.layout.activity_main);
        //Define global objects
        defineObjects();
        setQuestionDisplay();
    }

}
