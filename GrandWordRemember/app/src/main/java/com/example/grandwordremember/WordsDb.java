package com.example.grandwordremember;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/29 0029.
 */

public class WordsDb {
    private DBOpenHandler mDBOpenHandler;

    public WordsDb(Context context) {
        this.mDBOpenHandler = new DBOpenHandler(context, "words", null, 1);
    }

    public void insert(WordRecBean wordRecBean) {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        db.execSQL("INSERT INTO words(word, level, test_count, correct_count) values(?, ?, ?, ?)",
                new String[]{wordRecBean.getWord(), String.valueOf(wordRecBean.getLevel()), String.valueOf(wordRecBean.getTestCount()), String.valueOf(wordRecBean.getCorrectCount())});
        db.close();
    }

    public void update(WordRecBean wordRecBean) {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        db.execSQL("UPDATE words SET level=?, test_count=?, correct_count=? WHERE word=?",
                new String[]{String.valueOf(wordRecBean.getLevel()), String.valueOf(wordRecBean.getTestCount()), String.valueOf(wordRecBean.getCorrectCount()), wordRecBean.getWord()});
        db.close();
    }

    public Cursor query(String word) {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        return db.query("words",
                new String[]{"test_count", "correct_count"},
                "word=?",
                new String[]{word},
                null,
                null,
                null);
    }

    public List<WordRecBean> getAllRecords() {
        SQLiteDatabase db = mDBOpenHandler.getWritableDatabase();
        Cursor cursor = db.query("words",
                new String[]{"word", "level", "test_count", "correct_count"},
                null,
                null,
                null,
                null,
                "word");
        List<WordRecBean> wordRecBeans = new ArrayList<>();

        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndex("word"));
            int level = cursor.getInt(cursor.getColumnIndex("level"));
            int testCount = cursor.getInt(cursor.getColumnIndex("test_count"));
            int correctCount = cursor.getInt(cursor.getColumnIndex("correct_count"));

            WordRecBean wordRecBean = new WordRecBean();
            wordRecBean.setWord(word);
            wordRecBean.setLevel(level);
            wordRecBean.setTestCount(testCount);
            wordRecBean.setCorrectCount(correctCount);

            wordRecBeans.add(wordRecBean);
        }

        return wordRecBeans;
    }
}
