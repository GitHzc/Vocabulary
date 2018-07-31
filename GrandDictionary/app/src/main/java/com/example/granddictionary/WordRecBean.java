package com.example.granddictionary;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/22 0022.
 */

public class WordRecBean implements Comparable<WordRecBean>{

    /**
     * word : be
     * explanation : [bi:,bi] aux. v.(am,is,are之原型) vi.是ˌ在
     * level : 1
     */

    @SerializedName("word")
    private String word;
    @SerializedName("explanation")
    private String explanation;
    @SerializedName("level")
    private String level;

    public static WordRecBean objectFromData(String str) {

        return new Gson().fromJson(str, WordRecBean.class);
    }

    public static List<WordRecBean> arrayWordRecBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<WordRecBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public int compareTo(@NonNull WordRecBean o) {
        return (this.getWord().toLowerCase()).compareTo(o.getWord().toLowerCase());
    }
}
