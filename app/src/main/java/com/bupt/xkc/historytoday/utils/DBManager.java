package com.bupt.xkc.historytoday.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 本地数据库表的增删改查
 * Created by xkc on 4/7/16.
 */
public class DBManager {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBManager(Context context){
        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
    }



}
