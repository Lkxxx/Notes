package com.lk.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class SettingsActivity extends ActionBarActivity {


    private Toolbar toolbar;
    private RelativeLayout rl_save,rl_recover,rl_about;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(Color.rgb(15, 15, 15));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rl_save = (RelativeLayout)findViewById(R.id.rl_save);
        rl_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backup();


            }
        });

        rl_recover = (RelativeLayout)findViewById(R.id.rl_recover);
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

        rl_about = (RelativeLayout)findViewById(R.id.rl_about);
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
        /*backUpFile = getDatabasePath("//data//com.lk.notes//databases//notes.db");
        if(backUpFile.exists()){
            FileInputStream inputStream = new FileInputStream(backUpFile);
            File newBackUpFile = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/com.lk.notes/backup/notes.db");
            FileOutputStream outputStream = new FileOutputStream(newBackUpFile);
            if (!newBackUpFile.exists()){
                newBackUpFile.getParentFile().mkdirs();
                newBackUpFile.createNewFile();
                Toast.makeText(SettingsActivity.this,"备份成功", Toast.LENGTH_SHORT).show();

            }else {
                newBackUpFile.delete();
                newBackUpFile.createNewFile();
                Toast.makeText(SettingsActivity.this, "重建备份成功", Toast.LENGTH_SHORT).show();
            }
            outputStream.close();
            inputStream.close();

        }*/
        /*try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data////databases//{database name}";
                String backupDBPath = "{database name}";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }*/
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
