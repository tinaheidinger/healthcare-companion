package com.example.maria.basestationapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;
/*
* Tagesziele werden angezeigt
* bei laden der View vom Server abgefragt
*
* Ziele ueber plus-button hizugefuegt werden
* */

public class DailyGoals extends Activity {
    private static final String TAG = "DailyGoals";

    private static ConnectivityManager connMgr;
    private static NetworkInfo networkInfo;
    private static ArrayList<Goal> listGoals = new ArrayList<Goal>();

    private Button menu;

    /* method starts when activity is called
    * views and data is requested and loaded to display
     *  */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //new DailyGoals.HttpAsyncTaskPOST().execute("http://139.59.158.39:8080/goal");
        new DailyGoals.HttpAsyncTaskGET().execute("http://139.59.158.39:8080/goals");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);

        //new DailyGoals.HttpAsyncTaskPOST().execute("http://139.59.158.39:8080/goal");

        /*
        * Network check, for debug purpose
        * */
        if (isConnected()) {
            Log.d(TAG, "connected: " + isConnected());
        } else {
            Log.d(TAG, "not connected");
        }
        try {
            Thread.sleep(1000);
            refresh();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        menu = (Button) findViewById(R.id.backButtonGoal);
        menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(DailyGoals.this, MainActivity.class);
                startActivity(intent);
            }

        });
    }

    protected void refresh() {
        Log.d(TAG, "refresh ... " + listGoals.toString());
        new DailyGoals.HttpAsyncTaskGET().execute("http://139.59.158.39:8080/goals");
        final ListView listview = (ListView) findViewById(R.id.goalslistView);

        final StableArrayAdapter adapter = new StableArrayAdapter(this, listGoals);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Goal goal = (Goal) parent.getItemAtPosition(position);
                String temp[] = new String[]{goal.emoji, goal.name, goal.emoji};
                Intent intent = new Intent(DailyGoals.this, CreateDailyGoal.class);
                intent.putExtra("goal", temp);

                startActivity(intent);

            }

        });

        FloatingActionButton myFab = (FloatingActionButton) this.findViewById(R.id.addGoal);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, 0);
                Intent intent = new Intent(DailyGoals.this, CreateDailyGoal.class);
                startActivity(intent);
            }
        });

    }

    /*
    * entries of the List
    * */
    private class StableArrayAdapter extends ArrayAdapter<Goal> {

        public StableArrayAdapter(Context context, ArrayList<Goal> goals) {
            super(context, 0, goals);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Goal goal = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_layout, parent, false);
            }
            // Lookup view for data population
            TextView emoji = (TextView) convertView.findViewById(R.id.emoji);
            TextView text = (TextView) convertView.findViewById(R.id.name);
            // Populate the data into the template view using the data object
            emoji.setText(goal.emoji);
            text.setText(goal.name);
            // Return the completed view to render on screen
            return convertView;
        }


        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    /**
     * POST request to server calls post method
     * (has to be an AsyncTask)
     */
    private class HttpAsyncTaskPOST extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            Log.d(TAG, "doIn Background: connected" + isConnected());

            return POST(urls[0]);
        }
       /* // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }*/
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

    /**
     * Post method
     * creates a JSON object with the entered daily goal data id, emoji, text
     * and send it to the server
     */
    public static String POST(String url) {
        Log.d(TAG, "Post Method started: connected" + networkInfo.toString());

        InputStream inputStream = null;
        String result = "";

        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("companion", 1);
            jsonObject.accumulate("emoji", "0x1F34E");
            jsonObject.accumulate("text", "jeden Tag Obst essen");

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httpPost.setEntity(se);
            httpPost.setHeader("Content-type", "application/json");

            Log.d(TAG, "preparing HttpPost with following content..");
            Log.d(TAG, json.toString());

            HttpResponse httpResponse = httpclient.execute(httpPost);
            Log.d(TAG, "HttpPost ok");

            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.d(TAG, "answer of server... \n" + result);
            } else {
                result = "Did not work!";
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSON Exception");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        Log.d(TAG, "prepare bufferedreader");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Log.d(TAG, "BR ok");

        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    /**
     * GET request to server, calls get method
     * (has to be an AsyncTask)
     */
    private class HttpAsyncTaskGET extends AsyncTask<String, Void, ArrayList<Goal>> {
        @Override
        protected ArrayList<Goal> doInBackground(String... urls) {
            Log.d(TAG, "doIn Background: connected" + isConnected());
            listGoals = GET(urls[0]);

            Log.d(TAG, listGoals.toString());
            return listGoals;
        }
       /* // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }*/
    }

    /**
     * requests all daily goals of the specific companion
     */
    public static ArrayList<Goal> GET(String url) {
        Log.d(TAG, "GET Method started: connected" + networkInfo.toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url + "?companion=3");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        ArrayList<Goal> result = new ArrayList<Goal>();

        try {
            Log.d(TAG, "before execute:"+jsonArray.length());
            System.setProperty("http.keepAlive", "false");
            HttpResponse response = httpclient.execute(httpget);

            Log.d(TAG, "after execute"+jsonArray.length());
            String server_response = null;
            server_response = EntityUtils.toString(response.getEntity());
            jsonArray = new JSONArray(server_response);


            if (jsonArray.length() > 0) {
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
                }
            }

        } catch (IOException e) {
            Log.e(TAG,"IOException GET"+ e.toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception " + e.getMessage());
        }


        Log.d(TAG, "Server response..." + result);
        return result;
    }


}

