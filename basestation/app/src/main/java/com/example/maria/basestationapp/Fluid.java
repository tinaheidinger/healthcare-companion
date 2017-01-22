package com.example.maria.basestationapp;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

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

import static com.example.maria.basestationapp.R.id.monthFluid;
import static com.example.maria.basestationapp.R.id.weekFluid;

/**
 * Created by Marie on 18.01.2017.
 */

public class Fluid extends AppCompatActivity{
    private static final String TAG = "FluidScreen";

    private Button backButton;
    private Button weekButton;
    private Button monthButton;

    private SeekBar waterAmountSlider;
    private FloatingActionButton addWaterToDay;

    public int waterAmount = 0;
    public int dayWaterAmount = 0;

    private int[] waterWeek = new int[7];
    private int[] waterMonth = new int[31];

    //private ArrayList<Integer> waterWeek = new ArrayList<Integer>();
    //private ArrayList<Integer> waterMonth = new ArrayList<Integer>();


    LineGraphSeries<DataPoint> seriesWeek;
    LineGraphSeries<DataPoint> seriesMonth;

    public String[] monthXAxis= new String[31];

    public GraphView graph;

    public Calendar now = Calendar.getInstance();

    private static ConnectivityManager connMgr;
    private static NetworkInfo networkInfo;
    private static ArrayList<Fluid> listFluid = new ArrayList<Fluid>();


    protected void onCreate(Bundle savedInstanceState) {
        new Fluid.HttpAsyncTaskGET().execute("http://139.59.158.39:8080/fluid");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fluid);

        for (int i=0; i<31; i++) {
            monthXAxis[i] = new String(Integer.toString(i+1));
        }

        graph = (GraphView) findViewById(R.id.graph);
        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setPadding(50);

        seriesWeek = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, waterWeek[0]),
                new DataPoint(1, waterWeek[1]),
                new DataPoint(2, waterWeek[2]),
                new DataPoint(3, waterWeek[3]),
                new DataPoint(4, waterWeek[4]),
                new DataPoint(5, waterWeek[5]),
                new DataPoint(6, waterWeek[6])
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

        waterAmountSlider = (SeekBar) findViewById(R.id.waterSlider);
        waterAmountSlider.setOnSeekBarChangeListener(new waterListener());

        addWaterToDay = (FloatingActionButton) findViewById(R.id.addFluid);
        addWaterToDay.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                onAddWaterPressed();
            }
        });

        weekButton = (Button) findViewById(weekFluid);
        weekButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeToWeekView();
            }
        });

        monthButton = (Button) findViewById(monthFluid);
        monthButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                changeToMonthView();
            }
        });

    }

    private class waterListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar){}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            switch (progress){
                case 0:
                    waterAmount = 0;
                    break;
                case 1:
                    waterAmount = 200;
                    break;
                case 2:
                    waterAmount = 300;
                    break;
                case 3:
                    waterAmount = 500;
                    break;
                case 4:
                    waterAmount = 750;
                    break;
                case 5:
                    waterAmount = 1000;
                    break;
            }

            final TextView waterText = (TextView) findViewById(R.id.addWaterText);
            waterText.setText(Integer.toString(waterAmount) + " " + getString(R.string.waterText));
        }
    }

    public void changeToMonthView(){
        DataPoint[] datapoints = new DataPoint[31];

        graph.removeAllSeries();
/*
        seriesMonth = new LineGraphSeries<>(new DataPoint[] {

                new DataPoint(0, waterWeek[0]),
                new DataPoint(1, waterWeek[1]),
                new DataPoint(2, waterWeek[2]),
                new DataPoint(3, waterWeek[3]),
                new DataPoint(4, waterWeek[4]),
                new DataPoint(5, waterWeek[5]),
                new DataPoint(6, waterWeek[6])
        });
*/
        for (int i=0; i<31; i++) {
            datapoints[i] = new DataPoint(i, waterMonth[i]);
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
                new DataPoint(0, waterWeek[0]),
                new DataPoint(1, waterWeek[1]),
                new DataPoint(2, waterWeek[2]),
                new DataPoint(3, waterWeek[3]),
                new DataPoint(4, waterWeek[4]),
                new DataPoint(5, waterWeek[5]),
                new DataPoint(6, waterWeek[6])
        });

        graph.addSeries(seriesWeek);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

    }

    public void checkDayOfWeek(){
        switch(now.get(Calendar.DAY_OF_WEEK)){
            case(Calendar.MONDAY):
                waterWeek[0] += waterAmount;
                break;
            case(Calendar.TUESDAY):
                waterWeek[1] += waterAmount;
                break;
            case(Calendar.WEDNESDAY):
                waterWeek[2] += waterAmount;
                break;
            case(Calendar.THURSDAY):
                waterWeek[3] += waterAmount;
                break;
            case(Calendar.FRIDAY):
                waterWeek[4] += waterAmount;
                break;
            case(Calendar.SATURDAY):
                waterWeek[5] += waterAmount;
                break;
            case(Calendar.SUNDAY):
                waterWeek[6] += waterAmount;
                break;
            default:
                break;
        }
    }

    public void onBackPressed(){
        super.onBackPressed();
        this.finish();
    }

    public void onMonthPressed(){
        changeToMonthView();
    }

    public void onWeekPressed(){
        changeToWeekView();
    }

    public void onAddWaterPressed(){
        //dayWaterAmount += waterAmount;
        checkDayOfWeek();
        seriesWeek.resetData(new DataPoint[] {
                new DataPoint(0, waterWeek[0]),
                new DataPoint(1, waterWeek[1]),
                new DataPoint(2, waterWeek[2]),
                new DataPoint(3, waterWeek[3]),
                new DataPoint(4, waterWeek[4]),
                new DataPoint(5, waterWeek[5]),
                new DataPoint(6, waterWeek[6])
        });
    }


    ///test serverstuff

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

            if (jsonObject.length() > 0) {
                Log.d(TAG, jsonObject.toString());
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

            if (jsonObject.length() > 0) {
                Log.d(TAG, jsonObject.toString());
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


                //tempData[0] = Sonntagsdaten;
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

    private class HttpAsyncTaskGET extends AsyncTask<String, Void, ArrayList<Fluid>> {
        @Override
        protected ArrayList<Fluid> doInBackground(String... urls) {
            Log.d(TAG, "doIn Background: connected" + isConnected());


            waterMonth = GET_Month(urls[0]);
            //
            waterWeek = GET_Week(urls[0]);


            //Log.d(TAG, listFluid.toString());
            return listFluid;
        }
       /* // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }*/
    }
}

