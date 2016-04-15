package com.bupt.xkc.historytoday.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bupt.xkc.historytoday.models.ListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 从本地数据库加载分页加载事件列表
 * Created by xkc on 4/14/16.
 */
public class EventLoader {
    private volatile static EventLoader eventLoader;
    private  DBManager dbManager;
    private static int page;
    private static int itemCountPerPage;

    private final String LOG_TAG = EventLoader.class.getSimpleName();
    private  EventLoader(Context context){
        init(context);

    }

    //作为一个缓存队列，存储20条事件
    private static ArrayList<ListModel> list;

    private static final int capacity = 20;

    private  void init(Context context) {
        list = new ArrayList<>();
        dbManager = new DBManager(context);

    }

    public static EventLoader getInstance(Context context, int page, int itemCountPerPage){
        EventLoader.page = page;
        EventLoader.itemCountPerPage = itemCountPerPage;
        if (eventLoader == null){
            synchronized (EventLoader.class){
                if (eventLoader == null) {
                    eventLoader = new EventLoader(context);
                }
            }
        }
        return eventLoader;
    }

    //加载第page页，每页的item个数为itemCountPerPage
    public  ArrayList<ListModel> load(){
        ArrayList<ListModel> loadList = new ArrayList<>();

        //如果list中的事件个数大于一页中要显示的事件条数
        if (list.size() >= itemCountPerPage){
            for (int i = 0; i < itemCountPerPage; i ++){
                loadList.add(list.get(0));
                list.remove(0);
            }

        }else {//如果个数小于一页中要显示的条数
            updateList();
        }

        return loadList;
    }


    private void updateList(){
        if (list.size() <= capacity){//此时缓存队列中的条数没有填满缓冲区（最大容量）
            int lastId = page * itemCountPerPage;//请求的最后一个event的_id
            ArrayList<ListModel> listFromDB = getListFromDB(lastId);

            for (int i = 0; i < listFromDB.size() && i < capacity; i ++){
                list.add(listFromDB.get(i));
            }

        }
    }


    private  ArrayList<ListModel> getListFromDB(int lastId) {
        Cursor cursor = dbManager.queryDefault(lastId);
        ArrayList<ListModel> listModels = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
                int year = cursor.getInt(cursor.getColumnIndexOrThrow("year"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String e_id = cursor.getString(cursor.getColumnIndexOrThrow("e_id"));

                ListModel event = new ListModel(e_id, day, year, title);
                listModels.add(event);

            }while (cursor.moveToNext());
        }

        return listModels;
    }


}
