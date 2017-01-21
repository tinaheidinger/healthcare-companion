package com.example.maria.basestationapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListReminders extends AppCompatActivity {
    private static final String TAG = "ListReminders";

    private static ConnectivityManager connMgr;
    private static NetworkInfo networkInfo;

    private static ArrayList<Reminder> listReminders = new ArrayList<Reminder>();

    private Button menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ListReminders.HttpAsyncTaskGET().execute("http://139.59.158.39:8080/reminders");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_reminders);

        //listReminders.add(new Reminder(EmojiMap.replaceCheatSheetEmojis(":pill:"), "Blutdruck Pillen"));

        menu = (Button) findViewById(R.id.menuReminder);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListReminders.this, MainActivity.class);
                startActivity(intent);
            }
        });
        try {
            Thread.sleep(1000);
            loadEntries();
        } catch (InterruptedException e) {
            Log.e(TAG,e.toString());
            Intent intent = new Intent(ListReminders.this, MainActivity.class);
            startActivity(intent);
        }
    }

    protected void loadEntries() {
        Log.d(TAG, "load ... " + listReminders.toString());
        new ListReminders.HttpAsyncTaskGET().execute("http://139.59.158.39:8080/reminders");
        final ListView listview = (ListView) findViewById(R.id.reminderlistView);

        final StableArrayAdapter adapter = new StableArrayAdapter(this, listReminders);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Reminder r = (Reminder) parent.getItemAtPosition(position);
                String temp[] = new String[]{r.name, r.emoji};
                Intent intent = new Intent(ListReminders.this, CreateReminder.class);
                intent.putExtra("reminder", temp);
                intent.putExtra("reminder_date", r.date_time);

                startActivity(intent);

            }

        });

        FloatingActionButton myFab = (FloatingActionButton) this.findViewById(R.id.addReminder);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, 0);
                Intent intent = new Intent(ListReminders.this, CreateReminder.class);
                startActivity(intent);
            }
        });

    }


    /**
     * GET request to server, calls get method
     * (has to be an AsyncTask)
     */
    private class HttpAsyncTaskGET extends AsyncTask<String, Void, ArrayList<Reminder>> {
        @Override
        protected ArrayList<Reminder> doInBackground(String... urls) {
            Log.d(TAG, "doIn Background: connected" + isConnected());
            listReminders = GET(urls[0]);

            Log.d(TAG, listReminders.toString());
            return listReminders;
        }
       /* // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }*/
    }

    /**
     * requests all reminders of the specific companion
     */
    public static ArrayList<Reminder> GET(String url) {
        Log.d(TAG, "GET Method started: connected" + networkInfo.toString());

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url + "?companion=3");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        ArrayList<Reminder> result = new ArrayList<Reminder>();

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
                Reminder reminder;
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    emoji = jsonObject.getString("emoji");
                    text = jsonObject.getString("text");
                    emojimap = EmojiMap.replaceCheatSheetEmojis(emoji);
                    reminder = new Reminder(emojimap, text);
                    Log.d(TAG, reminder.toString());
                    result.add(reminder);
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

    public void addReminder(Reminder r){
        listReminders.add(r);
    }

    private class StableArrayAdapter extends ArrayAdapter<Reminder> {

        public StableArrayAdapter(Context context, ArrayList<Reminder> r) {
            super(context, 0, r);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Reminder reminder = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_layout, parent, false);
            }
            // Lookup view for data population
            TextView emoji = (TextView) convertView.findViewById(R.id.emoji);
            TextView text = (TextView) convertView.findViewById(R.id.name);
            // Populate the data into the template view using the data object
            emoji.setText(reminder.emoji);
            text.setText(reminder.name);
            // Return the completed view to render on screen
            return convertView;
        }


        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
