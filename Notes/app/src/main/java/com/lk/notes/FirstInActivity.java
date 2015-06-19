package com.lk.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.lk.notes.anim.WaveAnim;
import com.lk.notes.anim.WaveUpAnim;
import com.lk.notes.waveview.WaveView;

import java.io.File;
import java.io.IOException;


public class FirstInActivity extends ActionBarActivity {
    private static final int FIRSTIN = 1;
    private static final int WAVE = 2;
    private double TIME = 20;


    private WaveView waveView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FIRSTIN) {
                startActivity(new Intent(FirstInActivity.this, NotesActivity.class));

            } else if (msg.what == WAVE) {
                TIME = TIME + 5;
                waveView.clearAnimation();
                Log.e("run", "progress" + TIME % 100);
                if (TIME < 80) {

                }

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_in);
        SharedPreferences sp = getSharedPreferences("isFirstIn", NotesActivity.MODE_PRIVATE);
        boolean isFirstIn = sp.getBoolean("isFirstInWith1.14", true);
        final SharedPreferences.Editor editor = sp.edit();
        if (isFirstIn) {

            thread.start();
            editor.putBoolean("isFirstInWith1.14", false);
            editor.commit();
        } else {
            initView();
        }
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                deleteOldData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            re();
            new File(Environment.getExternalStorageDirectory() + "/Notes/backup/firstIn.db").delete();
            Message msg = new Message();
            msg.what = FIRSTIN;
            handler.sendMessage(msg);

        }
    });

    private void initView() {


        Intent intent = this.getIntent();
        int  r = intent.getIntExtra("r", 0);
        int  g = intent.getIntExtra("g", 159);
        int  b = intent.getIntExtra("b", 175);
        int  r1 = intent.getIntExtra("r1", 74);
        int  g1 = intent.getIntExtra("g1", 187);
        int  b1 = intent.getIntExtra("b1", 198);


        final RelativeLayout l = (RelativeLayout) findViewById(R.id.ll_firstin);
        waveView = (WaveView) findViewById(R.id.wave_view);
        waveView.setProgress(15);
        l.setBackgroundColor(Color.rgb(r1, g1, b1));
        waveView.setBackgroundColor(Color.rgb(r, g, b));

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        //final TranslateAnimation sa = new TranslateAnimation(0, 0,0, (float) (-0.6*localDisplayMetrics.heightPixels));
        final WaveAnim w1 = new WaveAnim((float) (0.2 * localDisplayMetrics.heightPixels));
        final WaveAnim w2 = new WaveAnim((float) (0.35 * localDisplayMetrics.heightPixels));
        final WaveAnim w3 = new WaveAnim((float) (0.55 * localDisplayMetrics.heightPixels));
        final WaveAnim w4 = new WaveAnim((float) (0.8 * localDisplayMetrics.heightPixels));
        final WaveAnim w5 = new WaveAnim((float) (1.1 * localDisplayMetrics.heightPixels));
        final WaveUpAnim w6 = new WaveUpAnim((float) (1.5 * localDisplayMetrics.heightPixels));
        w1.setDuration(2500);
        w2.setDuration(2500);
        w3.setDuration(2500);
        w4.setDuration(2500);
        w5.setDuration(2500);
        w6.setDuration(5000);


        waveView.startAnimation(w1);

        w1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                waveView.clearAnimation();
                waveView.startAnimation(w2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        w2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                waveView.clearAnimation();
                waveView.startAnimation(w3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        w3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                waveView.clearAnimation();
                waveView.startAnimation(w4);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        w4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                waveView.clearAnimation();
                waveView.startAnimation(w5);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        w5.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                waveView.clearAnimation();
                waveView.startAnimation(w6);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        w6.setFillAfter(true);
        w6.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public void re() {
        String title, text, time, id;
        NotesDao dao = new NotesDao(this);
        SQLiteDatabase database = op();
        Cursor c = database.query("notes", new String[]{"title", "text", "time", "id"}, null, null, null, null, "id");
        while (c.moveToNext()) {
            title = c.getString(0);
            text = c.getString(1);
            time = c.getString(2);
            id = c.getString(3);

            if (idExist(id)) {
                dao.add(title, text, time, id, null, null);
            }
        }
        c.close();
        database.close();
    }

    File Path = new File(Environment.getExternalStorageDirectory() + "/Notes/backup/firstIn.db");

    public SQLiteDatabase op() {

        Log.e("path", Environment.getExternalStorageDirectory() + "/Notes/backup/firstIn.db");

        if (Path.exists()) {
            return SQLiteDatabase.openOrCreateDatabase(Path, null);
        } else {
            return SQLiteDatabase.openOrCreateDatabase(Path, null);
        }
    }

    public boolean idExist(String id) {
        SQLiteDatabase database1 = new NotesOpenHelper(this).getReadableDatabase();
        Cursor cursor = database1.query("notes", null, "id = ?", new String[]{id}, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();
            database1.close();
            Log.e("exist", "ture");
            return false;
        } else {
            cursor.close();
            database1.close();
            Log.e("exist", "false");
            return true;
        }

    }

    public void deleteOldData() throws IOException {
        File dbFile = getApplicationContext().getDatabasePath("notes.db");
        new BackupTask(this).fileCopy(dbFile, Path);
        dbFile.delete();

    }


}
