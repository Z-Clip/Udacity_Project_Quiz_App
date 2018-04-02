package com.example.android.quizapp;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import android.hardware.SensorManager;


public class MainActivity extends AppCompatActivity {

    //Global variables
    Editable userName;
    Editable userEmail;
    String difficulty;
    String phase;
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


    // Save off key global variables on saveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("JKL Test", "Vars saved off successfully");
        outState.putCharSequence("userName" , userName);
        outState.putCharSequence("userEmail" , userEmail);
        outState.putString("phase" , phase);
        outState.putString("difficulty" , difficulty);
        outState.putBoolean("inputsReceived" , inputsReceived);
        outState.putInt("question" , question);
        outState.putInt("possibleScore" , possibleScore);
        outState.putStringArray("userInputAry" , userInputAry);
        outState.putIntArray("scoreAry" , scoreAry);
    }

    //On create method. Loads the initial layout.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_layout);
        editTextListeners();  //Configure listeners for the EditText objects on the first layout
        phase = "initial";
        rebuildState(savedInstanceState);  //Rebuild vars on a state change
    }

    //Rebuild key global variables on a state change
    public void rebuildState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            userName = (Editable) savedInstanceState.getCharSequence("userName" , userName);
            userEmail = (Editable) savedInstanceState.getCharSequence("userEmail" , userEmail);
            phase = savedInstanceState.getString("phase");
            difficulty = savedInstanceState.getString("difficulty");
            inputsReceived = savedInstanceState.getBoolean("inputsReceived");
            question = savedInstanceState.getInt("question");
            possibleScore = savedInstanceState.getInt("possibleScore");
            userInputAry = savedInstanceState.getStringArray("userInputAry");
            scoreAry = savedInstanceState.getIntArray("scoreAry");
        }
    }

    /* In the very first layout, there are two EditText views for userName and userEmail. If the
     * configuration changes before you've clicked a difficulty button, the user input is lost
     * because the EditText views' values have not been saved off to variables yet. These listeners
     * attempt to save those values to variables on IME action 'done'. It's not bullet proof, but
     * it's the best I've got right now.
     */
    public void editTextListeners() {
        userNameViewID = findViewById(R.id.userName);
        userNameViewID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    userName = userNameViewID.getText();
                    Log.v("JKL Test", "userName listener worked");
                    return true;
                }
                return false;
            }
        });
        userEmailViewID = findViewById(R.id.userEmail);
        userEmailViewID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    userEmail = userEmailViewID.getText();
                    Log.v("JKL Test", "userEmail listener worked");
                    return true;
                }
                return false;
            }
        });
    }

    //If a configuration change is detected, execute changeLayoutBasedOnOrientation
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeLayoutBasedOnOrientation(newConfig);
    }

    /* Executed when a configuration change is detected. Flips between the portrait and landscape
     * orientation for whatever layout is current active (base on the 'phase' of the app). It also
     * rebuilds the views for the layout.
     */
    public void changeLayoutBasedOnOrientation (Configuration newConfig) {
        //Portrait orientation
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (phase.equals("initial")) {
                setContentView(R.layout.initial_layout);
                userNameViewID = findViewById(R.id.userName);
                userNameViewID.setText(userName);
                userEmailViewID = findViewById(R.id.userEmail);
                userEmailViewID.setText(userEmail);
            } else if (phase.equals("quiz")) {
                setContentView(R.layout.activity_main);
                populateArrays();
                defineObjects();
                setQuestionDisplay();
            } else if (phase.equals("final")) {
                setContentView(R.layout.final_layout);
                populateArrays();
                compileAndDisplayResults();
            }

            //Landscape orientation
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (phase.equals("initial")) {
                setContentView(R.layout.initial_layout_landscape);
                userNameViewID = findViewById(R.id.userName);
                userNameViewID.setText(userName);
                userEmailViewID = findViewById(R.id.userEmail);
                userEmailViewID.setText(userEmail);
            } else if (phase.equals("quiz")) {
                setContentView(R.layout.activity_main_landscape);
                populateArrays();
                defineObjects();
                setQuestionDisplay();
            } else if (phase.equals("final")) {
                setContentView(R.layout.final_layout_landscape);
                populateArrays();
                compileAndDisplayResults();
            }
        }
    }

    /*Executed when a difficulty button is selected on the initial layout.
     * Receives the user input for name and email into global vars.
     */
    private void setUserInfo() {
        userNameViewID = findViewById(R.id.userName);
        userName = userNameViewID.getText();
        userEmailViewID = findViewById(R.id.userEmail);
        userEmail = userEmailViewID.getText();

        //Check to ensure both fields have been filled out.
        if (phase.equals("quiz") && userNameViewID.length() != 0 && userEmailViewID.length() !=0) {
            inputsReceived = true;
        } else {
            Toast.makeText(this,"Name and email are both required to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    //Executed if the user clicks the easy button on the initial layout
    public void setEasyQuizParams(View view) {
        phase = "quiz";
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
        phase = "quiz";
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
        phase = "quiz";
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
        /* The following arrays are not defined in populateArrays because we don't want their values
         * reverting on a state change.
         */
        possibleScore = (questionAry.length - 1);  //The possible score is equal to the number of questions (0 key does not correspond with a question)
        userInputAry = new String[questionAry.length];
        scoreAry = new int[questionAry.length];
        //Change the layout to activity_main
        if (getResources().getConfiguration().orientation == 1) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_landscape);
        }
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

    /* Called when the user selects the "next question" button. "GONE-s" the dynamic views, increments
     * the question variable, and re-sets the display.
     */
    public void nextQuestion(View view) {
        checkAnswers();
        goneOptionViews();
        question = question + 1;
        if (question < questionAry.length) {
            setQuestionDisplay();
        } else {
            phase = "final";
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

    /* This method gets the user input for a question, saves it to the userInputAry (for retrieval in
     * the detailed score breakdown, and determines whether the user's answer is correct. The method
     * used to evaluate the answer depends on the type of question.
     * 'free text' questions check to see whether the user's input contains one or more keywords.
     * 'single choice' questions check to see whether the user's selection matches the correct answer.
     * 'multiple choice' questions compare the user's selection(s) to the correct answer(s).
     */
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
            freeTextView.setText("");  //Clear the text

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
                    buttonView.setChecked(false);  //Clear the selection
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

    // This method compiles the user's score and displays the results.
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

        if (getResources().getConfiguration().orientation == 1) {
            setContentView(R.layout.final_layout);
        } else {
            setContentView(R.layout.final_layout_landscape);
        }

        TextView summaryInfo = findViewById(R.id.score_summary_text_view);
        summaryInfo.setText(summaryInfoText);

        TextView finalScorePercent = findViewById(R.id.percent_correct_text_view);
        finalScorePercent.setText(percentCorrect);
    }

    //Resets all the variables except for userName and userEmail and reverts to the initial layout.
    public void resetQuiz(View view) {
        phase = "initial";
        if (getResources().getConfiguration().orientation == 1) {
            setContentView(R.layout.initial_layout);
        } else {
            setContentView(R.layout.initial_layout_landscape);
        }

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

    /* Executed if the user selects the button to email a detailed score breakdown.  The bulk of
     * this method involves the creation of the text of the body of the email. There must be a better
     * way to do this... but this works for now.
     */
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
