package com.example.grandwordremember;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/7/27 0027.
 */

public class WordTestBean {
    private int choice = -1;
    private String word;
    private int level;
    private ArrayList<String> explanations;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ArrayList<String> getExplanations() {
        return explanations;
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public void setExplanations(ArrayList<String> explanations) {
        this.explanations = explanations;
    }
}
