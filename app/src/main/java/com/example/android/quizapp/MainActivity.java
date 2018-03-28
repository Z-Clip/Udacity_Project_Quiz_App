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
    String[] userInputAry = new String [6];
    Double[] scoreAry = new Double[6];
    //Globally defined object IDs
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

    //Receives the user input for name and email into global vars.
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

    /* This method assesses the questionAry, finds the values that correspond with the current
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

    public void checkAnswers() {
        String questionType = typeAry[question];

        if (questionType.equals("free text")) {
            String text = freeTextView.getText().toString();
            userInputAry[question] = text;
            String[] correctAnswerAry = answerAry[question].split(":");
            for (int i = 0 ; i < correctAnswerAry.length ; i++) {
                if (text.contains(correctAnswerAry[i])) {
                    scoreAry[question] = 1.0;
                }
            }
            freeTextView.setText("");

        } else if (questionType.equals("single choice")) {
            for (int i = 1; i <= 4; i++) {
                //Get the view ID corresponding with i
                int viewID = getResources().getIdentifier("radio_option_" + i, "id", getPackageName());
                RadioButton buttonView = findViewById(viewID);
                if (buttonView.isChecked()) {
                    String text = (String) buttonView.getText();
                    userInputAry[question] = text;
                    if (text.equals(answerAry[question])) {
                        scoreAry[question] = 1.0;
                    }
                    buttonView.setChecked(false);
                    break;
                }
            }

        } else if (questionType.equals("multiple choice")) {
            userInputAry[question] = ":";
            String[] checkBoxArray = optionAry[question].split(":");
            String[] correctAnswerAry = answerAry[question].split(":");
            Double scoreModifier = 1.0 / ((correctAnswerAry.length)-1);  //Determine how many points each correct answer is worth
            for (int i = 1; i < checkBoxArray.length; i++) {
                //Get the checkbox view ID corresponding with i
                int viewID = getResources().getIdentifier("checkbox_option_" + i, "id", getPackageName());
                CheckBox checkBoxObject = findViewById(viewID);
                if (checkBoxObject.isChecked()) {
                    String input = ":" + checkBoxArray[i] + ":";
                    String answer = answerAry[question]+":";
                    if (answer.contains(input)) {
                        scoreAry[question] = scoreAry[question] + scoreModifier;
                    } else {
                        scoreAry[question] = scoreAry[question] - scoreModifier;
                    }
                    if (scoreAry[question] < 0.0) {scoreAry[question] = 0.0;}  //Negative points are not allowed. Scores are >= 0.0
                    userInputAry[question] = userInputAry[question] + ":" + checkBoxArray[i];  //Record the user's answer
                    checkBoxObject.setChecked(false);  //Clear the check mark
                }
            }
        }
    }

    /* In order for setQuestionDisplay to work correctly, it assumes that all of the option views
     * already have their visibility set to GONE. This is designed to be executed just before the
     * question variable increments, and it clears out the option views.
     */
    public void goneOptionViews () {
        picture.setVisibility(View.GONE);
        freeTextView.setVisibility(View.GONE);
        singleChoiceView.setVisibility(View.GONE);
        multipleChoiceView.setVisibility(View.GONE);
    }

    /*Called when the user selects the "next question" button. "GONE-s" the dynamic views, increments
    * the question variable, and re-sets the display.
    */
    public void nextQuestion (View view) {
        checkAnswers();
        goneOptionViews();
        question = question + 1;
        if (question < questionAry.length) {
            setQuestionDisplay();
        } else {
            calculateScore();
        }
    }

    public void calculateScore() {}

    // This method sets global array variables based on the string arrays stored in the array resource
    public void populateArrays() {
        //Grab the needed string arrays from the arrays resource
        questionAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_questions", "array", getPackageName()));
        typeAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_types", "array", getPackageName()));
        optionAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_options", "array", getPackageName()));
        answerAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_answers", "array", getPackageName()));
    }

    /* There are a number of objects related to views in activity_main that are referenced a number
     * of times throughout the code. This method sets global variables for those object IDs so we
     * don't have to call the findViewById method a bazillion times.
     */
    public void defineObjects() {
        freeTextView = findViewById(R.id.free_text_answer);
        questionHeader = findViewById(R.id.header_text_view);
        questionText = findViewById(R.id.question_text);
        singleChoiceView = findViewById(R.id.radio_buttons);
        multipleChoiceView = findViewById(R.id.checkboxes);
        picture = findViewById(R.id.image);
    }

    //Executed if the user clicks the easy button on the initial layout
    public void setEasyQuizParams(View view) {
        setUserInfo();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }
        difficulty="easy";
        buildQuestionnaire();
    }

    //Executed if the user clicks the medium button on the initial layout
    public void setMediumQuizParams(View view) {
        setUserInfo();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }
        difficulty="medium";
        buildQuestionnaire();
    }

    //Executed if the user clicks the hard button on the initial layout
    public void setHardQuizParams(View view) {
        setUserInfo();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }
        difficulty="hard";
        buildQuestionnaire();
    }

    /* This method executes all the methods necessary to transition from the initial layout to
     * the activity_main layout. It builds variables for the arrays, changes the layout, defines
     * global variables associated with the view objects, and sets the display.
     */
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
