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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.maria.basestationapp.R.id.loadTextSteps;
import static com.example.maria.basestationapp.R.id.monthSteps;
import static com.example.maria.basestationapp.R.id.weekSteps;

/**
 * Created by Marie on 20.01.2017.
 */

public class Steps extends AppCompatActivity {
    private Button backButton;
    private Button weekButton;
    private Button monthButton;
    private TextView loadingText;

    private static final String TAG = "StepsScreen";

    private int[] stepsWeek = new int[7];

    public static ArrayList<Integer> stepsMonth;
    public int dayStepsAmount;

    private static Integer[] dayAmountArray = new Integer[31];

    LineGraphSeries<DataPoint> seriesWeek;
    LineGraphSeries<DataPoint> seriesMonth;

    public String[] monthXAxis= new String[31];

    public GraphView graph;

    public Calendar now = Calendar.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("dd");
    private Integer today = Integer.parseInt(sdf.format(now.getTime()));;


    private static ConnectivityManager connMgr;
    private static NetworkInfo networkInfo;
    private static ArrayList<Integer> listSteps = new ArrayList<Integer>();



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

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        loadingText = (TextView) findViewById(loadTextSteps);

        backButton = (Button) findViewById(R.id.backButtonSteps);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });

        weekButton = (Button) findViewById(weekSteps);
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

    public void changeToMonthView(){
        DataPoint[] datapoints = new DataPoint[31];

        graph.removeAllSeries();

        for (int i=0; i<31; i++) {
            datapoints[i] = new DataPoint(i, dayAmountArray[i]);
        }
        seriesMonth = new LineGraphSeries<>(datapoints);
        graph.addSeries(seriesMonth);

        StaticLabelsFormatter staticLabelsFormatterMonth = new StaticLabelsFormatter(graph);
        staticLabelsFormatterMonth.setHorizontalLabels(monthXAxis);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatterMonth);
    }

    public void changeToWeekView(){

        DataPoint[] datapoints = new DataPoint[7];
        graph.removeAllSeries();

        if (today < 7) {
            today = 7;
        }
        //Log.d(TAG, "today:" + today.toString());

        String[] labelX = new String[7];
        Integer dataPointIdx=6;
        for (Integer i=today-1; i>=today-7; i--) {
            datapoints[dataPointIdx] = new DataPoint(dataPointIdx, dayAmountArray[i]);
            //Log.d(TAG, "dataPointIdx:" + dataPointIdx.toString() + " i: "+i.toString() + " amount:"+dayAmountArray[i]);
            labelX[dataPointIdx] = Integer.toString((i+1));

            dataPointIdx--;
        }
        seriesWeek = new LineGraphSeries<>(datapoints);
        graph.addSeries(seriesWeek);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(labelX);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }


    //////////SERVERKOMMUNIKATION////////////


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

    public static ArrayList<Integer> GET_Month(String url) {
        Log.d(TAG, "GET Method started: connected" + networkInfo.toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url + "?companion=5&period=month");

        JSONObject jsonObject = new JSONObject();
        ArrayList<Integer> result = new ArrayList<Integer>();

        try {
            Log.d(TAG, "before execute:"+jsonObject.length());
            System.setProperty("http.keepAlive", "false");
            HttpResponse response = httpclient.execute(httpget);

            Log.d(TAG, "after execute"+jsonObject.length());
            String server_response = null;
            server_response = EntityUtils.toString(response.getEntity());

            jsonObject = new JSONObject(server_response);
            /*
            if (jsonObject.length() >0 ) {
                Log.d(TAG, jsonObject.toString());
            } else {
                Log.d(TAG, "Length 0");
            }
            */

            JSONArray jsonArray = jsonObject.getJSONArray("steps");
            if (jsonArray.length() > 0)
            {
                Integer amount;
                String date="";
                Integer day;
                Integer month;
                Integer year;
                JSONObject stepsObject;
                String[] parts;
                //Log.d(TAG, jsonArray.toString());

                for (int i=0; i<dayAmountArray.length; i++) {
                    dayAmountArray[i] = 0;
                }

                for (int i =0; i < jsonArray.length(); i++) {
                    stepsObject = jsonArray.getJSONObject(i);
                    //Log.d(TAG, fluidObject.toString());
                    date = stepsObject.getString("date");
                    //Log.d(TAG, date);
                    parts = date.split("-");
                    year = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]);
                    day = Integer.parseInt(parts[2]);
                    //Log.d(TAG, "year: "+year.toString() + " month: "+month.toString() + " day: "+day.toString());
                    amount = stepsObject.getInt("amount");
                    //Log.d(TAG, amount.toString());
                    dayAmountArray[day-1] = amount;
                }

                /* DEBUG
                Log.d(TAG, "Array fertig: ");
                for (int i=0;i<dayAmountArray.length;i++) {
                    Log.d(TAG, "day: "+(i+1)+" amount: "+dayAmountArray[i].toString());
                }
                */
            }
        } catch (IOException e) {
            Log.e(TAG,"IOException GET"+ e.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception " + e.getMessage());
        }
        Log.d(TAG, "Server response..." + result);
        return result;
    }

    private class HttpAsyncTaskGET extends AsyncTask<String, Void, ArrayList<Integer>> {
        @Override
        protected ArrayList<Integer> doInBackground(String... urls) {
            Log.d(TAG, "doIn Background: connected" + isConnected());


            stepsMonth = GET_Month(urls[0]);

            return listSteps;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<Integer> result) {

            weekButton = (Button) findViewById(weekSteps);
            weekButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    changeToWeekView();
                }
            });

            monthButton = (Button) findViewById(monthSteps);
            monthButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    changeToMonthView();
                }
            });

            loadingText.setVisibility(View.GONE);

            changeToMonthView();
        }
    }
}

