package com.example.android.quizapp;

import android.content.Intent;
import android.net.Uri;
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
    int possibleScore;
    //Global Arrays
    String[] questionAry;
    String[] typeAry;
    String[] optionAry;
    String[] answerAry;
    String[] userInputAry;  //Store in an array to provide a detailed breakdown in the email
    int[] scoreAry;  //Store in an array to provide a detailed breakdown in the email
    //Globally defined object IDs
    EditText userNameViewID;
    EditText userEmailViewID;
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
    private void setUserInfo() {
        userNameViewID = findViewById(R.id.userName);
        userName = userNameViewID.getText();
        userEmailViewID = findViewById(R.id.userEmail);
        userEmail = userEmailViewID.getText();

        //Check to ensure both fields have been filled out.
        if (userNameViewID.length() != 0 && userEmailViewID.length() !=0) {
            inputsReceived = true;
        } else {
            Toast.makeText(this,"Name and email are both required to proceed.", Toast.LENGTH_SHORT).show();
        }
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
    public void buildQuestionnaire() {
        //Build easy question, type, option, and answer arrays.
        populateArrays();
        //Change the layout to activity_main
        setContentView(R.layout.activity_main);
        //Define global objects
        defineObjects();
        setQuestionDisplay();
    }

    // This method sets global array variables based on the string arrays stored in the array resource
    public void populateArrays() {
        //Grab the needed string arrays from the arrays resource
        questionAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_questions", "array", getPackageName()));
        typeAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_types", "array", getPackageName()));
        optionAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_options", "array", getPackageName()));
        answerAry = getResources().getStringArray(getResources().getIdentifier(difficulty+"_answers", "array", getPackageName()));

        possibleScore = (questionAry.length - 1);  //The possible score is equal to the number of questions (0 key does not correspond with a question)
        userInputAry = new String[questionAry.length];
        scoreAry = new int[questionAry.length];
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

    /*Called when the user selects the "next question" button. "GONE-s" the dynamic views, increments
     * the question variable, and re-sets the display.
     */
    public void nextQuestion(View view) {
        checkAnswers();
        goneOptionViews();
        question = question + 1;
        if (question < questionAry.length) {
            setQuestionDisplay();
        } else {
            compileAndDisplayResults();
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
            String text = freeTextView.getText().toString().toUpperCase();
            userInputAry[question] = text;
            String[] correctAnswerAry = answerAry[question].split(":");
            for (int i = 0 ; i < correctAnswerAry.length ; i++) {
                if (text.contains(correctAnswerAry[i])) {
                    scoreAry[question] = 1;
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
                        scoreAry[question] = 1;
                    }
                    buttonView.setChecked(false);
                    break;
                }
            }

        } else if (questionType.equals("multiple choice")) {
            String[] correctAnswerAry = answerAry[question].split(":");
            /* possibleScore already has a count of 1 associated with this question, but we need to add count
             * for the additional possible correct answers. 0 key does not correspond with an answer.
             * correctAnswerAry.length - 2 in the number of possible additional correct answers we need
             * to add to possibleScore.
             */
            possibleScore = possibleScore + (correctAnswerAry.length - 2);
            int scoreCount = 0;
            String answerString = ":" + answerAry[question] + ":";
            for (int i = 1 ; i <= 4 ; i++) {
                //Get the checkbox view ID corresponding with i
                int viewID = getResources().getIdentifier("checkbox_option_" + i, "id", getPackageName());
                CheckBox checkBoxObject = findViewById(viewID);
                String text = (String) checkBoxObject.getText();
                if (checkBoxObject.isChecked()) {
                    userInputAry[question] = userInputAry[question] + ":" + text;
                    if (answerString.contains(":" + text + ":")) {
                        scoreCount = scoreCount + 1;
                    } else {
                        scoreCount = scoreCount - 1;
                    }
                    checkBoxObject.setChecked(false);  //Clear the check mark
                }
                if (scoreCount <= 0) {
                    scoreAry[question] = 0;
                } else {
                    scoreAry[question] = scoreCount;
                }
            }
        }
    }

    /* In order for setQuestionDisplay to work correctly, it assumes that all of the option views
     * already have their visibility set to GONE. This is designed to be executed just before the
     * question variable increments, and it clears out the option views.
     */
    public void goneOptionViews() {
        picture.setVisibility(View.GONE);
        freeTextView.setVisibility(View.GONE);
        singleChoiceView.setVisibility(View.GONE);
        multipleChoiceView.setVisibility(View.GONE);
    }

    public void compileAndDisplayResults() {
        int finalScore = 0;
        for (int i = 0 ; i < scoreAry.length ; i++) {
            finalScore = finalScore + scoreAry[i];
        }

        String summaryInfoText = "Your Name:  " + userName;
        summaryInfoText = summaryInfoText + "\n\n" + "Your Email:  " + userEmail;
        summaryInfoText = summaryInfoText + "\n\n" + "Possible Score:  " + possibleScore;
        summaryInfoText = summaryInfoText + "\n\n" + "Your Score:  " + finalScore;

        double percentCorrectDouble = (double)finalScore / (double)possibleScore;
        String percentCorrect = (String.format("%.0f" , (100 * percentCorrectDouble))) + "%";

        setContentView(R.layout.final_layout);

        TextView summaryInfo = findViewById(R.id.score_summary_text_view);
        summaryInfo.setText(summaryInfoText);

        TextView finalScorePercent = findViewById(R.id.percent_correct_text_view);
        finalScorePercent.setText(percentCorrect);
    }

    public void resetQuiz(View view) {
        setContentView(R.layout.initial_layout);

        //Carry over userName and userEmail
        userNameViewID = findViewById(R.id.userName);
        userEmailViewID = findViewById(R.id.userEmail);
        userNameViewID.setText(userName);
        userEmailViewID.setText(userEmail);

        //Reset all vars and clear arrays
        possibleScore = 0;
        difficulty = null;
        question = 1;
        questionAry = null;
        typeAry = null;
        optionAry = null;
        answerAry = null;
        userInputAry = null;
        scoreAry = null;
    }

    public void emailUserScoreBreakdown(View view) {
        String emailBody = "Hello " + userName +", \n\n";
        emailBody = emailBody + "  You have chosen to receive a detailed breakdown of your 'Think You Know: Biology Edition' score. \n\n";
        for (int i = 1 ; i < questionAry.length ; i++) {
            String question = questionAry[i];
            emailBody = emailBody + "Question " + i + ":\n" + question + "\n\n";

            String questionType = typeAry[i];
            if (questionType.equals("free text")) {
                emailBody = emailBody + "Your Answer :\n    " + userInputAry[i] + "\nYour answer needed to include one of the following words or phrases:\n    ";
                String[] correctAnswersAry = answerAry[i].split(":");
                String correctAnswerString = null;
                for (int line = 0 ; line < correctAnswersAry.length ; line++) {
                    if (correctAnswerString == null) {
                        correctAnswerString = correctAnswersAry[line];
                    } else if (line == correctAnswersAry.length-1) {
                        correctAnswerString = correctAnswerString + ", or " + correctAnswersAry[line];
                    } else {
                        correctAnswerString = correctAnswerString + ", " + correctAnswersAry[line];
                    }
                }
                emailBody = emailBody + correctAnswerString + "\n\n";

            } else if (questionType.equals("single choice")) {
                emailBody = emailBody + "Your Answer :\n    " + userInputAry[i] + "\nCorrect Answer:\n    " + answerAry[i] + "\n\n";

            } else if (questionType.equals("multiple choice")) {
                emailBody = emailBody + "Your Answer(s) :\n    ";
                String[] userAnswerAry = userInputAry[i].split(":");
                String userAnswerString = "";
                for (int line = 1 ; line < userAnswerAry.length ; line++) {
                    if (userAnswerString.length() == 0) {
                        userAnswerString = userAnswerAry[line];
                    } else if (line == userAnswerAry.length-1) {
                        userAnswerString = userAnswerString + ", and " + userAnswerAry[line];
                    } else {
                        userAnswerString = userAnswerString + ", " + userAnswerAry[line];
                    }
                }
                emailBody = emailBody + userAnswerString + "\nCorrect Answer(s):\n    ";
                String[] correctAnswersAry = answerAry[i].split(":");
                String correctAnswerString = "";
                for (int line = 1 ; line < correctAnswersAry.length ; line++) {
                    if (correctAnswerString.length() == 0) {
                        correctAnswerString = correctAnswersAry[line];
                    } else if (line == correctAnswersAry.length-1) {
                        correctAnswerString = correctAnswerString + ", and " + correctAnswersAry[line];
                    } else {
                        correctAnswerString = correctAnswerString + ", " + correctAnswersAry[line];
                    }
                }
                emailBody = emailBody + correctAnswerString + "\n\n";
            }
        }

        int finalScore = 0;
        for (int i = 0 ; i < scoreAry.length ; i++) {
            finalScore = finalScore + scoreAry[i];
        }

        emailBody = emailBody + "Possible points = " + possibleScore + "\n" + "Your point total = " + finalScore + "\n\nThanks for playing!" ;

        String[] userEmailAry = {userEmail.toString()};

        Intent sendEmail = new Intent(Intent.ACTION_SENDTO);
        sendEmail.setData(Uri.parse("mailto:"));
        sendEmail.putExtra(Intent.EXTRA_EMAIL,userEmailAry);
        sendEmail.putExtra(Intent.EXTRA_SUBJECT, userName + "'s 'Think You Know :Biology Edition' Score Breakdown");
        sendEmail.putExtra(Intent.EXTRA_TEXT, emailBody);
        if (sendEmail.resolveActivity(getPackageManager()) != null) {
            startActivity(sendEmail);
        } else {
            Toast.makeText(this,"Could not launch email app.", Toast.LENGTH_SHORT).show();
        }
    }

}
