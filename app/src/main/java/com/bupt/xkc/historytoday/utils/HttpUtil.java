package com.bupt.xkc.historytoday.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.bupt.xkc.historytoday.callbacks.LoadEventListCallback;
import com.bupt.xkc.historytoday.config.APIs;
import com.bupt.xkc.historytoday.models.HintMessage;
import com.bupt.xkc.historytoday.models.ListModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xkc on 4/8/16.
 */
public class HttpUtil {
    private static final String LOG_TAG = HttpUtil.class.getSimpleName();

    public static void getEventListJson(LoadEventListCallback callback, Uri builtUri) {
//        Log.i(LOG_TAG, "====>getEventListJson...");
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        String jsonResult = "";
        try {
            url = new URL(builtUri.toString());

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

//            Log.i(LOG_TAG, "====>jsonResult=" + jsonResult);

            //接口回调
            if (jsonResult.length() <= 0) {
                callback.onLoadFinish(new Exception(HintMessage.NO_DATA), jsonResult);
            } else {
                callback.onLoadFinish(null, jsonResult);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static ArrayList<ListModel> getEventListFromJson(String jsonString) {
//        Log.i(LOG_TAG, "====>getEventListFromJson...");
        ArrayList<ListModel> listModels = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(jsonString);
            String reason = object.getString(APIs.RESPONSE_KEY.REASON);
            int error_code = object.getInt(APIs.RESPONSE_KEY.ERROR_CODE);

            if (reason.equalsIgnoreCase("success") && error_code == 0) {//响应成功
                JSONArray result = object.getJSONArray("result");
                int showCount = Math.min(result.length(), 20);//最多下载20条
                for (int i = 0; i < showCount; i++) {
                    JSONObject event = result.getJSONObject(i);
                    String day = event.getString("day");
                    String date = event.getString("date");
                    String title = event.getString("title");
                    String e_id = event.getString("e_id");

                    ListModel model = new ListModel(e_id, day, date, title);
                    listModels.add(model);

                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "json parse exception");
        }

        return listModels;
    }


    public static HashMap<String, String> getEventDetailFromJson(String jsonString) {
//        Log.i(LOG_TAG, "====>getEventDetailFromJson...");
        HashMap<String, String> map = new HashMap<>();
        try {
            JSONObject object = new JSONObject(jsonString);
            String reason = object.getString(APIs.RESPONSE_KEY.REASON);
            int error_code = object.getInt(APIs.RESPONSE_KEY.ERROR_CODE);

            if (reason.equalsIgnoreCase("success") && error_code == 0) {//响应成功
                JSONArray result = object.getJSONArray("result");
                JSONObject event = result.getJSONObject(0);
                String e_id = event.getString("e_id");
                String title = event.getString("title");
                String content = event.getString("content");
                String picNo = event.getString("picNo");//图片个数
                map.put("e_id", e_id);
                map.put("title", title);
                map.put("content", content);


                JSONArray picUrl = event.getJSONArray("picUrl");
                String pic_title = "";
                String url = "";
                if (Integer.parseInt(picNo) > 0) {//如果有图片
                    JSONObject pic = picUrl.getJSONObject(0);//第一个图片json对象
                    pic_title = pic.getString("pic_title");
                    url = pic.getString("url");

                    map.put("pic_title", pic_title);
                    map.put("url", url);

                }

//                Log.i(LOG_TAG, "====>title=" + title + ",content=" + content + ",url=" + url);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "getEventDetailFromJson() json exception!");
        }

        return map;
    }


}
