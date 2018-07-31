package com.example.grandwordremember;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    int start = 0;
    int wordColor;
    int wordNum;
    boolean showBackground;
    ArrayList<String> mExplanations;
    Map<String, Boolean> records;
    ArrayList<WordTestBean> wordTestBeans;
    SharedPreferences mSharedPreferences;

    @BindView(R.id.activity_test_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_test_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.submit)
    Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(TestActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        wordNum = mSharedPreferences.getInt("number", 10);
        switch (mSharedPreferences.getString("color", "Red")) {
            case "Red":
               wordColor = Color.RED;
               break;
            case "Green":
                wordColor = Color.GREEN;
                break;
            case "Blue":
                wordColor = Color.BLUE;
                break;
            case "Black":
                wordColor = Color.BLACK;
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_test:
                MyAdapter myAdapter = new MyAdapter(getTestContent(start));
                mRecyclerView.setAdapter(myAdapter);
                mSubmitButton.setVisibility(View.VISIBLE);
                showBackground = false;
                break;
            case R.id.start_review:
                break;
        }
        return true;
    }

    static public Intent getIntent(Context context) {
        return new Intent(context, TestActivity.class);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.word)
        TextView mTextView;
        @BindView(R.id.radio_group)
        RadioGroup mRadioGroup;
        @BindView(R.id.choice1)
        RadioButton mRadioButton1;
        @BindView(R.id.choice2)
        RadioButton mRadioButton2;
        @BindView(R.id.choice3)
        RadioButton mRadioButton3;
        @BindView(R.id.choice4)
        RadioButton mRadioButton4;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mRadioGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    for (int i = 0; i < 4; i++) {
                        mRadioGroup.getChildAt(i).setBackgroundColor(0);
                    }

                    if (showBackground && mRadioGroup.getTag(R.integer.answer) != null) {
                        Integer index = (Integer) mRadioGroup.getTag(R.integer.answer);
                        mRadioGroup.getChildAt(index).setBackgroundColor(Color.GRAY);
                    }
                    return true;
                }
            });

            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (group.getCheckedRadioButtonId() != -1) {
                        Integer index = (Integer) group.getTag(R.integer.answer);
                        String word = (String) group.getTag(R.integer.word);
                        if (group.getChildAt(index).getId() == checkedId) {
                            records.put(word, true);
                        } else {
                            records.put(word, false);
                        }
                    }
                }
            });

            mTextView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mTextView.setTextColor(wordColor);
                    return true;
                }
            });
        }
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<WordTestBean> mTestWords;

        public MyAdapter(List<WordTestBean> wordTestBeans) {
            super();
            mTestWords = wordTestBeans;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.mRadioGroup.setTag(R.integer.word, mTestWords.get(position).getWord());
            holder.mTextView.setText(mTestWords.get(position).getWord());
            ArrayList<String> explanations = mTestWords.get(position).getExplanations();
            String answer = mExplanations.get(position);
            holder.mRadioGroup.clearCheck();

            int choice = mTestWords.get(position).getChoice();
            if (choice != -1) {
                ((RadioButton) holder.mRadioGroup.getChildAt(choice)).setChecked(true);
            }

            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0:
                        holder.mRadioButton1.setOnClickListener(getCustomOnClickListener(0, position));
                        if (answer.equals(explanations.get(i))) {
                            holder.mRadioGroup.setTag(R.integer.answer, 0);
                        }
                        holder.mRadioButton1.setText(explanations.get(i));
                        break;
                    case 1:
                        holder.mRadioButton2.setOnClickListener(getCustomOnClickListener(1, position));
                        if (answer.equals(explanations.get(i))) {
                            holder.mRadioGroup.setTag(R.integer.answer, 1);
                        }
                        holder.mRadioButton2.setText(explanations.get(i));
                        break;
                    case 2:
                        holder.mRadioButton3.setOnClickListener(getCustomOnClickListener(2, position));
                        if (answer.equals(explanations.get(i))) {
                            holder.mRadioGroup.setTag(R.integer.answer, 2);
                        }
                        holder.mRadioButton3.setText(explanations.get(i));
                        break;
                    case 3:
                        holder.mRadioButton4.setOnClickListener(getCustomOnClickListener(3, position));
                        if (answer.equals(explanations.get(i))) {
                            holder.mRadioGroup.setTag(R.integer.answer, 3);
                        }
                        holder.mRadioButton4.setText(explanations.get(i));
                        break;
                }
            }
        }

        View.OnClickListener getCustomOnClickListener( final int choice, final int position) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTestWords.get(position).setChoice(choice);
                }
            };
        }

        @Override
        public int getItemCount() {
            return mTestWords.size();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(TestActivity.this);
            View view = layoutInflater.inflate(R.layout.recycler_view_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @OnClick(R.id.submit)
    void submit() {
       showBackground = true;

       WordsDb wordsDb = new WordsDb(TestActivity.this);
       String word = null;
       int level = 0;
       Cursor cursor = null;

       for (WordTestBean wordTestBean : wordTestBeans) {
           word = wordTestBean.getWord();
           level = wordTestBean.getLevel();
           cursor = wordsDb.query(word);

           int testCount = 1, correctCount = 0;
           if (records.get(word)) {
               correctCount = 1;
           }

           WordRecBean wordRecBean = new WordRecBean();
           wordRecBean.setWord(word);
           wordRecBean.setLevel(level);
           wordRecBean.setTestCount(1);


           if (cursor.getCount() != 0) {
               cursor.moveToNext();
               testCount = cursor.getInt(cursor.getColumnIndex("test_count")) + 1;
               correctCount = cursor.getInt(cursor.getColumnIndex("correct_count"));
               if (records.get(word)) {
                   correctCount = correctCount + 1;
               }
               wordRecBean.setTestCount(testCount);
               wordRecBean.setCorrectCount(correctCount);
               wordsDb.update(wordRecBean);
           }  else {
               wordRecBean.setTestCount(testCount);
               wordRecBean.setCorrectCount(correctCount);
               wordsDb.insert(wordRecBean);
           }
       }
       cursor.close();
       start += wordNum;
    }

    public List<WordTestBean> getTestContent(int start) {;
        Uri uri = Uri.parse("content://com.example.granddictionary.DictContentProvider/dict");
        Cursor cursor = getContentResolver().query(uri, null, null, null, "_id limit " + wordNum + " offset " + start );

        mExplanations = new ArrayList<>();
        records = new HashMap<>();
        ArrayList<String> words = new ArrayList<>();
        ArrayList<Integer> levels = new ArrayList<>();
        wordTestBeans = new ArrayList<>();
        Pattern p = Pattern.compile("\\[.*\\](.*)");

        while (cursor.moveToNext()) {
            Integer level = cursor.getInt(cursor.getColumnIndex("level"));
            levels.add(level);
            String word = cursor.getString(cursor.getColumnIndex("word"));
            words.add(word);
            records.put(word, false);
            Matcher m = p.matcher(cursor.getString(cursor.getColumnIndex("explanation")));
            if (m.find()) {
                mExplanations.add(m.group(1).trim());
            }
        }

        cursor.close();

        Random random = new Random();
        int index = 0;

        for (int i = 0; i < words.size(); i++) {
            ArrayList<String> choices = new ArrayList<>();
            WordTestBean wordTestBean = new WordTestBean();
            wordTestBean.setWord(words.get(i));
            wordTestBean.setLevel(levels.get(i));

            Set<Integer> indexes = new HashSet<>();
            indexes.add(i);
            while(indexes.size() != 4) {
                index = random.nextInt(10);
                indexes.add(index);
            }

            for (Integer j : indexes) {
                choices.add(mExplanations.get(j));
            }

            wordTestBean.setExplanations(choices);
            wordTestBeans.add(wordTestBean);
        }

        return wordTestBeans;
    }
}
