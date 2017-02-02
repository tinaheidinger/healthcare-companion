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
import org.apache.http.client.methods.HttpPost;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.maria.basestationapp.R.id.addWaterText;
import static com.example.maria.basestationapp.R.id.loadText;
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
    public TextView waterText;
    public TextView loadingText;
    private SeekBar waterAmountSlider;
    private FloatingActionButton addWaterToDay;

    public int waterAmount = 0;
    ArrayList<Integer> waterMonth;

    public boolean inWeekView;

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
    //public Integer today

    SimpleDateFormat dateToday = new SimpleDateFormat("yyyy-MM-dd");
    //public String strToday = dateToday.format(now.getTime());
    private static String stringToday;
    private static int today;

    private static void setToday(int newValue) {
        today = newValue;
    }

    private static int getToday () {
        return today;
    }

    Integer[] dateToPost = new Integer[3];

    private static void setStringToday(String newStringToday) {
        stringToday = newStringToday;
    }

    private static String getStrToday () {
        return stringToday;
    }



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

        loadingText = (TextView) findViewById(loadText);

        waterText = (TextView) findViewById(addWaterText);
        waterText.setText(Integer.toString(0) + " " + getString(R.string.waterText));

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

        dateToPost[0] = now.YEAR;
        dateToPost[1] = now.MONTH;
        dateToPost[2] = now.DAY_OF_MONTH;

        setStringToday(dateToday.format(now.getTime()));
        setToday(Integer.parseInt(sdf.format(now.getTime())));
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

            waterText.setText(Integer.toString(waterAmount) + " " + getString(R.string.waterText));
        }
    }

    public void changeToMonthView(){
        inWeekView = false;

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
        inWeekView = true;

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

    public void onBackPressed(){
        super.onBackPressed();
        this.finish();
    }

    public void onAddWaterPressed(){
        dayAmountArray[today-1] += waterAmount;

        ///////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////   !!!! HIER BITTE DIE RICHTIGE URL HINEINSCHREIBEN !!!! /////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////

        new Fluid.HttpAsyncTaskPOST().execute("http://139.59.158.39:8080/fluidtest");

        if(inWeekView){
            changeToWeekView();
        } else {
            changeToMonthView();
        }
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

    public static String POST(String url){
        Log.d(TAG,"Post Method started");

        InputStream inputStream = null;
        String result = "";

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            JSONObject mainObject = new JSONObject();
            JSONObject jsonObjectToArray = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            jsonObjectToArray.accumulate("amount", dayAmountArray[getToday()-1]);
            jsonObjectToArray.accumulate("date", getStrToday());

            jsonArray.put(jsonObjectToArray);

            mainObject.put("fluid", jsonArray);
            mainObject.put("companion", 5);

            json = mainObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);
            httpPost.setHeader("Content-type", "application/json");

            Log.d(TAG,"preparing HttpPost with following content..");
            Log.d(TAG,json);

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

                //Log.d(TAG, jsonArray.toString());

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

            return listFluid;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<Integer> result) {

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

            loadingText.setVisibility(View.GONE);

            changeToMonthView();
        }




    }
    private class HttpAsyncTaskPOST extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }
    }
}

