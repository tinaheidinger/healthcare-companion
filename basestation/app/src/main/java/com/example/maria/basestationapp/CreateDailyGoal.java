package com.example.maria.basestationapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Date;

/**
 * Klasse um ein neues Tagesziel anzulegen und zu speichern
 * */
public class CreateDailyGoal extends AppCompatActivity {
    private static final String TAG = "CreateDailyGoal.class";
    private static String name = "";
    private static String emoji = "";

    private EditText editText;
    private EditText editEmoji;

    private Button furtherButton;
    private Button save;
    private Button backButton;
    private Button enterBackButton;
    private boolean backcheck=false;

    private CalendarView calender;
    private ArrayList<Date> dates;
    private ListView listviewDates;
    private StableArrayAdapter adapter;

    private static Goal post;

    /**
     * wird beim ersten Starten aufgerufen und initialisiert die Elemente der View
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entering_daily_goal);

        TextView titel = (TextView)findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.titleGoal);
        editEmoji = (EditText) findViewById(R.id.emojiGoal);

        furtherButton = (Button) findViewById(R.id.furtherButton);
        furtherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = editText.getText().toString();
                emoji = EmojiMap.replaceUnicodeEmojis(editEmoji.getText().toString());

                InputMethodManager inputManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(furtherButton.getWindowToken(),0);
                backcheck=true;
                loadCalender();
                Log.d(TAG,name);
            }

        });
        backButton = (Button) findViewById(R.id.backButtonGoal);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }

        });
        dates = new ArrayList<Date>();
        adapter = new StableArrayAdapter(this,dates);

        Intent intent = getIntent();
        if (null != intent) {
            String data[]= intent.getStringArrayExtra("goal");
            if(data!=null) {
                editEmoji.setText(data[0]);
                editText.setText(data[1]);
            }
        }
        /*int unicode = 0x1F604;
        String emoji = new String(Character.toChars(unicode));

        titel.setText(emoji);*/

    }

    /**
     * laedt die Ansicht, in der der Kalender dargestellt wird
     * so koennen Termine ausgewaehlt werden, wann Tagesziel erreicht werden soll
     * */
    protected void loadCalender(){
        setContentView(R.layout.activity_entering_daily_goal_datepicker);
        //new HttpAsyncTaskPOST().execute("http://139.59.158.39:8080/goal");
        listviewDates = (ListView) findViewById(R.id.listDates);
        backButton =(Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.activity_entering_daily_goal);

                TextView titel = (TextView)findViewById(R.id.textView);
                editText = (EditText) findViewById(R.id.titleGoal);
                editEmoji = (EditText) findViewById(R.id.emojiGoal);

                furtherButton = (Button) findViewById(R.id.furtherButton);
                furtherButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        name = editText.getText().toString();
                        emoji = EmojiMap.replaceUnicodeEmojis(editEmoji.getText().toString());

                        post = new Goal(emoji, name,"");
                        Log.d(TAG,"Goal"+editEmoji+" "+name);
                        //new HttpAsyncTaskPOST().execute("http://139.59.158.39:8080/goal");
                    }

                });
                enterBackButton =(Button) findViewById(R.id.backButtonGoal);
                enterBackButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                editText.setText(name);
                editEmoji.setText(EmojiMap.replaceCheatSheetEmojis(emoji));
            }
        });

        furtherButton = (Button) findViewById(R.id.furtherButton);
        furtherButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = editText.getText().toString();
                emoji = EmojiMap.replaceUnicodeEmojis(editEmoji.getText().toString());

                post = new Goal(emoji, name,"");
                new HttpAsyncTaskPOST().execute("http://139.59.158.39:8080/goal");

                Intent intent = new Intent(CreateDailyGoal.this, DailyGoals.class);
                startActivity(intent);
            }

        });

        calender = (CalendarView) findViewById(R.id.calendarView);
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            Date temp;
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // display the selected date by using a toast
                temp = parseDate(year, month, dayOfMonth);
                if(!dates.contains(temp)){
                    dates.add(temp);
                    updateListView();
                }
                else{
                    dates.remove(temp);
                }
            }
        });
        Intent intent = getIntent();
        if (null != intent) {
            String data[]= intent.getStringArrayExtra("goal");
            if(data!=null) {
                editText.setText(data[2]);
                updateListView();
            }
        }

    }

    /**
     * um ListView mit Daten zu aktualisieren
     * */
    private void updateListView(){
        listviewDates.setAdapter(adapter);
        listviewDates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Date item = (Date) parent.getItemAtPosition(position);

                dates.remove(item);
                updateListView();
            }

        });
    }

    /**
     * Hilfsmethode um Date zu String umzuwandeln
     * */
    private Date parseDate(int year, int month, int dayOfMonth){
        SimpleDateFormat formatter = new SimpleDateFormat("d-MM-yyyy");
        String datetoString = Integer.toString(dayOfMonth)+"-0"+Integer.toString(month+1)+"-"+Integer.toString(year);
        Date date=new Date();
        try {

            date = formatter.parse(datetoString);
            System.out.println(date);
            System.out.println(formatter.format(date));

        } catch (ParseException e) {
            Log.e(TAG, e.toString());
        }

        return date;
    }

    /*
    *Hilfsmethode um Date zu String umzuwandeln
    * */
    private String parseDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("d-MM-yyyy");
        String datetoString = formatter.format(date);
        return datetoString;
    }

    /**
     * POST request to server calls post method
     * (has to be an AsyncTask)
     * */
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

    /**
     * Post method
     * creates a JSON object with the entered daily goal data id, emoji, text
     * and send it to the server
     *
     * */
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
            jsonObject.accumulate("emoji", post.emoji);
            jsonObject.accumulate("text", post.name);
            jsonObject.accumulate("date",post.day);

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

    private class StableArrayAdapter extends ArrayAdapter<Date> {

        public StableArrayAdapter(Context context, ArrayList<Date> goals) {
            super(context, 0, goals);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Date date = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_calendar_layout, parent, false);
            }
            // Lookup view for data population
            TextView pickedDates = (TextView) convertView.findViewById(R.id.pickedDates);
            pickedDates.setText(parseDate(date));
            return convertView;
        }


        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    /**
     * Hilfsmethode um erhaltenen Stream lesbar zu machen
     * */
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
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


}
