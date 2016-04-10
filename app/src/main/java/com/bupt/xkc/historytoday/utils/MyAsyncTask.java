package com.bupt.xkc.historytoday.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xkc on 4/8/16.
 */
public class MyAsyncTask extends AsyncTask<Object,Void,Object> {
    private final String LOG_TAG = MyAsyncTask.class.getSimpleName();
    @Override
    protected String doInBackground(Object... params) {
        Uri uri = (Uri) params[0];
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        String jsonResult = "";
        try {
//            Log.i(LOG_TAG,"====>uri="+uri.toString());
            url = new URL(uri.toString());

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                if (builder.length() != 0) {
                    jsonResult = builder.toString();
                }

            }
        }catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonResult;
    }

}
