package com.example.grandwordremember;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/7/29 0029.
 */

public class DBOpenHandler extends SQLiteOpenHelper {
    public static String TABLE_WORDS_NAME = "words";

    public DBOpenHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE words(_id integer primary key autoincrement," +
                "word varchar(64) unique," +
                "level int default 0," +
                "test_count int default 0," +
                "correct_count int default 0," +
                "last_time timestamp)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
