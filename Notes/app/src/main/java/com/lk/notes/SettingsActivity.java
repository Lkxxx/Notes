package com.lk.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class SettingsActivity extends ActionBarActivity {


    private Toolbar toolbar;
    private RelativeLayout rl_save, rl_recover, rl_about;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));

        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 159);
        int b = sharedPreferences.getInt("b", 175);
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));
        window.setNavigationBarColor(Color.rgb(r, g, b));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(15);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        rl_save = (RelativeLayout) findViewById(R.id.rl_save);
        rl_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backup();


            }
        });

        rl_recover = (RelativeLayout) findViewById(R.id.rl_recover);
        rl_recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);

                alertDialog.setTitle("警告");
                alertDialog.setMessage("还原备份将覆盖当前的笔记，确认继续？");
                alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        recover();

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
        });

        rl_about = (RelativeLayout) findViewById(R.id.rl_about);
        rl_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);

                alertDialog.setTitle("关于");
                alertDialog.setMessage("这是一个开始。成长离不开开源的支持，感谢每个人!");
                alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        return;

                    }
                });


                alertDialog.show();
            }
        });
    }

    private void recover() {
        new BackupTask(this).execute("restroeDatabase");
        Toast.makeText(SettingsActivity.this, "还原备份成功", Toast.LENGTH_SHORT).show();
    }


    private void backup() {
        new BackupTask(this).execute("backupDatabase");
        Toast.makeText(SettingsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();

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


}
