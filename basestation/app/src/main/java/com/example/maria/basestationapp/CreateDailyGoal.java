package com.example.maria.basestationapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
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

import com.stacktips.view.CustomCalendarView;
import com.stacktips.view.DayDecorator;
import com.stacktips.view.DayView;
import com.stacktips.view.utils.CalendarUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    private CustomCalendarView calender;
    private ArrayList<Date> dates;
    private ListView listviewDates;
    private StableArrayAdapter adapter;

    private static Goal post;

    private ArrayList<Date> reachedGoals;
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
    }

    /**
     * laedt die Ansicht, in der der Kalender dargestellt wird
     * so koennen Termine ausgewaehlt werden, wann Tagesziel erreicht werden soll
     * */
    protected void loadCalender(){
        setContentView(R.layout.activity_entering_daily_goal_datepicker);
        //new HttpAsyncTaskPOST().execute("http://139.59.158.39:8080/goal");

        //new CreateDailyGoal.HttpAsyncTaskGET().execute("http://139.59.158.39:8080/");

        reachedGoals = new ArrayList<Date>();

        reachedGoals.add(parseDate(2016,11,6));
        reachedGoals.add(parseDate(2016,11,7));
        reachedGoals.add(parseDate(2016,11,8));
        reachedGoals.add(parseDate(2016,11,10));
        reachedGoals.add(parseDate(2016,11,11));

        reachedGoals.add(parseDate(2016,11,13));
        reachedGoals.add(parseDate(2016,11,14));
        reachedGoals.add(parseDate(2016,11,15));
        reachedGoals.add(parseDate(2016,11,16));
        reachedGoals.add(parseDate(2016,11,17));

        reachedGoals.add(parseDate(2017,0,3));
        reachedGoals.add(parseDate(2017,0,5));
        reachedGoals.add(parseDate(2017,0,7));
        reachedGoals.add(parseDate(2017,0,8));
        reachedGoals.add(parseDate(2017,0,10));

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

                        post = new Goal(emoji, name);
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
                //name = editText.getText().toString();
                emoji = EmojiMap.replaceUnicodeEmojis(editEmoji.getText().toString());

                post = new Goal(emoji, name);
                Log.d(TAG, "post: "+name);

                new HttpAsyncTaskPOST().execute("http://139.59.158.39:8080/goal");

                Intent intent = new Intent(CreateDailyGoal.this, DailyGoals.class);
                startActivity(intent);
            }

        });

        calender = (CustomCalendarView) findViewById(R.id.calendarView);
        calender.setFirstDayOfWeek(0);
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());


        List<DayDecorator> decorators = new ArrayList<>();
        decorators.add(new ColorDecorator());
        calender.setDecorators(decorators);
        calender.refreshCalendar(currentCalendar);
        /*calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
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
        });*/
        Intent intent = getIntent();
        if (null != intent) {
            String data[]= intent.getStringArrayExtra("goal");
            if(data!=null) {
                editText.setText(data[2]);
                //updateListView();
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
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String datetoString = Integer.toString(dayOfMonth)+"-0"+Integer.toString(month+1)+"-"+Integer.toString(year);
        Date date=new Date();
        try {
            date = formatter.parse(datetoString);
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
        }

        return date;
    }

    /*
    *Hilfsmethode um Date zu String umzuwandeln
    * */
    private String parseDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
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
            Toast.makeText(getBaseContext(), "Neues Ziel gespeichert. \n Sollte es noch nicht angezeigt werden, hinunter scrollen.  ", Toast.LENGTH_LONG).show();
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
        Log.d(TAG, post.emoji+" "+post.name);

        InputStream inputStream = null;
        String result = "";

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("companion", 4);
            jsonObject.accumulate("emoji", post.emoji);
            jsonObject.accumulate("text", post.name);

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

    /**
     * GET request to server, calls get method
     * (has to be an AsyncTask)
     */
    private class HttpAsyncTaskGET extends AsyncTask<String, Void, ArrayList<Date>> {
        @Override
        protected ArrayList<Date> doInBackground(String... urls) {
            reachedGoals = GET(urls[0]);

            Log.d(TAG, reachedGoals.toString());
            return reachedGoals;
        }
       /* // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }*/
    }

    /*
    * fordert Tage an an denen Tagesziel erreicht wurde
    * */
    public static ArrayList<Date> GET(String url) {
        Log.d(TAG, "GET Method started");

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url + "?goal=1");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        ArrayList<Date> result = new ArrayList<Date>();

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date date;

        try {
            Log.d(TAG, "before execute:"+jsonArray.length());
            System.setProperty("http.keepAlive", "false");
            HttpResponse response = httpclient.execute(httpget);

            Log.d(TAG, "after execute"+jsonArray.length());
            String server_response = null;
            server_response = EntityUtils.toString(response.getEntity());
            jsonArray = new JSONArray(server_response);


            if (jsonArray.length() > 0) {
                String jsonDate="";

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    jsonDate = jsonObject.getString("date");
                    date = format.parse(jsonDate);
                    Log.d(TAG, date.toString());
                    result.add(date);
                }
            }

        } catch (IOException e) {
            Log.e(TAG,"IOException GET"+ e.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception " + e.toString());
        } catch (ParseException e){
            Log.e(TAG,"ParseException GET method"+ e.toString());
        }


        Log.d(TAG, "Server response..." + result);
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
    private class ColorDecorator implements DayDecorator {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        @Override
        public void decorate(DayView dayView) {
            for(int i=0; i<reachedGoals.size(); i++) {
                //Log.d(TAG, reachedGoals.get(i).toString());
                if (formatter.format(dayView.getDate()).equals(formatter.format(reachedGoals.get(i)))){
                    //Log.d(TAG,"equals");
                    int color = Color.parseColor("#98FB98");
                    dayView.setBackgroundColor(color);

                }
            }
        }
    }

}
