package com.example.grandwordremember;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {
    @BindView(R.id.activity_setting_word_color)
    TextView mWordColor;
    @BindView(R.id.activity_setting_word_num)
    TextView mWordNum;
    @BindView(R.id.activity_setting_save_statistic)
    TextView mWordss;
    @BindView(R.id.activity_setting_check_box)
    CheckBox mSaveAndStatistic;
    @BindView(R.id.activity_setting_toolbar)
    Toolbar mToolbar;

    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this);
        String color = mSharedPreferences.getString("color", "red");
        int num = mSharedPreferences.getInt("number", 10);
        String isStatistic = mSharedPreferences.getString("saveAndStatistic", "是");
        mWordColor.setText(color);
        mWordNum.setText(String.valueOf(num));
        mWordss.setText(isStatistic);

        if (isStatistic.equals("是")) {
            mSaveAndStatistic.setChecked(true);
        } else {
            mSaveAndStatistic.setChecked(false);
        }

        mSaveAndStatistic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWordss.setText("是");
                    mSharedPreferences.edit().putString("saveAndStatistic", "是").apply();
                } else {
                    mWordss.setText("否");
                    mSharedPreferences.edit().putString("saveAndStatistic", "否").apply();
                }
            }
        });
    }

    @OnClick({R.id.activity_setting_word_color, R.id.activity_setting_word_num})
    void click(View v) {
        switch (v.getId()) {
            case R.id.activity_setting_word_color:
                showChooseColorDialog();
                break;
            case R.id.activity_setting_word_num:
                showSettingWordNumDialog();
                break;
        }
    }

    static public Intent getIntent(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    private void showChooseColorDialog() {
        int checkItem = 0;
        String color = mSharedPreferences.getString("color", "Red");
        String[] colorArray = getResources().getStringArray(R.array.color_choice);
        for (int i = 0; i < colorArray.length; i++) {
            if (color.equals(colorArray[i])) {
                checkItem = i;
                break;
            }
        }

        AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this)
                .setTitle("请选择单词颜色")
                .setSingleChoiceItems(R.array.color_choice, checkItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String color = getResources().getStringArray(R.array.color_choice)[which];
                        mSharedPreferences.edit().putString("color", color).apply();
                        mWordColor.setText(color);
                       dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
    }

    private void showSettingWordNumDialog() {
        final int num = mSharedPreferences.getInt("number", 10);
        final EditText editText = new EditText(SettingActivity.this);

        AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this)
                .setTitle("测试单词数")
                .setView(editText)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSharedPreferences.edit().putInt("number", num).apply();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText() != null) {
                            mWordNum.setText(editText.getText());
                            int n = Integer.valueOf(editText.getText().toString());
                            mSharedPreferences.edit().putInt("number", n).apply();
                        }
                    }
                })
                .create();
        alertDialog.show();
    }
}
