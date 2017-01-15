package com.example.maria.basestationapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import java.util.Calendar;

public class CreateReminder extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "CreateDailyGoal.class";
    private static String name = "";
    private static String emoji = "";
    private static int[] date_time;

    private EditText editText, editEmoji, editDate, editTime;
    private Button furtherButton_text, furtherButton_picker;
    private Button save;
    private Button backButton_text, backButton_picker;
    private ImageButton btn_date, btn_time;

    private DatePickerDialog datePicker;
    private TimePicker timePicker;

    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        TextView titel = (TextView) findViewById(R.id.headerEditReminder);
        editText = (EditText) findViewById(R.id.titleReminder);
        editEmoji = (EditText) findViewById(R.id.emojiReminder);

        furtherButton_text = (Button) findViewById(R.id.furtherBtnReminder);
        furtherButton_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = editText.getText().toString();
                emoji = EmojiMap.replaceUnicodeEmojis(editEmoji.getText().toString());

                InputMethodManager inputManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(furtherButton_text.getWindowToken(), 0);
                loadPicker();
                Log.d(TAG, name);
            }

        });
        backButton_text = (Button) findViewById(R.id.backBtnReminder);
        backButton_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }

        });

        Intent intent = getIntent();
        if (null != intent) {
            String data[] = intent.getStringArrayExtra("reminder");
            if (data != null) {
                editText.setText(data[0]);
                editEmoji.setText(data[1]);
            }
        }
    }

    private void loadPicker() {
        setContentView(R.layout.activity_reminder_datepicker_time);

        backButton_picker = (Button) findViewById(R.id.backBtnReminder);
        backButton_picker.setOnClickListener(this);

        furtherButton_picker = (Button) findViewById(R.id.furtherBtnReminder);
        furtherButton_picker.setOnClickListener(this);

        btn_date = (ImageButton) findViewById(R.id.btn_date);
        btn_date.setOnClickListener(this);

        btn_time = (ImageButton) findViewById(R.id.btn_time);
        btn_time.setOnClickListener(this);

        editDate = (EditText) findViewById(R.id.in_date);
        editDate.setOnFocusChangeListener(this);

        editTime = (EditText) findViewById(R.id.in_time);
        editTime.setOnFocusChangeListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == btn_time) {
            hidekeyboard(v);

            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

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

        if (v == backButton_picker) {


            setContentView(R.layout.activity_reminder);

            TextView titel = (TextView) findViewById(R.id.headerEditReminder);
            editText = (EditText) findViewById(R.id.titleReminder);
            editEmoji = (EditText) findViewById(R.id.emojiReminder);

            furtherButton_text = (Button) findViewById(R.id.furtherBtnReminder);
            furtherButton_text.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    name = editText.getText().toString();
                    emoji = EmojiMap.replaceUnicodeEmojis(editEmoji.getText().toString());
                    loadPicker();
                    Log.d(TAG, name);
                }

            });
            backButton_text = (Button) findViewById(R.id.backBtnReminder);
            backButton_text.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            editText.setText(name);
            editEmoji.setText(EmojiMap.replaceCheatSheetEmojis(emoji));
        }

        if (v == furtherButton_picker) {
            if (name.length() > 0 && date_time != null) {
                Reminder reminder = new Reminder(emoji, name, date_time);
                Intent intent = new Intent(CreateReminder.this, CreateReminder.class);
                intent.putExtra("emoji", emoji);
                intent.putExtra("name", name);
                intent.putExtra("date_time", date_time);

                Toast.makeText(getApplicationContext(),
                        "Erinnerung wurde gespeichert. Einen Moment bitte..", Toast.LENGTH_LONG).show();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Trage bitte einen Titel und die gewünschte Zeit ein!").setTitle("Info");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (view == editDate) {
            hidekeyboard(view);
            onClick(btn_date);
        }

        if (view == editTime) {
            hidekeyboard(view);
            onClick(btn_time);
        }
    }

    private void hidekeyboard(View v) {
        InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }
}
