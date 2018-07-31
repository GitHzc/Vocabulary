package com.example.grandwordremember;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.activity_main_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.test:
                Intent intent = TestActivity.getIntent(MainActivity.this);
                startActivity(intent);
                break;
            case R.id.add:
                showAddDialog();
                break;
            case R.id.statistic:
                Intent intent1 = StatisticActivity.getIntent(MainActivity.this);
                startActivity(intent1);
                break;
            case R.id.search:
                break;
            case R.id.setting:
                Intent intent2 = SettingActivity.getIntent(MainActivity.this);
                startActivity(intent2);
                break;
        }
        return true;
    }

    void showAddDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View view = layoutInflater.inflate(R.layout.add_dialog, null, false);
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setView(view)
                .setIcon(R.drawable.dict)
                .setTitle("增加单词")
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
                        Uri uri = Uri.parse("content://com.example.granddictionary.DictContentProvider/dict");

                        Cursor cursor = getContentResolver().query(uri,
                                null,
                                "word=?",
                                new String[]{word},
                                null);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("explanation", explanation);
                        contentValues.put("level", level);

                        if (cursor.getCount() == 0) {
                            contentValues.put("word", word);
                            getContentResolver().insert(uri, contentValues);
                            Toast.makeText(MainActivity.this, "已加入", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isChecked) {
                                getContentResolver().update(uri, contentValues, "word=?", new String[]{word});
                                Toast.makeText(MainActivity.this, "已更新", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "单词已存在", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .create();
        alertDialog.show();
    }
}
