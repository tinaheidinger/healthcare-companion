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
import java.text.SimpleDateFormat;
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
    ArrayList<Integer> waterMonth;

    LineGraphSeries<DataPoint> seriesWeek;
    LineGraphSeries<DataPoint> seriesMonth;

    public String[] monthXAxis= new String[31];

    public GraphView graph;

    public Calendar now = Calendar.getInstance();

    private static ConnectivityManager connMgr;
    private static NetworkInfo networkInfo;
    private static ArrayList<Integer> listFluid = new ArrayList<Integer>();

    private static Integer[] dayAmountArray = new Integer[31];

    SimpleDateFormat sdf = new SimpleDateFormat("dd");
    private Integer today = Integer.parseInt(sdf.format(now.getTime()));;

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

        changeToMonthView();

        /*
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        */

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

        Log.d(TAG, "today:" + today.toString());

        String[] labelX = new String[7];
        Integer dataPointIdx=6;
        for (Integer i=today-1; i>=today-7; i--) {
            datapoints[dataPointIdx] = new DataPoint(dataPointIdx, dayAmountArray[i]);
            Log.d(TAG, "dataPointIdx:" + dataPointIdx.toString() + " i: "+i.toString() + " amount:"+dayAmountArray[i]);
            labelX[dataPointIdx] = Integer.toString((i+1));

            dataPointIdx--;
        }
        seriesWeek = new LineGraphSeries<>(datapoints);
        graph.addSeries(seriesWeek);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        //staticLabelsFormatter.setHorizontalLabels(new String[]{"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"});
        staticLabelsFormatter.setHorizontalLabels(labelX);
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);


    }

    public void onBackPressed(){
        super.onBackPressed();
        this.finish();
    }

    public void onAddWaterPressed(){
        dayAmountArray[today-1] += waterAmount;
        changeToWeekView();
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
            if (jsonObject.length() >0 ) {
                Log.d(TAG, jsonObject.toString());
            } else {
                Log.d(TAG, "Length 0");
            }

            JSONArray jsonArray = jsonObject.getJSONArray("fluid");
            if (jsonArray.length() > 0)
            {
                Integer amount;
                String date="";
                Integer day;
                Integer month;
                Integer year;
                JSONObject fluidObject;
                String[] parts;

                //dataPoints = new DataPoint[31];
                Log.d(TAG, jsonArray.toString());

                for (int i=0; i<dayAmountArray.length; i++) {
                    dayAmountArray[i] = 0;
                }

                for (int i =0; i < jsonArray.length(); i++) {
                    fluidObject = jsonArray.getJSONObject(i);
                    //Log.d(TAG, fluidObject.toString());
                    date = fluidObject.getString("date");
                    //Log.d(TAG, date);
                    parts = date.split("-");
                    year = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]);
                    day = Integer.parseInt(parts[2]);
                    //Log.d(TAG, "year: "+year.toString() + " month: "+month.toString() + " day: "+day.toString());
                    amount = fluidObject.getInt("amount");
                    //Log.d(TAG, amount.toString());

                    // daten einfüllen
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


            waterMonth = GET_Month(urls[0]);
            //
            //waterWeek = GET_Week(urls[0]);


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

