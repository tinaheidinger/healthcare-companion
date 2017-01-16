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

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{
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
        goal.setOnClickListener(this);

        ImageButton reminder = (ImageButton) findViewById(R.id.reminderButton);
        reminder.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.goalButton:
                   Intent intent1 = new Intent(view.getContext(), DailyGoals.class);
                   startActivityForResult(intent1, 0);
               break;
           case R.id.reminderButton:
               Intent intent2 = new Intent(view.getContext(), ListReminders.class);
               startActivityForResult(intent2, 0);
               break;
       }
    }
}
