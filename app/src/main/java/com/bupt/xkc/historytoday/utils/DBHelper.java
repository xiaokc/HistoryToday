package com.bupt.xkc.historytoday.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建和升级数据库、数据表
 * Created by xkc on 4/7/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    //数据库名
    private static final String DATABASE_NAME = "today.db";

    //数据库版本号
    private static final int DATABASE_VERSION = 1;

    //创建数据表存储每天的历史事件列表内容
    private static final String CREATE_TABLE_TODAY_HISTORY =
            "create table today_history(_id integer primary key autoincrement, "
            + " e_id varchar(50),"
            + " day varchar(20), "
            + " date date,"
            + " title varchar(200))";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TODAY_HISTORY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
