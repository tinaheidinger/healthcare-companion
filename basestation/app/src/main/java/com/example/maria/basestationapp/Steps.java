package com.example.maria.basestationapp;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Marie on 20.01.2017.
 */

public class Steps extends AppCompatActivity {
    private Button backButton;
    private Button weekButton;
    private Button monthButton;

    private static final String TAG = "StepsScreen";

    private int[] stepsWeek = new int[7];
    private int[] stepsMonth = new int[31];

    public int dayStepsAmount;

    LineGraphSeries<DataPoint> seriesWeek;
    LineGraphSeries<DataPoint> seriesMonth;

    public String[] monthXAxis= new String[31];

    public GraphView graph;

    public Calendar now = Calendar.getInstance();

    private static ConnectivityManager connMgr;
    private static NetworkInfo networkInfo;
    private static ArrayList<Steps> listSteps = new ArrayList<Steps>();



    protected void onCreate(Bundle savedInstanceState) {
        new Steps.HttpAsyncTaskGET().execute("http://139.59.158.39:8080/steps");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        for (int i=0; i<31; i++) {
            monthXAxis[i] = Integer.toString(i+1);
        }

        graph = (GraphView) findViewById(R.id.graph);
        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setPadding(50);
        seriesWeek = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, stepsWeek[0]),
                new DataPoint(1, stepsWeek[1]),
                new DataPoint(2, stepsWeek[2]),
                new DataPoint(3, stepsWeek[3]),
                new DataPoint(4, stepsWeek[4]),
                new DataPoint(5, stepsWeek[5]),
                new DataPoint(6, stepsWeek[6])
        });
        graph.addSeries(seriesWeek);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        backButton = (Button) findViewById(R.id.backButtonSteps);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        weekButton = (Button) findViewById(R.id.weekSteps);
        weekButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeToWeekView();
            }
        });

        monthButton = (Button) findViewById(R.id.monthSteps);
        monthButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeToMonthView();
            }
        });
    }

    public void checkDayOfWeek(){
        switch(now.get(Calendar.DAY_OF_WEEK)){
            case(Calendar.MONDAY):
                stepsWeek[0] = dayStepsAmount;
                break;
            case(Calendar.TUESDAY):
                stepsWeek[1] = dayStepsAmount;
                break;
            case(Calendar.WEDNESDAY):
                stepsWeek[2] = dayStepsAmount;
                break;
            case(Calendar.THURSDAY):
                stepsWeek[3] = dayStepsAmount;
                break;
            case(Calendar.FRIDAY):
                stepsWeek[4] = dayStepsAmount;
                break;
            case(Calendar.SATURDAY):
                stepsWeek[5] = dayStepsAmount;
                break;
            case(Calendar.SUNDAY):
                stepsWeek[6] = dayStepsAmount;
                break;
            default:
                break;
        }
    }

    public void changeToMonthView(){
        DataPoint[] datapoints = new DataPoint[31];

        graph.removeAllSeries();

        for (int i=0; i<31; i++) {
            datapoints[i] = new DataPoint(i, stepsMonth[i]);
        }
        seriesMonth = new LineGraphSeries<>(datapoints);
        graph.addSeries(seriesMonth);

        StaticLabelsFormatter staticLabelsFormatterMonth = new StaticLabelsFormatter(graph);
        staticLabelsFormatterMonth.setHorizontalLabels(monthXAxis);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatterMonth);
    }

    public void changeToWeekView(){
        graph.removeAllSeries();

        seriesWeek = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, stepsWeek[0]),
                new DataPoint(1, stepsWeek[1]),
                new DataPoint(2, stepsWeek[2]),
                new DataPoint(3, stepsWeek[3]),
                new DataPoint(4, stepsWeek[4]),
                new DataPoint(5, stepsWeek[5]),
                new DataPoint(6, stepsWeek[6])
        });

        graph.addSeries(seriesWeek);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    public void onMonthPressed(){
        changeToMonthView();
    }

    public void onWeekPressed(){
        changeToWeekView();
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public boolean isConnected() {
        connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(TAG, "networktype: " + networkInfo.getTypeName() + " networkInfo: " + networkInfo.getExtraInfo());
            Log.d(TAG, "state: " + networkInfo.getState());

            return true;
        } else {
            return false;
        }
    }

    public static int[] GET_Month(String url) {
        Log.d(TAG, "GET Method started: connected" + networkInfo.toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url + "?companion=5&period=month");
        //JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject();
        //ArrayList<Fluid> result = new ArrayList<Fluid>();
        int[] tempData = new int[31];

        try {
            Log.d(TAG, "before execute:"+jsonObject.length());
            System.setProperty("http.keepAlive", "false");
            HttpResponse response = httpclient.execute(httpget);

            Log.d(TAG, "after execute"+jsonObject.length());
            String server_response = null;
            server_response = EntityUtils.toString(response.getEntity());
            jsonObject = new JSONObject(server_response);

            //Log.d(TAG, "get halp to month");
            if (jsonObject.length() > 0) {
                //Log.d(TAG,"!!!" +jsonObject.toString());
                /*
                String emoji = "";
                String emojimap = "";
                String text = "";
                Goal goal;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    emoji = jsonObject.getString("emoji");
                    text = jsonObject.getString("text");
                    emojimap = EmojiMap.replaceCheatSheetEmojis(emoji);
                    goal = new Goal(emojimap, text);
                    Log.d(TAG, goal.toString());
                    result.add(goal);

                }*/
                //String dates = jsonObject.getString("steps");

                //Log.d(TAG, dates);

                /* TESTDATEN --> HIER ECHTE DATEN EINLESEN! */

                for (int i=0; i<31;i++) {
                    tempData[i] = i*100;
                }
            }
        } catch (IOException e) {
            Log.e(TAG,"IOException GET"+ e.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception " + e.getMessage());
        }
        Log.d(TAG, "Server response..." + tempData);
        return tempData;
    }

    public static int[] GET_Week(String url) {
        Log.d(TAG, "GET_WeeK()");
        Log.d(TAG, "GET Method started: connected" + networkInfo.toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url + "?companion=5&period=week");
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject();
        //ArrayList<Fluid> result = new ArrayList<Fluid>();
        int[] tempData = new int[7];

        try {
            Log.d(TAG, "before execute:"+jsonObject.length());
            System.setProperty("http.keepAlive", "false");
            HttpResponse response = httpclient.execute(httpget);

            Log.d(TAG, "after execute"+jsonObject.length());
            String server_response = null;
            server_response = EntityUtils.toString(response.getEntity());
            jsonObject = new JSONObject(server_response);
            //Log.d(TAG, "send halp to week");

            if (jsonObject.length() > 0) {
                //Log.d(TAG, "welp" +jsonObject.toString());
                /*
                String emoji = "";
                String emojimap = "";
                String text = "";
                Goal goal;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    emoji = jsonObject.getString("emoji");
                    text = jsonObject.getString("text");
                    emojimap = EmojiMap.replaceCheatSheetEmojis(emoji);
                    goal = new Goal(emojimap, text);
                    Log.d(TAG, goal.toString());
                    result.add(goal);

                }*/


                /* TESTDATEN --> HIER ECHTE DATEN EINLESEN! */

                tempData[0] = 900;
                tempData[1] = 99;
                tempData[2] = 199;
                tempData[3] = 299;
                tempData[4] = 399;
                tempData[5] = 499;
                tempData[6] = 799;
            }
        } catch (IOException e) {
            Log.e(TAG,"IOException GET"+ e.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception " + e.getMessage());
        }
        Log.d(TAG, "Server response..." + tempData);
        return tempData;
    }

    private class HttpAsyncTaskGET extends AsyncTask<String, Void, ArrayList<Steps>> {
        @Override
        protected ArrayList<Steps> doInBackground(String... urls) {
            Log.d(TAG, "doIn Background: connected" + isConnected());


            stepsMonth = GET_Month(urls[0]);
            //
            stepsWeek = GET_Week(urls[0]);

            return listSteps;
        }
       /* // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }*/
    }
}

