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

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);

        ImageButton goal = (ImageButton) findViewById(R.id.goalButton);
        goal.setOnClickListener(this);

        ImageButton reminder = (ImageButton) findViewById(R.id.reminderButton);
        reminder.setOnClickListener(this);

        ImageButton fluid = (ImageButton) findViewById(R.id.fluidButton);
        fluid.setOnClickListener(this);

        ImageButton steps = (ImageButton) findViewById(R.id.walkButton);
        steps.setOnClickListener(this);

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
           case R.id.fluidButton:
               Intent intent3 = new Intent(view.getContext(), Fluid.class);
               startActivityForResult(intent3, 0);
               break;
           case R.id.walkButton:
               Intent intent4 = new Intent(view.getContext(), Steps.class);
               startActivityForResult(intent4, 0);
               break;
       }
    }
}
