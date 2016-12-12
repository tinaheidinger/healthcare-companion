package com.example.maria.basestationapp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/*
* draft of daily goals, setting up connection by entering view
*
* goals can be added via plus button
* post request is executed
* */

public class DailyGoals extends Activity {
    private static final String TAG = "DailyGoals";

    private static ConnectivityManager connMgr;
    private static NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);

        final ListView listview = (ListView) findViewById(R.id.goalslistView);
        String[] values = new String[] { "Obst essen", "Sport machen" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
            }

        });

        FloatingActionButton myFab = (FloatingActionButton) this.findViewById(R.id.addGoal);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new HttpAsyncTask().execute("http://localhost:8080");

            }
        });

        /*
        * Network setup
        * */
        if(isConnected()){
            Log.d(TAG,"connected: "+isConnected());
        }else{
            Log.d(TAG,"not connected");
        }

    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            Log.d(TAG,"doIn Background: connected"+isConnected());

            return POST(urls[0]);
        }
       /* // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }*/
    }

    public boolean isConnected(){
        connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d(TAG, networkInfo.getTypeName());
            Log.d(TAG, networkInfo.getExtraInfo());
            Log.d(TAG, networkInfo.getType()+"");
            Log.d(TAG, networkInfo.getState()+"");

            return true;
        }
        else {
            return false;
        }
    }

    public static String POST(String url){
        Log.d(TAG,"Post gestartet: connected"+networkInfo.toString());

        InputStream inputStream = null;
        String result = "";

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("companion", 1);
            jsonObject.accumulate("emoji", "U+1F603");
            jsonObject.accumulate("text", "test abc");

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            Log.d(TAG,"preparing HttpPost");
            Log.d(TAG,json.toString());

            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.d(TAG,"HttpPost ok");

            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

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

}

