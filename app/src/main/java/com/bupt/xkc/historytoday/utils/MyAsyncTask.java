package com.bupt.xkc.historytoday.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.bupt.xkc.historytoday.models.HintMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by xkc on 4/8/16.
 */
public class MyAsyncTask extends AsyncTask<Object,Void,Object> {
    private final String LOG_TAG = MyAsyncTask.class.getSimpleName();
    @Override
    protected Object doInBackground(Object... params) {
        Uri uri = (Uri) params[0];
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        String jsonResult = "";
        HashMap<String,Object> resultMap = new HashMap<>();
        try {
//            Log.i(LOG_TAG,"====>uri="+uri.toString());
            url = new URL(uri.toString());

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(3000);
            conn.setInstanceFollowRedirects(false);//不允许重定向
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {

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
            }else {
                resultMap.put("error",HintMessage.NETWORK_ERROR);
            }
        }catch (IOException e) {
            Log.e(LOG_TAG,"====>http time out!");
            resultMap.put("error",HintMessage.NETWORK_ERROR);
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
        resultMap.put("result",jsonResult);
        return resultMap;
    }

}
