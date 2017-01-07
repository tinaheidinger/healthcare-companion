package com.example.maria.basestationapp;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;
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

public class EnteringDailyGoal extends AppCompatActivity {
    private static final String TAG = "EnteringDailyGoals";
    private static String name = "";
    private static String emoji = "";

    private EditText editText;
    private EditText editEmoji;

    private Button button;
    private Button save;
    private Button back;
    private boolean backcheck=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entering_daily_goal);

        TextView titel = (TextView)findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.titleGoal);
        editEmoji = (EditText) findViewById(R.id.emojiGoal);

        button = (Button) findViewById(R.id.furtherButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                name = editText.getText().toString();
                emoji = EmojiMap.replaceUnicodeEmojis(editEmoji.getText().toString());
                backcheck=true;
                loadCalender();
                Log.d(TAG,name);
            }

        });

        /*int unicode = 0x1F604;
        String emoji = new String(Character.toChars(unicode));

        titel.setText(emoji);*/

    }

    private void loadCalender(){
        setContentView(R.layout.activity_entering_daily_goal_datepicker);
        //new HttpAsyncTaskPOST().execute("http://139.59.158.39:8080/goal");
        back =(Button) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {;
                setContentView(R.layout.activity_entering_daily_goal);

                TextView titel = (TextView)findViewById(R.id.textView);
                editText = (EditText) findViewById(R.id.titleGoal);
                editEmoji = (EditText) findViewById(R.id.emojiGoal);

                button = (Button) findViewById(R.id.furtherButton);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        name = editText.getText().toString();
                        emoji = EmojiMap.replaceUnicodeEmojis(editEmoji.getText().toString());
                        backcheck=true;
                        loadCalender();
                        Log.d(TAG,name);
                    }

                });
                editText.setText(name);
                editEmoji.setText(EmojiMap.replaceCheatSheetEmojis(emoji));
            }
        });
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
        // onPostExecute displays the results of the AsyncTask.
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
            jsonObject.accumulate("emoji", emoji);
            jsonObject.accumulate("text", name);

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
