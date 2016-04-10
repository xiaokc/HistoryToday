package com.bupt.xkc.historytoday.services;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.bupt.xkc.historytoday.callbacks.LoadEventListCallback;
import com.bupt.xkc.historytoday.config.APIs;
import com.bupt.xkc.historytoday.models.ListModel;
import com.bupt.xkc.historytoday.utils.DBManager;
import com.bupt.xkc.historytoday.utils.HttpUtil;

import java.util.ArrayList;

/**
 * Created by xkc on 4/8/16.
 */
public class LoadEventListService extends IntentService {
    private static final String LOG_TAG = LoadEventListService.class.getSimpleName();
    private DBManager dbManager;
    private Intent broadcastIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        dbManager = new DBManager(LoadEventListService.this);
        broadcastIntent = new Intent("com.bupt.xkc.historytoday.activitys.main");
    }

    public LoadEventListService() {
        this(LOG_TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LoadEventListService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LOG_TAG,"====>onHandleIntent...");
        String todayDate = intent.getStringExtra("today");
        requestEventList(todayDate);


    }

    private void requestEventList(String todayDate) {
        Uri builtUri = Uri.parse(APIs.REQUEST_URL.QUERY_EVENT).buildUpon()
                .appendQueryParameter("key", APIs.REQUEST_URL.API_KEY)
                .appendQueryParameter("date",todayDate)
                .build();

        Log.i(LOG_TAG,"====>builtUri="+builtUri);

        HttpUtil.getEventListJson(new LoadEventListCallback() {
            @Override
            public void onLoadFinish(Exception e, Object result) {
                //TODO:将json字符串解析成ListMode，并存入本地数据库
                if (e == null) {
                    String jsonString = (String) result;
                    if (jsonString != null && jsonString.length() > 0) {
                        ArrayList<ListModel> listModels = HttpUtil.getEventListFromJson(jsonString);
                        saveEventToLocal(listModels);
                        sendBroadcast(broadcastIntent);
                    }
                }

            }
        }, builtUri);
    }


    private void saveEventToLocal(ArrayList<ListModel> listModels){
        for (int i = 0; i < listModels.size(); i ++){
            ListModel event = listModels.get(i);
            dbManager.addOneEvent(event);
        }
    }

}
