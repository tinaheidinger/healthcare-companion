package com.example.maria.basestationapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class Reminder extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "CreateDailyGoal.class";
    private static String name = "";
    private static String emoji = "";

    private EditText editText, editEmoji, editDate, editTime;
    private Button furtherButton;
    private Button save;
    private Button backButton_reminder;
    private Button backButton_picker;
    private ImageButton btn_date;
    private ImageButton btn_time;

    private DatePickerDialog datePicker;
    private TimePicker timePicker;

    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        TextView titel = (TextView)findViewById(R.id.headerEditReminder);
        editText = (EditText) findViewById(R.id.titleReminder);
        editEmoji = (EditText) findViewById(R.id.emojiReminder);

        furtherButton = (Button) findViewById(R.id.furtherBtnReminder);
        furtherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = editText.getText().toString();
                emoji = EmojiMap.replaceUnicodeEmojis(editEmoji.getText().toString());

                InputMethodManager inputManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(furtherButton.getWindowToken(),0);
                loadPicker();
                Log.d(TAG,name);
            }

        });
        backButton_reminder = (Button) findViewById(R.id.backBtnReminder);
        backButton_reminder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }

        });

       Intent intent = getIntent();
        if (null != intent) {
            String data[]= intent.getStringArrayExtra("reminder");
            if(data!=null) {
                editText.setText(data[0]);
                editEmoji.setText(data[1]);
            }
        }


    }

    private void loadPicker(){
        setContentView(R.layout.activity_reminder_datepicker_time);

        backButton_reminder =(Button) findViewById(R.id.backBtnReminder);
        backButton_reminder.setOnClickListener(this);

        btn_date = (ImageButton) findViewById(R.id.btn_date);
        btn_date.setOnClickListener(this);

        btn_time = (ImageButton) findViewById(R.id.btn_time);
        btn_time.setOnClickListener(this);

        editDate = (EditText) findViewById(R.id.in_date);
        editTime = (EditText) findViewById(R.id.in_time);


    }

    @Override
    public void onClick(View v) {

        if (v == btn_date) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);


            datePicker = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            editDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, year, month, day);
            datePicker.show();
        }
        if (v == btn_time) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            editTime.setText(hourOfDay + ":" + minute);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        }

        if(v== backButton_reminder){
            setContentView(R.layout.activity_reminder);

            TextView titel = (TextView)findViewById(R.id.headerEditReminder);
            editText = (EditText) findViewById(R.id.titleReminder);
            editEmoji = (EditText) findViewById(R.id.emojiReminder);

            furtherButton = (Button) findViewById(R.id.furtherBtnReminder);
            furtherButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    name = editText.getText().toString();
                    emoji = EmojiMap.replaceUnicodeEmojis(editEmoji.getText().toString());
                    loadPicker();
                    Log.d(TAG,name);
                }

            });
            backButton_picker =(Button) findViewById(R.id.backBtnReminder);
            backButton_picker.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            editText.setText(name);
            editEmoji.setText(EmojiMap.replaceCheatSheetEmojis(emoji));
        }
    }
}
