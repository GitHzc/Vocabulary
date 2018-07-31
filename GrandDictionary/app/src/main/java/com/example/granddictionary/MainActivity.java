package com.example.granddictionary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static private ProgressDialog mProgressDialog;
    static public MyHandler mMyHandler;
    static private int progressStatus;
    final static int MAX_PROGRESS = 100;
    final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    List<WordRecBean> wordRecBeans;
    private WordAdapter wordAdapter;
    boolean filterType;
    boolean showMeaning = false;


    @BindView(R.id.activity_main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_main_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.activity_main_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.activity_main_show_word)
    TextView mShowWord;
    @BindView(R.id.activity_main_show_explanation)
    TextView mShowExplanation;
    @BindView(R.id.activity_main_linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.activity_main_show_linear_layout)
    LinearLayout mShowLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        showWords();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                for (int i = 0; i < 28; i++) {
                    TextView temp = (TextView) mLinearLayout.getChildAt(i);
                    temp.setBackgroundResource(R.color.coppery);
                }
                showWords();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        initialIndex();
    }

    private void initialIndex() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterType = false;
                for (int i = 0; i < 28; i++) {
                    TextView temp = (TextView) mLinearLayout.getChildAt(i);
                    temp.setBackgroundResource(R.color.coppery);
                }

                v.setBackgroundResource(R.color.seaBlue);

                String letter = "";
                if (((TextView) v).getText() != null) {
                    letter = ((TextView) v).getText().toString();
                }
                wordAdapter.getFilter().filter(letter);
            }
        };

        for (int i = 0; i < 28; i++) {
            TextView textView = new TextView(MainActivity.this);
            LinearLayout.LayoutParams lp = null;
            if (i != 0 && i != 27) {
                textView.setText(alphabet.substring(i - 1, i));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                lp = new LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.WRAP_CONTENT);
            } else {
                lp = new LinearLayout.LayoutParams(70, LinearLayout.LayoutParams.MATCH_PARENT);
            }

            textView.setBackgroundResource(R.color.coppery);
            lp.setMarginEnd(10);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(lp);
            textView.setOnClickListener(onClickListener);
            mLinearLayout.addView(textView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.support, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                showSearchDialog();
                break;
            case R.id.add:
                showAddDialog();
                break;
            case R.id.download:
                getWords();
                break;
            case R.id.show_meaning:
                if (!item.isChecked()) {
                    showMeaning = true;
                    item.setChecked(true);
                    mShowLinearLayout.setVisibility(View.GONE);
                } else {
                    showMeaning = false;
                    item.setChecked(false);
                    mShowLinearLayout.setVisibility(View.VISIBLE);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    static class MyHandler extends Handler {
        MyHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                if (progressStatus < MAX_PROGRESS) {
                    mProgressDialog.setProgress(progressStatus);
                } else {
                    mProgressDialog.dismiss();
                }
            }
        }
    }

    void getWords() {
        showProgress();
        mMyHandler = new MyHandler();
        Retrofit retrofit = HttpUtils.getRetrofit();
        HttpUtils.MyApi myApi = retrofit.create(HttpUtils.MyApi.class);
        myApi.getWords()
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        List<WordRecBean> wordRecBeans = WordRecBean.arrayWordRecBeanFromData(s);
                        Collections.sort(wordRecBeans);

                        DictDb dictDb = new DictDb(MainActivity.this);
                        dictDb.initDict(wordRecBeans);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                                showWords();
                            }
                        });
                    }
                }) ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.word)
        TextView word;
        @BindView(R.id.explanation)
        TextView explanation;
        @BindView(R.id.recycler_view_linear_layout)
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            explanation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (showMeaning) {
                        explanation.setVisibility(View.VISIBLE);
                    } else {
                        explanation.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view);
    }

    public class WordAdapter extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener,View.OnLongClickListener,Filterable{
        private List<WordRecBean> wordRecList;
        private List<WordRecBean> filterList;
        private OnItemClickListener mOnItemClickListener;
        private OnItemLongClickListener mOnItemLongClickListener;

        public WordAdapter(List<WordRecBean> wordRecBeans) {
            wordRecList = wordRecBeans;
            filterList = wordRecBeans;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.recycler_view_item, parent, false);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return new ViewHolder(view);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(v);
            }
            return true;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.linearLayout.setTag(filterList.get(position));
            WordRecBean wordRecBean = filterList.get(position);
            holder.word.setText(wordRecBean.getWord());
            holder.explanation.setText(wordRecBean.getExplanation());
        }

        @Override
        public int getItemCount() {
            return filterList.size();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
            mOnItemLongClickListener = onItemLongClickListener;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String constraintStr = constraint.toString();
                    if (constraintStr.isEmpty()) {
                        filterList = wordRecList;
                    } else {
                        List<WordRecBean> filteredList = new ArrayList<>();
                        for (WordRecBean wordRecBean : wordRecList) {
                            if (!filterType && wordRecBean.getWord().startsWith(constraintStr)) {
                                filteredList.add(wordRecBean);
                            }
                            if (filterType && wordRecBean.getWord().contains(constraintStr)) {
                                filteredList.add(wordRecBean);
                            }
                        }

                        filterList = filteredList;
                        wordRecBeans = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filterList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filterList = (ArrayList<WordRecBean>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

    public void showWords() {
        final DictDb dictDb = new DictDb(MainActivity.this);
        wordRecBeans = dictDb.findAll();
        wordAdapter = new WordAdapter(wordRecBeans);

        wordAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
            WordRecBean wordRecBean = (WordRecBean) view.getTag();
            mShowWord.setText(wordRecBean.getWord());
            mShowExplanation.setText(wordRecBean.getExplanation());
            }
        });

        wordAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final int index = mRecyclerView.getChildAdapterPosition(view);
                        final String word = wordRecBeans.get(index).getWord();

                        switch (item.getItemId()) {
                            case R.id.delete:
                                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("删除单词")
                                        .setIcon(R.drawable.dict)
                                        .setMessage("是否确定要删除单词" + word)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                wordRecBeans.remove(index);
                                                wordAdapter.notifyItemRemoved(index);
                                                wordAdapter.notifyItemChanged(index);
                                                dictDb.delete("word=?", new String[]{word});
                                                Toast.makeText(MainActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(MainActivity.this, "已取消", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .create();
                                alertDialog.show();
                                break;
                            case R.id.modify:
                                final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.modify_dialog, null);
                                AlertDialog alertDialog1 = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("修改单词")
                                        .setIcon(R.drawable.dict)
                                        .setView(view)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String newWord = ((EditText) view.findViewById(R.id.add_dialog_word)).getText().toString();
                                                String newExplanation = ((EditText) view.findViewById(R.id.add_dialog_explanation)).getText().toString();
                                                String newLevel = ((EditText) view.findViewById(R.id.add_dialog_level)).getText().toString();

                                                ContentValues contentValues = new ContentValues();
                                                contentValues.put("word", newWord);
                                                contentValues.put("explanation", newExplanation);
                                                contentValues.put("level", newLevel);

                                                dictDb.update(contentValues, "word=?", new String[]{word});
                                                Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(MainActivity.this, "已取消", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .create();
                                        alertDialog1.show();
                                break;
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        mRecyclerView.setAdapter(wordAdapter);
    }

    void showProgress() {
        progressStatus = 0;
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMax(MAX_PROGRESS);
        mProgressDialog.setTitle("正在下载...");
        mProgressDialog.setMessage("已完成：");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    static public void setProgressStatus(int p) {
        progressStatus = p;
    }

    private void showSearchDialog() {
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.search_dialog, null);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("查询单词")
                .setIcon(R.drawable.dict)
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "已取消", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filterType = true;
                        String query = ((EditText) view.findViewById(R.id.search_dialog_edit_text)).getText().toString();
                        wordAdapter.getFilter().filter(query);
                    }
                })
                .create();
        alertDialog.show();
    }

    private void showAddDialog() {
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_dialog, null);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("增加单词")
                .setIcon(R.drawable.dict)
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "已取消", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String word = ((EditText) view.findViewById(R.id.add_dialog_word)).getText().toString();
                        String explanation = ((EditText) view.findViewById(R.id.add_dialog_explanation)).getText().toString();
                        String level = ((EditText) view.findViewById(R.id.add_dialog_level)).getText().toString();
                        boolean isChecked = ((CheckBox) view.findViewById(R.id.add_dialog_checkbox)).isChecked();

                        DictDb dictDb = new DictDb(MainActivity.this);
                        Cursor cursor = dictDb.query(null, "word=?", new String[]{word}, null);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("word", word);
                        contentValues.put("explanation", explanation);
                        contentValues.put("level", level);

                        if (cursor.getCount() == 0) {
                            dictDb.insert(contentValues);
                            Toast.makeText(MainActivity.this, "已增加", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isChecked) {
                                dictDb.update(contentValues, "word=?", new String[]{word});
                                Toast.makeText(MainActivity.this, "已更新", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "已存在，请选择覆盖", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .create();
        alertDialog.show();
    }
}
