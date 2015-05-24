package com.lk.notes;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lk.notes.anim.ShakeAnim;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditNoteActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private EditText et_title, et_text;
    private NotesDao dao;
    private long timecurrentTimeMillis;
    private List<NotesInfo> mNotesInfos;
    private NotesAdapter adapter;
    private LinearLayout ll_edit;


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
        et_title.setText(title);
        et_text.setText(text);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("新建");
        toolbar.setTitleTextColor(Color.rgb(15, 15, 15));
        SharedPreferences sharedPreferences =getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 159);
        int b = sharedPreferences.getInt("b", 175);
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));
        window.setNavigationBarColor(Color.rgb(r, g, b));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_check:
                        save();
                        Window window = getWindow();
                        toolbar.setBackgroundColor(Color.rgb(46, 190, 120));
                        window.setStatusBarColor(Color.rgb(41, 171, 108));

                        break;
                    default:
                        break;

                }
                return true;
            }
        });


    }


    private void save() {

        NotesInfo info = new NotesInfo();
        dao = new NotesDao(this);
        String str_title = et_title.getText().toString().trim();
        String str_text = et_text.getText().toString().trim();
        long timeGetTime = new Date().getTime();
        String str_id = String.valueOf(timeGetTime);
        Log.e("msg", str_id);
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH时mm分",
                Locale.getDefault());
        String str_time = sdf.format(timeGetTime);
        Log.e("a", str_title + str_text + str_time);
        if (TextUtils.isEmpty(str_title)) {
            ll_edit = (LinearLayout) findViewById(R.id.ll_edit);
            ShakeAnim shakeAnim = new ShakeAnim();
            shakeAnim.setDuration(300);
            ll_edit.startAnimation(shakeAnim);
            Toast.makeText(this, "请输入标题", Toast.LENGTH_LONG).show();

        } else {
            info.setTitle(str_title);
            info.setText(str_text);
            info.setTime(str_time);
            info.setId(str_id);
            Log.e("a", str_title + str_text + str_time);
            dao.add(str_title, str_text, str_time, str_id);
            finish();
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String str_title = et_title.getText().toString().trim();
                String str_text = et_text.getText().toString().trim();

                if (TextUtils.isEmpty(str_title) && TextUtils.isEmpty(str_text)) {
                    finish();

                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditNoteActivity.this);
                    alertDialog.setTitle("FBI warning");
                    alertDialog.setMessage("保存？");
                    alertDialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            save();
                        }
                    });
                    alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }
                    return true;
                    default:
                        break;

                }
                return super.onOptionsItemSelected(item);
        }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.menu_edit_note, menu);
            return true;
        }


        @Override
        public boolean onKeyDown ( int keyCode, KeyEvent event){
            String str_title = et_title.getText().toString().trim();
            String str_text = et_text.getText().toString().trim();

            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (TextUtils.isEmpty(str_title) && TextUtils.isEmpty(str_text)) {
                    finish();

                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditNoteActivity.this);

                    alertDialog.setTitle("FBI warning");
                    alertDialog.setMessage("确定退出？");
                    alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            finish();

                        }
                    });
                    alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });

                    alertDialog.show();
                }
            }

            return super.onKeyDown(keyCode, event);
        }
    }
