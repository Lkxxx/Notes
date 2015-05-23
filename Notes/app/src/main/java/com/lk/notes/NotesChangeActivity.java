package com.lk.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesChangeActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private EditText et_title, et_text;
    private NotesDao dao;
    private long timecurrentTimeMillis;
    private List<NotesInfo> mNotesInfos;
    private NotesAdapter adapter;
    private String id;
    private String str_title;
    private String str_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        initView();


    }

    private void initView() {
        et_text = (EditText) findViewById(R.id.et_text);
        et_title = (EditText) findViewById(R.id.et_title);

        Intent intent = this.getIntent();
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        id = intent.getStringExtra("id");
        et_title.setText(title);
        et_text.setText(text);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("编辑");
        toolbar.setTitleTextColor(Color.rgb(15, 15, 15));

        toolbar.setBackgroundColor(Color.rgb(0, 159, 175));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_check:
                        change();
                        Window window = getWindow();
                        toolbar.setBackgroundColor(Color.rgb(211, 14, 25));
                        window.setStatusBarColor(Color.rgb(189, 10, 12));
                        finish();
                        break;


                }
                return true;
            }
        });


    }


    private void change() {

        NotesInfo info = new NotesInfo();
        dao = new NotesDao(this);
        str_title = et_title.getText().toString().trim();

        str_text = et_text.getText().toString().trim();
        long timeGetTime = new Date().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH时mm分",
                Locale.getDefault());
        String str_time = sdf.format(timeGetTime);
        Log.e("a", str_title + str_text + str_time+id);
        if (TextUtils.isEmpty(str_title)) {
            Toast.makeText(this, "请输入标题", Toast.LENGTH_LONG).show();
            return;
        }else{
            info.setTitle(str_title);
            info.setText(str_text);
            info.setTime(str_time);
            Log.e("a", str_title + str_text + str_time);
            dao.change(str_title, str_text, str_time,id);}


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(NotesChangeActivity.this);

                alertDialog.setTitle("FBI warning");
                alertDialog.setMessage("保存？");
                alertDialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        change();
                        finish();

                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                alertDialog.show();

                return true;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            change();
            Toast.makeText(NotesChangeActivity.this, "修改已经保存", Toast.LENGTH_SHORT).show();
            finish();
        }


            return super.onKeyDown(keyCode, event);
        }
    }

