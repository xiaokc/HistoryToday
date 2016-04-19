package com.bupt.xkc.historytoday.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.style.UpdateAppearance;
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
        String add = "insert into today_history(e_id,day,year,title)"
                + " values(?,?,?,?)";

        try {
            db.execSQL(add,
                    new Object[]{model.getE_id(),model.getDay(),model.getYear(),model.getTitle()});
            db.setTransactionSuccessful();

        }catch (Exception e){
            Log.e(LOG_TAG,"====>add event failed!");
        }finally {
            db.endTransaction();
        }


    }

    //查询当天的事件列表
    public Cursor queryEventList(String todayDate){
//        Log.i(LOG_TAG,"====>query todayDate="+todayDate);
        cursor = db.rawQuery("select * from today_history where day = ? order by year desc",
                new String[]{todayDate});

        return cursor;
    }


    //清空数据表，并将自增列置0
    public void deleteAll(){
        String delete = "delete from today_history";
        String update= "update sqlite_sequence SET seq = 0 where name = 'today_history'";
        db.execSQL(delete);
        db.execSQL(update);
    }


    public Cursor queryLimit(String todayDate, int lastId, int limit){
        cursor = db.rawQuery("select * from today_history where day = ? and _id > " + lastId +
                        " order by year desc limit "+ limit,
                new String[]{todayDate});
        return cursor;
    }


    public Cursor queryLimitDefaultDay(int lastId, int limit){
        cursor = db.rawQuery("select * from today_history where _id > "+ lastId +
        " order by year desc limit " + limit, null);
        return cursor;
    }

    public Cursor queryDefault(String todayDate, int lastId){
        cursor = db.rawQuery("select * from today_history where _id > "
                + lastId +" and day = " + todayDate,null);

        return cursor;
    }


    public void close(){
        cursor.close();
    }


}
