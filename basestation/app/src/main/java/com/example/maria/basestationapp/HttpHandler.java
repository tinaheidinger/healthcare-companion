package com.example.maria.basestationapp;


import android.nfc.Tag;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by maria on 07/12/16.
 *
 * Class to handle AsyncTask, only for testing purpose
 * not used
 */

public class HttpHandler extends AsyncTask<String, Integer, Double> {

    private static final String TAG = "HttpHandler";

    private HttpURLConnection conn;
    private OutputStream outputStream;
    JSONObject postData = new JSONObject();


    @Override
    protected Double doInBackground(String... param) {
        openConnection();
        //post();
        return null;
    }

    public void openConnection() {
        URL url = null;
        try {
            url = new URL("http://127.0.0.1:8080/");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");

            Log.d(TAG,"before connect");
            conn.connect();
            Log.d(TAG,"connected");
        } catch (MalformedURLException e) {
            Log.d(TAG, "openConnection: malformedException");
        } catch (IOException e) {
            Log.d(TAG, "openConnection: IOException");
            e.printStackTrace();
        }

    }

    public void post() {

       try {
           OutputStream os = conn.getOutputStream();
           BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
           writer.write("\"companion\": 1\n" +
                   "    \"emoji\": :),\n" +
                   "    \"text\": test");
           writer.close();
           os.close();

           //Read
           BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

           String line = null;
           StringBuilder sb = new StringBuilder();

           while ((line = br.readLine()) != null) {
               sb.append(line);
           }

           br.close();
           String result = sb.toString();
       }catch (IOException e){
           Log.d(TAG, "post IOException occured");
       }
    }

    public void closeConnection(){

            if(conn != null) {
                conn.disconnect();
            }
    }

    private void writeStream(OutputStream out){
        String output = "companion";

        try {
            outputStream.write(output.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            Log.d(TAG,"writeStream IOE");
        }
    }



}
