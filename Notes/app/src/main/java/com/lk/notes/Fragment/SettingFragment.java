package com.lk.notes.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lk.notes.BackupTask;
import com.lk.notes.NotesDao;
import com.lk.notes.NotesOpenHelper;
import com.lk.notes.R;
import com.lk.notes.ThemeActivity;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class SettingFragment extends Fragment implements View.OnClickListener {
    private LinearLayout ll_backup, ll_recover, ll_theme, ll_suggest;
    private ProgressDialog progressDialog;
    private static final int RECOVER = 3;
    String dbFilePath = Environment.getExternalStorageDirectory() + "/Notes/backup/notes.db";
    private View view;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == RECOVER) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "还原成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_setting, container, false);


       LinearLayout ll_Setting = (LinearLayout) view.findViewById(R.id.ll_setting);
        TranslateAnimation translateAnimation = new TranslateAnimation(-50,0,1000,0);
        translateAnimation.setDuration(300);
        ll_Setting.setAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                initView();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return view;
    }

    private void initView() {


        ll_backup = (LinearLayout) view.findViewById(R.id.ll_backup);
        ll_recover = (LinearLayout) view.findViewById(R.id.ll_recover);
        ll_theme = (LinearLayout) view.findViewById(R.id.ll_theme);
        ll_suggest = (LinearLayout) view.findViewById(R.id.ll_suggest);
        ll_backup.setOnClickListener(this);
        ll_recover.setOnClickListener(this);
        ll_theme.setOnClickListener(this);
        ll_suggest.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_backup:
                backup();
                break;
            case R.id.ll_recover:
                recover();
                getActivity().setResult(100000);
                break;
            case R.id.ll_theme:
                Intent intent = new Intent(getActivity(), ThemeActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_suggest:
                suggest();
        }
    }

    private void suggest() {

        Uri uri = Uri.parse("market://details?id="+getActivity().getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void backup() {
        new BackupTask(getActivity()).execute("backupDatabase");
        Toast.makeText(getActivity(), "备份成功", Toast.LENGTH_SHORT).show();

    }

    private void recover() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("提示");
        alertDialog.setMessage("确认还原？");
        alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (new File(dbFilePath).exists()) {
                    progressDialog = ProgressDialog.show(getActivity(), "", "正在还原", true, false);
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
                    Toast.makeText(getActivity(), "没有发现备份文件", Toast.LENGTH_SHORT).show();
                }
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
        NotesDao dao = new NotesDao(getActivity());
        SQLiteDatabase database = op(getActivity().getApplicationContext());
        Cursor c = database.query("notes", new String[]{"title", "text", "time", "id", "year", "clock"}, null, null, null, null, "id");
        while (c.moveToNext()) {
            title = c.getString(0);
            text = c.getString(1);
            time = c.getString(2);
            id = c.getString(3);
            year = c.getString(4);
            clock = c.getString(5);
            if (idNotExist(id)) {
                dao.add(title, text, time, id, year, null);
            }

        }
        c.close();
        database.close();
    }

    public boolean idNotExist(String id) {
        SQLiteDatabase database1 = new NotesOpenHelper(getActivity()).getReadableDatabase();
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
