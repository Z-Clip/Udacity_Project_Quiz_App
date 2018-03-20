package com.example.android.quizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //Initialize global variables
    Editable userName;
    Editable userEmail;

    int question = 1;

    //On create method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_layout);
    }

    private void setUserInfo () {
        EditText getUserName = findViewById(R.id.userName);
        userName = getUserName.getText();

        EditText getUserEmail = findViewById(R.id.userEmail);
        userEmail = getUserEmail.getText();
    }

    public void setEasyQuizParams(View view) {
        setUserInfo();
        setContentView(R.layout.activity_main);
    }

    public void setMediumQuizParams(View view) {
        setUserInfo();
        setContentView(R.layout.activity_main);
    }

    public void setHardQuizParams(View view) {
        setUserInfo();
        setContentView(R.layout.activity_main);
    }

}
