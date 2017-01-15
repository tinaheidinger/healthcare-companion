package com.example.maria.basestationapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

/*
* Grundgerüst des Homescreen der Anwendung
* Avatar mit ImageButton-Funktionen enthalten
* */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getActionBar().hide();

        setContentView(R.layout.activity_main);

        ImageButton goal = (ImageButton) findViewById(R.id.goalButton);
        goal.setOnClickListener(new View.OnClickListener() {

            //when pressed, the levels.class is run
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DailyGoals.class);
                startActivityForResult(intent, 0);
            }
        });

        ImageButton reminder = (ImageButton) findViewById(R.id.reminderButton);
        reminder.setOnClickListener(new View.OnClickListener() {

             @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateReminder.class);
                startActivityForResult(intent, 0);
            }
        });
    }

}
