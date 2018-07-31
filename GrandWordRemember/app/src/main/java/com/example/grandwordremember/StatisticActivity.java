package com.example.grandwordremember;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Dictionary;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticActivity extends AppCompatActivity {
    @BindView(R.id.activity_statistic_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_statistic_recycler_view)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        WordsDb wordsDb = new WordsDb(StatisticActivity.this);
        List<WordRecBean> wordRecBeans = wordsDb.getAllRecords();
        MyAdapter myAdapter = new MyAdapter(wordRecBeans);
        mRecyclerView.setAdapter(myAdapter);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.activity_statistic_word)
        TextView word;
        @BindView(R.id.activity_statistic_level)
        TextView level;
        @BindView(R.id.activity_statistic_right)
        TextView correctCount;
        @BindView(R.id.activity_statistic_test)
        TextView testCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<WordRecBean> mWordRecBeans;

        public MyAdapter(List<WordRecBean> wordRecBeans) {
            super();
            mWordRecBeans = wordRecBeans;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.word.setText(mWordRecBeans.get(position).getWord());
            holder.level.setText(String.valueOf(mWordRecBeans.get(position).getLevel()));
            holder.testCount.setText(String.valueOf(mWordRecBeans.get(position).getTestCount()));
            holder.correctCount.setText(String.valueOf(mWordRecBeans.get(position).getCorrectCount()));
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(StatisticActivity.this).inflate(R.layout.statistic_recycler_view_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return mWordRecBeans.size();
        }
    }

    static public Intent getIntent(Context context) {
        return new Intent(context, StatisticActivity.class);
    }
}
