package com.lk.notes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;


public class SettingActivity extends ActionBarActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout ll_backup, ll_recover, ll_theme, ll_message;
    private ProgressDialog progressDialog;
    private static final int RECOVER = 3;
    String dbFilePath = Environment.getExternalStorageDirectory() + "/Notes/backup/notes.db";


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RECOVER) {
                progressDialog.dismiss();
                Toast.makeText(SettingActivity.this, "还原成功", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

    }

    private void initView() {
        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 159);
        int b = sharedPreferences.getInt("b", 175);
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(15);
        toolbar.setPopupTheme(R.style.MyToolBar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));

        }

        ll_backup = (LinearLayout) findViewById(R.id.ll_backup);
        ll_recover = (LinearLayout) findViewById(R.id.ll_recover);
        ll_theme = (LinearLayout) findViewById(R.id.ll_theme);
        ll_message = (LinearLayout) findViewById(R.id.ll_message);
        ll_backup.setOnClickListener(this);
        ll_recover.setOnClickListener(this);
        ll_theme.setOnClickListener(this);
        ll_message.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_backup:
                backup();
                break;
            case R.id.ll_recover:
                recover();
                setResult(100000);
                break;
            case R.id.ll_theme:
                Intent intent = new Intent(SettingActivity.this, ThemeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_message:
                Intent intent1 = new Intent(SettingActivity.this, MessageActivity.class);
                startActivity(intent1);
                break;

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void backup() {
        new BackupTask(this).execute("backupDatabase");
        Toast.makeText(this, "备份成功", Toast.LENGTH_SHORT).show();

    }

    private void recover() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingActivity.this);
        alertDialog.setTitle("提示");
        alertDialog.setMessage("确认还原？");
        alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (new File(dbFilePath).exists()) {
                    progressDialog = ProgressDialog.show(SettingActivity.this, "", "正在还原", true, false);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            re();
                            Message msg = new Message();
                            msg.what = RECOVER;
                            handler.sendMessage(msg);
                        }

                    });
                    thread.start();
                } else {
                    Toast.makeText(SettingActivity.this, "没有发现备份文件", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("合并", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });

        alertDialog.show();
    }


    public SQLiteDatabase op(Context context) {

        Log.e("path", dbFilePath);
        File Path = new File(dbFilePath);
        if (Path.exists()) {
            return SQLiteDatabase.openOrCreateDatabase(Path, null);
        } else {
            return SQLiteDatabase.openOrCreateDatabase(Path, null);
        }
    }


    public void re() {
        String title, text, time, id, year, clock;
        NotesDao dao = new NotesDao(this);
        SQLiteDatabase database = op(getApplicationContext());
        Cursor c = database.query("notes", new String[]{"title", "text", "time", "id", "year", "clock"}, null, null, null, null, "id");
        while (c.moveToNext()) {
            title = c.getString(0);
            text = c.getString(1);
            time = c.getString(2);
            id = c.getString(3);
            year = c.getString(4);
            clock = c.getString(5);
            if (idNotExist(id)) {
                dao.add(title, text, time, id, year, clock);
            }

        }
        c.close();
        database.close();
    }

    public boolean idNotExist(String id) {
        SQLiteDatabase database1 = new NotesOpenHelper(this).getReadableDatabase();
        Cursor cursor = database1.query("notes", null, "id = ?", new String[]{id}, null, null, null);
        if (cursor.moveToNext()) {
            //存在
            cursor.close();
            database1.close();
            Log.e("exist", "ture");
            return false;
        } else {
            //不存在
            cursor.close();
            database1.close();
            Log.e("exist", "false");
            return true;
        }

    }





}
