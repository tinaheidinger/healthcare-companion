package com.example.maria.basestationapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateReminder extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "CreateReminder.class";
    private static String name = "";
    private static String emoji = "";
    private static int[] date_time;
    private static String dayOfReminder="";
    private static String date="";

    private EditText editText, editEmoji, editDate, editTime;
    private Button furtherButton_text, furtherButton_picker;
    private Button save;
    private Button backButton_text, backButton_picker;
    private ImageButton btn_date, btn_time;

    private DatePickerDialog datePicker;
    private TimePicker timePicker;

    private int year, month, day, hour, minute;

    private RadioGroup radioGroup;
    private RadioButton radioDay;

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

            date_time = intent.getIntArrayExtra("dateTime");
            if(date_time != null){
                year = date_time[0];
                month = date_time[1];
                day = date_time[2];
                hour = date_time[3];
                minute = date_time[4];
            }

            date = intent.getStringExtra("date");
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

        if(date!=null){
            editDate.setText(date);
        }

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        dayOfReminder = getIntent().getStringExtra("day");
        if(dayOfReminder!=null){
            switch (dayOfReminder){
                case "Montag":
                    radioDay = (RadioButton) findViewById(R.id.mo);
                    radioDay.setChecked(true);
                    break;
                case "Dienstag":
                    radioDay = (RadioButton) findViewById(R.id.di);
                    radioDay.setChecked(true);
                    break;
                case "Mittwoch":
                    radioDay = (RadioButton) findViewById(R.id.mi);
                    radioDay.setChecked(true);
                    break;
                case "Donnerstag":
                    radioDay = (RadioButton) findViewById(R.id.don);
                    radioDay.setChecked(true);
                    break;
                case "Freitag":
                    radioDay = (RadioButton) findViewById(R.id.fr);
                    radioDay.setChecked(true);
                    break;
                case "Samstag":
                    radioDay = (RadioButton) findViewById(R.id.sa);
                    radioDay.setChecked(true);
                    break;
                case "Sonntag":
                    radioDay = (RadioButton) findViewById(R.id.so);
                    radioDay.setChecked(true);
                    break;
                case "nicht wiederholen":
                    radioDay = (RadioButton) findViewById(R.id.norepeat);
                    radioDay.setChecked(true);
                    break;
            }
        }else {
            radioDay = (RadioButton) findViewById(R.id.norepeat);
            radioDay.setChecked(true);
        }

    }

    @Override
    public void onClick(View v) {

        if (v==btn_date) {
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
                            if(date_time==null){
                                date_time = new int[5];
                            }
                            date_time[0] = year;
                            date_time[1] = monthOfYear+1;
                            date_time[2] = dayOfMonth;
                        }
                    }, year, month, day);
            datePicker.show();

        }else if (v == btn_time) {
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
                            if(date_time==null){
                                date_time = new int[5];
                            }
                            date_time[3] = hourOfDay;
                            date_time[4] = minute;
                        }
                    }, hour, minute, false);
            timePickerDialog.show();

        }else if (v == backButton_picker) {

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

        }else if (v == furtherButton_picker) {
            if (name.length() > 0 && editDate.getText().length() > 0) {

                radioDay = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                dayOfReminder= radioDay.getText().toString();

                Log.d(TAG, "gewählter radioButton: "+dayOfReminder);

                Intent intent = new Intent(CreateReminder.this, CreateReminder.class);
                intent.putExtra("emoji", emoji);
                intent.putExtra("name", name);
                intent.putExtra("date", editDate.getText());
                intent.putExtra("weekday", dayOfReminder);

                new CreateReminder.HttpAsyncTaskPOST().execute("http://139.59.158.39:8080/reminder");

                Toast.makeText(getApplicationContext(),
                        "Erinnerung wird gespeichert. Einen Moment bitte..", Toast.LENGTH_LONG).show();

                Intent i = new Intent(v.getContext(), ListReminders.class);
                startActivityForResult(i, 0);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Trag bitte einen Titel und das gewünschte Datum ein!").setTitle("Info");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private class HttpAsyncTaskPOST extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Neues Ziel gespeichert. Wird in einigen Augenblicken in die Liste übernommen. ", Toast.LENGTH_LONG).show();
        }
    }

    public static String POST(String url){
        Log.d(TAG,"Post Method started");

        InputStream inputStream = null;
        String result = "";

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("companion", 3);
            jsonObject.accumulate("emoji", emoji);
            jsonObject.accumulate("text", name);
            if(date_time!=null) {
                jsonObject.accumulate("date", date_time[0] + "-" + date_time[1] + "-" + date_time[2]);
            }else{
                jsonObject.accumulate("date", date);
            }
            jsonObject.accumulate("weekday", dayOfReminder);

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);
            httpPost.setHeader("Content-type", "application/json");

            Log.d(TAG,"preparing HttpPost with following content..");
            Log.d(TAG,json.toString());

            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.d(TAG,"HttpPost ok");

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.d(TAG, "answer of server... \n"+result);
            }
            else {
                result = "Did not work!";
            }

        } catch (JSONException e) {
            Log.d(TAG,"JSON Exception");
        } catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        Log.d(TAG,"prepare bufferedreader");
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        Log.d(TAG,"BR ok");

        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (view == editDate && b) {
            hidekeyboard(view);
            onClick(btn_date);
        }else  if (view == editTime && b) {
            hidekeyboard(view);
            onClick(btn_time);
        }
    }

    private void hidekeyboard(View v) {
        InputMethodManager inputManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    /**
     * Hilfsmethode um String zu Date umzuwandeln
     * */
    private Date parseDate(int year, int month, int dayOfMonth){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String datetoString = Integer.toString(year)+"-0"+Integer.toString(month+1)+"-"+Integer.toString(dayOfMonth);
        Date date=new Date();
        try {
            date = formatter.parse(datetoString);
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
        }

        return date;
    }
}
