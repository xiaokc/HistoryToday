package com.bupt.xkc.historytoday.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bupt.xkc.historytoday.models.ListModel;

/**
 * 本地数据库表的增删改查
 * Created by xkc on 4/7/16.
 */
public class DBManager {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private Cursor cursor;

    private final String LOG_TAG = DBManager.class.getSimpleName();

    public DBManager(Context context){
        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    //添加一条历史事件
    public void addOneEvent(ListModel model){
        db.beginTransaction();
        String add = "insert into today_history(e_id,day,date,title)"
                + " values(?,?,?,?)";

        try {
            db.execSQL(add,
                    new String[]{model.getE_id(),model.getDay(),model.getDate(),model.getTitle()});
            db.setTransactionSuccessful();

        }catch (Exception e){
            Log.e(LOG_TAG,"====>add event failed!");
        }finally {
            db.endTransaction();
        }


    }

    //查询当天的事件列表
    public Cursor queryEventList(String todayDate){
        Log.i(LOG_TAG,"====>query todayDate="+todayDate);
        cursor = db.rawQuery("select * from today_history where day = ?",new String[]{todayDate});

        return cursor;
    }

    public void close(){
        cursor.close();
    }


}
