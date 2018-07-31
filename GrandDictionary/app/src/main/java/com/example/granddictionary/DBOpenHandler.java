package com.example.granddictionary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/7/21 0021.
 */

public class DBOpenHandler extends SQLiteOpenHelper {

    public static String TABLE_DICT_NAME = "dict";

    public DBOpenHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE dict(_id integer primary key autoincrement, " +
                "word varchar(64) unique, " +
                "explanation text, level int default 0, " +
                "modified_time timestamp)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
