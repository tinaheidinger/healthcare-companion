package com.example.maria.basestationapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class EnteringDailyGoal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entering_daily_goal);

        TextView titel = (TextView)findViewById(R.id.enteringTextview);

        int unicode = 0x1F604;
        String emoji = new String(Character.toChars(unicode));

        titel.setText(emoji);

    }
}
