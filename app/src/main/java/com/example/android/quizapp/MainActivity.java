/* This code is the intellectual property of Jon Leverkuhn, and all content is original unless
 * otherwise specified. All images are royalty-free.
 */

package com.example.android.quizapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Global variables
    public Editable userName;
    public Editable userEmail;
    public String difficulty;
    public String phase = "initial";
    public Boolean inputsReceived = false;
    public int question = 1;
    public int possibleScore = 0;
    public boolean increment = true;
    //Global Arrays
    public String[] questionAry;
    public String[] typeAry;
    public String[] optionAry;
    public String[] answerAry;
    public String[] userInputAry;  //Store in an array to provide a detailed breakdown in the email
    public int[] scoreAry;  //Store in an array to provide a detailed breakdown in the email
    //Globally defined object IDs
    public EditText userNameViewID;
    public EditText userEmailViewID;
    public EditText freeTextView;
    public TextView questionHeader;
    public TextView questionText;
    public RadioGroup singleChoiceView;
    public LinearLayout multipleChoiceView;
    public ImageView picture;


    // Save off key global variables on saveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("userName", userName);
        outState.putCharSequence("userEmail", userEmail);
        outState.putString("phase", phase);
        outState.putString("difficulty", difficulty);
        outState.putBoolean("inputsReceived", inputsReceived);
        outState.putInt("question", question);
        outState.putInt("possibleScore", possibleScore);
        outState.putStringArray("userInputAry", userInputAry);
        outState.putIntArray("scoreAry", scoreAry);
    }

    //On create method. Loads the initial layout.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_layout);
        editTextListeners();  //Configure listeners for the EditText objects on the first layout
        rebuildState(savedInstanceState);  //Rebuild vars on a state change
    }

    //Rebuild key global variables on a state change
    public void rebuildState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            userName = (Editable) savedInstanceState.getCharSequence("userName", userName);
            userEmail = (Editable) savedInstanceState.getCharSequence("userEmail", userEmail);
            phase = savedInstanceState.getString("phase");
            difficulty = savedInstanceState.getString("difficulty");
            inputsReceived = savedInstanceState.getBoolean("inputsReceived");
            question = savedInstanceState.getInt("question");
            possibleScore = savedInstanceState.getInt("possibleScore");
            userInputAry = savedInstanceState.getStringArray("userInputAry");
            scoreAry = savedInstanceState.getIntArray("scoreAry");
            if (phase.equals("initial")) {
                displayUserInfo();
            }
        }
    }

    /*Copied from Stack Overflow thread https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
     *Credit: Navneeth G
     * Allows the soft keyboard to be hidden.
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
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
                    hideSoftKeyboard(MainActivity.this);
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
                    hideSoftKeyboard(MainActivity.this);
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
    public void changeLayoutBasedOnOrientation(Configuration newConfig) {
        //Portrait orientation
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            switch (phase) {
                case "initial":
                    setContentView(R.layout.initial_layout);
                    displayUserInfo();
                    break;
                case "quiz":
                    setContentView(R.layout.activity_main);
                    populateArrays();
                    defineObjects();
                    setQuestionDisplay();
                    break;
                case "final":
                    setContentView(R.layout.final_layout);
                    populateArrays();
                    compileAndDisplayResults();
                    break;
            }
            //Landscape orientation
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            switch (phase) {
                case "initial":
                    setContentView(R.layout.initial_layout_landscape);
                    displayUserInfo();
                    break;
                case "quiz":
                    setContentView(R.layout.activity_main_landscape);
                    populateArrays();
                    defineObjects();
                    setQuestionDisplay();
                    break;
                case "final":
                    setContentView(R.layout.final_layout_landscape);
                    populateArrays();
                    compileAndDisplayResults();
                    break;
            }
        }
    }

    //Sets the views in the initial layout.
    public void displayUserInfo() {
        userNameViewID = findViewById(R.id.userName);
        userNameViewID.setText(userName);
        userEmailViewID = findViewById(R.id.userEmail);
        userEmailViewID.setText(userEmail);
    }

    /*Executed when an onClick method is triggered within one of the edit text views.
     */
    public void setUserInfo(View view) {
        userNameViewID = findViewById(R.id.userName);
        userName = userNameViewID.getText();
        userEmailViewID = findViewById(R.id.userEmail);
        userEmail = userEmailViewID.getText();
    }

    //Ensures a username and email have been supplied before launching the quiz.
    public void completionCheck() {
        //Check to ensure both fields have been filled out.
        if (phase.equals("quiz") && userNameViewID.length() != 0 && userEmailViewID.length() != 0) {
            inputsReceived = true;
        } else {
            Toast.makeText(this, "Name and email are both required to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    //Executed if the user clicks the easy button on the initial layout
    public void setEasyQuizParams(View view) {
        phase = "quiz";
        setUserInfo(null);
        completionCheck();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }
        difficulty = "easy";
        buildQuestionnaire();
    }

    //Executed if the user clicks the medium button on the initial layout
    public void setMediumQuizParams(View view) {
        phase = "quiz";
        setUserInfo(null);
        completionCheck();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }
        difficulty = "medium";
        buildQuestionnaire();
    }

    //Executed if the user clicks the hard button on the initial layout
    public void setHardQuizParams(View view) {
        phase = "quiz";
        setUserInfo(null);
        completionCheck();
        //Prevent the layout from changing if inputsReceived does not evaluate to true
        if (!inputsReceived) {
            return;
        }
        difficulty = "hard";
        buildQuestionnaire();
    }

    //Calculates the possible score for the quiz. populateArrays must be called first.
    public void calculatePossibleScore () {
        possibleScore = 0;
        for (int i = 1 ; i < typeAry.length ; i++) {
            String type = typeAry[i];
            switch (type) {
                case "free text":
                    possibleScore = possibleScore + 1;
                    break;
                case "single choice":
                    possibleScore = possibleScore + 1;
                    break;
                case "multiple choice":
                    String[] array = answerAry[i].split(":");
                    int max = array.length;
                    for (int x = 1 ; x < max ; x++) {
                        possibleScore = possibleScore + 1;
                    }
                    break;
            }
        }
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
        userInputAry = new String[questionAry.length];
        scoreAry = new int[questionAry.length];
        calculatePossibleScore();
        //Change the layout to activity_main
        if (getResources().getConfiguration().orientation == 1) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_landscape);
        }
        //Define global objects
        defineObjects();
        setQuestionDisplay();
        editTextListeners();
    }

    // This method sets global array variables based on the string arrays stored in the array resource
    public void populateArrays() {
        //Grab the needed string arrays from the arrays resource
        questionAry = getResources().getStringArray(getResources().getIdentifier(difficulty + "_questions", "array", getPackageName()));
        typeAry = getResources().getStringArray(getResources().getIdentifier(difficulty + "_types", "array", getPackageName()));
        optionAry = getResources().getStringArray(getResources().getIdentifier(difficulty + "_options", "array", getPackageName()));
        answerAry = getResources().getStringArray(getResources().getIdentifier(difficulty + "_answers", "array", getPackageName()));
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
        increment = true;
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

    public void previousQuestion(View view) {
        increment = false;
        if (question != 1) {
            checkAnswers();
            goneOptionViews();
            question = question - 1;
            setQuestionDisplay();
        }
    }

    /* This method assesses the questionAry, finds the values that correspond with the current
     * question, and updates the views in the activity_main layout accordingly.
     */
    public void setQuestionDisplay() {
        questionHeader.setText(String.valueOf("Question " + question));
        questionText.setText(String.valueOf(questionAry[question]));

        Boolean answerExists = false;
        if (userInputAry[question] != null) {
            answerExists = true;
        }

        int imageID = getResources().getIdentifier(difficulty + "_" + question, "drawable", getPackageName());
        if (imageID >= 0) {
            picture.setVisibility(View.VISIBLE);
            picture.setImageResource(imageID);
        }

        String questionType = typeAry[question];
        switch (questionType) {
            case "free text":
                freeTextView.setVisibility(View.VISIBLE);  //Make visible
                if (answerExists) {
                    freeTextView.setText(String.valueOf(userInputAry[question]));
                } else {
                    freeTextView.setText("");  //Clear the text
                }
                break;

            case "single choice":
                singleChoiceView.setVisibility(View.VISIBLE);  //Make visible
                //Set the text for the radio buttons
                String[] radioButtonArray = optionAry[question].split(":");
                for (int i = 1; i < radioButtonArray.length; i++) {
                    //Get the view ID corresponding with i
                    int viewID = getResources().getIdentifier("radio_option_" + i, "id", getPackageName());
                    RadioButton buttonText = findViewById(viewID);
                    buttonText.setText(radioButtonArray[i]);
                    if (radioButtonArray[i].equals(userInputAry[question])) {
                        buttonText.setChecked(true);
                    }
                    if (!answerExists) {
                        RadioGroup radioGroup = findViewById(R.id.radio_buttons);
                        radioGroup.clearCheck();
                    }
                }
                break;

            case "multiple choice":
                multipleChoiceView.setVisibility(View.VISIBLE);
                //Set the text for the checkboxes
                String[] checkBoxArray = optionAry[question].split(":");
                for (int i = 1; i < checkBoxArray.length; i++) {
                    //Get the view ID corresponding with i
                    int viewID = getResources().getIdentifier("checkbox_option_" + i, "id", getPackageName());
                    CheckBox buttonText = findViewById(viewID);
                    buttonText.setText(checkBoxArray[i]);
                    if (answerExists) {
                        String userInput = ":" + userInputAry[question] +":";
                        if (userInput.contains(":" + checkBoxArray[i] + ":")) {
                            buttonText.setChecked(true);
                        }
                    }
                }
                break;
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
        switch (questionType) {
            case "free text":
                String text = freeTextView.getText().toString().toUpperCase();
                userInputAry[question] = text;
                String[] correctAnswerAry = answerAry[question].split(":");
                for (String answer: correctAnswerAry) {
                    if (text.contains(answer)) {
                        scoreAry[question] = 1;
                    }
                }
                break;
            case "single choice":
                for (int i = 1; i <= 4; i++) {
                    //Get the view ID corresponding with i
                    int viewID = getResources().getIdentifier("radio_option_" + i, "id", getPackageName());
                    RadioButton buttonView = findViewById(viewID);
                    if (buttonView.isChecked()) {
                        String textS = (String) buttonView.getText();
                        userInputAry[question] = textS;
                        if (textS.equals(answerAry[question])) {
                            scoreAry[question] = 1;
                        }
                    }
                }
                break;
            case "multiple choice":
                if (increment) {
                    userInputAry[question] = null;
                    scoreAry[question] = 0;
                }
                int scoreCount = 0;
                String answerString = ":" + answerAry[question] + ":";
                for (int i = 1; i <= 4; i++) {
                    //Get the checkbox view ID corresponding with i
                    int viewID = getResources().getIdentifier("checkbox_option_" + i, "id", getPackageName());
                    CheckBox checkBoxObject = findViewById(viewID);
                    String textM = (String) checkBoxObject.getText();
                    if (checkBoxObject.isChecked()) {
                        userInputAry[question] = userInputAry[question] + ":" + textM;
                        if (answerString.contains(":" + textM + ":")) {
                            scoreCount = scoreCount + 1;
                        } else {
                            scoreCount = scoreCount - 1;
                        }
                        checkBoxObject.setChecked(false);
                    }
                    if (scoreCount <= 0) {
                        scoreAry[question] = 0;
                    } else {
                        scoreAry[question] = scoreCount;
                    }
                }
                break;
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
        for (int point : scoreAry) {
            finalScore = finalScore + point;
        }

        String summaryInfoText = "Your Name:  " + userName;
        summaryInfoText = summaryInfoText + "\n\n" + "Your Email:  " + userEmail;
        summaryInfoText = summaryInfoText + "\n\n" + "Possible Score:  " + possibleScore;
        summaryInfoText = summaryInfoText + "\n\n" + "Your Score:  " + finalScore;

        double percentCorrectDouble = (double) finalScore / (double) possibleScore;
        String percentCorrect = (String.format("%.0f", (100 * percentCorrectDouble))) + "%";

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
        String emailBody = "Hello " + userName + ", \n\n";
        emailBody = emailBody + "  You have chosen to receive a detailed breakdown of your 'Think You Know: Biology Edition' score. \n\n";
        for (int i = 1; i < questionAry.length; i++) {
            String question = questionAry[i];
            emailBody = emailBody + "Question " + i + ":\n" + question + "\n\n";

            String questionType = typeAry[i];
            switch (questionType) {
                case "free text":
                    emailBody = emailBody + "Your Answer :\n    " + userInputAry[i] + "\nYour answer needed to include one of the following words or phrases:\n    ";
                    String[] correctAnswersAry = answerAry[i].split(":");
                    String correctAnswerString = null;
                    for (int line = 0; line < correctAnswersAry.length; line++) {
                        if (correctAnswerString == null) {
                            correctAnswerString = correctAnswersAry[line];
                        } else if (line == correctAnswersAry.length - 1) {
                            correctAnswerString = correctAnswerString + ", or " + correctAnswersAry[line];
                        } else {
                            correctAnswerString = correctAnswerString + ", " + correctAnswersAry[line];
                        }
                    }
                    emailBody = emailBody + correctAnswerString + "\n\n";
                    break;
                case "single choice":
                    emailBody = emailBody + "Your Answer :\n    " + userInputAry[i] + "\nCorrect Answer:\n    " + answerAry[i] + "\n\n";
                    break;
                case "multiple choice":
                    emailBody = emailBody + "Your Answer(s) :\n    ";
                    String[] userAnswerAry = userInputAry[i].split(":");
                    String userAnswerString = "";
                    for (int line = 1; line < userAnswerAry.length; line++) {
                        if (userAnswerString.length() == 0) {
                            userAnswerString = userAnswerAry[line];
                        } else if (line == userAnswerAry.length - 1) {
                            userAnswerString = userAnswerString + ", and " + userAnswerAry[line];
                        } else {
                            userAnswerString = userAnswerString + ", " + userAnswerAry[line];
                        }
                    }
                    emailBody = emailBody + userAnswerString + "\nCorrect Answer(s):\n    ";
                    String[] correctAnswersAryM = answerAry[i].split(":");
                    String correctAnswerStringM = null;
                    for (int line = 1; line < correctAnswersAryM.length; line++) {
                        if (correctAnswerStringM == null) {
                            correctAnswerStringM = correctAnswersAryM[line];
                        } else if (line == correctAnswersAryM.length - 1) {
                            correctAnswerStringM = correctAnswerStringM + ", and " + correctAnswersAryM[line];
                        } else {
                            correctAnswerStringM = correctAnswerStringM + ", " + correctAnswersAryM[line];
                        }
                    }
                    emailBody = emailBody + correctAnswerStringM + "\n\n";
                    break;
            }
        }

        int finalScore = 0;
        for (int points : scoreAry) {
            finalScore = finalScore + points;
        }

        emailBody = emailBody + "Possible points = " + possibleScore + "\n" + "Your point total = " + finalScore + "\n\nThanks for playing!";

        String[] userEmailAry = {userEmail.toString()};

        Intent sendEmail = new Intent(Intent.ACTION_SENDTO);
        sendEmail.setData(Uri.parse("mailto:"));
        sendEmail.putExtra(Intent.EXTRA_EMAIL, userEmailAry);
        sendEmail.putExtra(Intent.EXTRA_SUBJECT, userName + "'s 'Think You Know :Biology Edition' Score Breakdown");
        sendEmail.putExtra(Intent.EXTRA_TEXT, emailBody);
        if (sendEmail.resolveActivity(getPackageManager()) != null) {
            startActivity(sendEmail);
        } else {
            Toast.makeText(this, "Could not launch email app.", Toast.LENGTH_SHORT).show();
        }
    }

}
