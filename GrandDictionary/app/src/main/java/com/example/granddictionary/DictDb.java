package com.example.granddictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/21 0021.
 */

public class DictDb {
    private DBOpenHandler mDBOpenHandler;

    public DictDb(Context context) {
        this.mDBOpenHandler = new DBOpenHandler(context, "dictDb", null, 1);
    }

    public void insert(WordRecBean wordRecBean) {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        db.execSQL("INSERT INTO dict(word, explanation, level) values(?, ?, ?)", new Object[]{wordRecBean.getWord(), wordRecBean.getExplanation(), wordRecBean.getLevel()});
        db.close();
    }

    public void insert(ContentValues contentValues) {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        db.insert(DBOpenHandler.TABLE_DICT_NAME, null, contentValues);
        db.close();
    }

    public void update(WordRecBean wordRecBean) {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        db.execSQL("UPDATE dict SET explanation='?', level='?' WHERE word='?'", new Object[]{wordRecBean.getExplanation(),
            wordRecBean.getLevel(), wordRecBean.getWord()});
        db.close();
    }

    public int update(ContentValues contentValues, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        int res = db.update(DBOpenHandler.TABLE_DICT_NAME, contentValues, whereClause, whereArgs);
        db.close();
        return res;
    }

    public void initDict(List<WordRecBean> wordRecBeans) {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        int size = wordRecBeans.size();
        int count = 0;
        for (WordRecBean wordRecBean : wordRecBeans) {
            try {
                db.execSQL("INSERT INTO dict(word, explanation, level) values(?, ?, ?)", new Object[]{wordRecBean.getWord(), wordRecBean.getExplanation(), wordRecBean.getLevel()});
                count++;
                MainActivity.setProgressStatus(count * MainActivity.MAX_PROGRESS / size);
                MainActivity.mMyHandler.sendEmptyMessage(0x123);
            } catch (SQLiteConstraintException e) {
                count++;
                MainActivity.setProgressStatus(count * MainActivity.MAX_PROGRESS / size);
                MainActivity.mMyHandler.sendEmptyMessage(0x123);
            }
            catch (android.database.SQLException e) {
                e.printStackTrace();
            }
        }
        db.close();
    }

    public Cursor query(String[] projection, String where, String[] whereArgs, String sortOrder) {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        Cursor cursor = db.query("dict", projection, where, whereArgs, null, null, sortOrder, null);
        return cursor;
    }

    public int delete(String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        int res = db.delete(DBOpenHandler.TABLE_DICT_NAME, selection, selectionArgs);
        db.close();
        return res;
    }

    public List<WordRecBean> findAll() {
        List<WordRecBean> wordRecBeans = new ArrayList<>();
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        Cursor cursor =db.query("dict", new String[]{"word", "explanation", "level"}, null, null, null, null, "word COLLATE NOCASE");
        WordRecBean wordRecBean = null;

        while (cursor.moveToNext()) {
            wordRecBean = new WordRecBean();
            wordRecBean.setWord(cursor.getString(cursor.getColumnIndex("word")))    ;
            wordRecBean.setExplanation(cursor.getString(cursor.getColumnIndex("explanation")));
            wordRecBean.setLevel(cursor.getString(cursor.getColumnIndex("level")));
            wordRecBeans.add(wordRecBean);
        }

        db.close();
        return wordRecBeans;
    }
}
