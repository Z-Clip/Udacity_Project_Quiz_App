package com.example.android.quizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Initialize global variables
    Editable userName;
    Editable userEmail;
    Boolean inputsReceived = false;

    int question = 1;
    String[][] questionAry;

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

    }

    /* This method is executed when a user clicks the easy button on the initial layout.
     * It first checks to make sure the user has inputted something in both the name and
     * email fields. If not, it fires a toast message and the method breaks.
     * If the name and email have been supplied, the layout is updated to activity_main,
     * the question array (questionAry) is populated with easy questions, and the views in
     * activity_main are populated accordingly.
     */
    public void setEasyQuizParams(View view) {
        setUserInfo();
        if (!inputsReceived) {
            return;
        }
        setContentView(R.layout.activity_main);
        questionAry = new String[][] {
                {""},
                {"Easy Question 1","Type","Easy Question 1 Options","Easy Question 1 Answer","Image"},
                {"Easy Question 2","Type","Easy Question 2 Options","Easy Question 2 Answer","Image"},
                {"Easy Question 3","Type","Easy Question 3 Options","Easy Question 3 Answer","Image"},
                {"Easy Question 4","Type","Easy Question 4 Options","Easy Question 4 Answer","Image"},
                {"Easy Question 5","Type","Easy Question 5 Options","Easy Question 5 Answer","Image"}
        };

    }

    /* This method is executed when a user clicks the medium button on the initial layout.
     * It first checks to make sure the user has inputted something in both the name and
     * email fields. If not, it fires a toast message and the method breaks.
     * If the name and email have been supplied, the layout is updated to activity_main,
     * the question array (questionAry) is populated with medium questions, and the views in
     * activity_main are populated accordingly.
     */
    public void setMediumQuizParams(View view) {
        setUserInfo();
        if (!inputsReceived) {
            return;
        }
        setContentView(R.layout.activity_main);
        questionAry = new String[][] {
                {""},
                {"Medium Question 1","Type","Medium Question 1 Options","Medium Question 1 Answer","Image"},
                {"Medium Question 2","Type","Medium Question 2 Options","Medium Question 2 Answer","Image"},
                {"Medium Question 3","Type","Medium Question 3 Options","Medium Question 3 Answer","Image"},
                {"Medium Question 4","Type","Medium Question 4 Options","Medium Question 4 Answer","Image"},
                {"Medium Question 5","Type","Medium Question 5 Options","Medium Question 5 Answer","Image"}
        };
    }

    /* This method is executed when a user clicks the hard button on the initial layout.
     * It first checks to make sure the user has inputted something in both the name and
     * email fields. If not, it fires a toast message and the method breaks.
     * If the name and email have been supplied, the layout is updated to activity_main,
     * the question array (questionAry) is populated with hard questions, and the views in
     * activity_main are populated accordingly.
     */
    public void setHardQuizParams(View view) {
        setUserInfo();
        if (!inputsReceived) {
            return;
        }
        setContentView(R.layout.activity_main);
        questionAry = new String[][] {
                {""},
                {"Hard Question 1","Type","Hard Question 1 Options","Hard Question 1 Answer","Image"},
                {"Hard Question 2","Type","Hard Question 2 Options","Hard Question 2 Answer","Image"},
                {"Hard Question 3","Type","Hard Question 3 Options","Hard Question 3 Answer","Image"},
                {"Hard Question 4","Type","Hard Question 4 Options","Hard Question 4 Answer","Image"},
                {"Hard Question 5","Type","Hard Question 5 Options","Hard Question 5 Answer","Image"}
        };
    }
}
