package com.example.maria.basestationapp;


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
 */

public class HttpHandler extends AsyncTask<String, Integer, Double>{

    private static final String TAG = "HttpHandler";

    private HttpURLConnection conn;
    private OutputStream outputStream;
    JSONObject postData = new JSONObject();


    @Override
    protected Double doInBackground(String... param) {
        openConnection();
        post();
        return null;
    }

    public void openConnection(){
        URL url = null;
        try {
            url = new URL("http://localhost:8080/");
            conn = (HttpURLConnection) url.openConnection();
        }catch (MalformedURLException e) {
            Log.d(TAG,"openConnection: malformedException");
        }catch (IOException e) {
            Log.d(TAG,"openConnection: IOException");
        }

    }
    public void post(){

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
