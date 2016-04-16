package com.bupt.xkc.historytoday.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.bupt.xkc.historytoday.callbacks.LoadEventListCallback;
import com.bupt.xkc.historytoday.config.APIs;
import com.bupt.xkc.historytoday.models.HintMessage;
import com.bupt.xkc.historytoday.models.ListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            conn.setRequestProperty("Content-Type",
                    "application/json");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Accept", "application/json");
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

                //接口回调
                if (jsonResult.length() <= 0) {
                    callback.onLoadFinish(new Exception(HintMessage.NO_DATA), jsonResult);
                } else {
                    callback.onLoadFinish(null, jsonResult);
                }

            }else {
                callback.onLoadFinish(new Exception(HintMessage.NETWORK_ERROR),jsonResult);
            }

//            Log.i(LOG_TAG, "====>jsonResult=" + jsonResult);


        } catch (IOException e) {
            Log.e(LOG_TAG,"====>http time out!");
            callback.onLoadFinish(new Exception(HintMessage.NETWORK_ERROR),jsonResult);
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
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
//        Log.i(LOG_TAG,"====>jsonString="+jsonString);
        ArrayList<ListModel> listModels = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(jsonString);
            String reason = object.getString(APIs.RESPONSE_KEY.REASON);
            int error_code = object.getInt(APIs.RESPONSE_KEY.ERROR_CODE);

            if (reason.equalsIgnoreCase("success") && error_code == 0) {//响应成功
                JSONArray result = object.getJSONArray("result");
                int showCount = Math.min(result.length(), 50);//最多下载50条
                for (int i = 0; i < showCount; i++) {
                    JSONObject event = result.getJSONObject(i);
                    String day = event.getString("day");
                    String date = event.getString("date");
                    String title = event.getString("title");
                    String e_id = event.getString("e_id");

                    int  year = TodayHelper.getYear(date);

                    ListModel model = new ListModel(e_id, day, year, title);
                    listModels.add(0,model);

                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "====>json parse exception:"+e.getMessage());
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
            Log.e(LOG_TAG, "====>getEventDetailFromJson() json exception!");
        }

        return map;
    }


    /**
     * 检查是否有网络，
     * 如果手机连上wifi，但是未登录wifi认证，该情况并不能真正访问网络
     * 但是这种方法会返回true
     */
    public static boolean hasNetwork(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            return false;
        } else if (info.isAvailable() && info.isRoaming() &&
                info.getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        return true;

//        return isOnline(context);
    }


    public static boolean isOnline(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Process process = Runtime.getRuntime().exec("ping -c 1 www.baidu.com");
            int returnVal = process.waitFor();
            return (returnVal==0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;

    }


}
