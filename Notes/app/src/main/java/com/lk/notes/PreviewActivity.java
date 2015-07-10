package com.lk.notes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;


public class PreviewActivity extends ActionBarActivity {
    private String title, text, id, clock, time;
    private TextView tv_title, tv_text, tv_time, tv_clock;
    private LinearLayout ll_clock;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        initView();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {toolbar.setElevation(8);}
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_close_white_48dp);
        toolbar.setBackgroundColor(Color.alpha(0));
        ll_clock = (LinearLayout) findViewById(R.id.ll_clock);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_text = (TextView) findViewById(R.id.tv_text);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_clock = (TextView) findViewById(R.id.tv_clock);

        Intent intent = this.getIntent();
        title = intent.getStringExtra("title");
        text = intent.getStringExtra("text");
        time = intent.getStringExtra("time");
        id = intent.getStringExtra("id");
        clock = intent.getStringExtra("clock");
        Log.e("duan","duan");
        tv_title.setText(title);
        tv_text.setText(text);
        tv_time.setText(ConvertTime.convertTime(time, id));
        ll_clock.setVisibility(View.GONE);
        if (clock != null) {
            ll_clock.setVisibility(View.VISIBLE);
            tv_clock.setText(ConvertTime.convertClock(clock));
        }
        Log.e("duan","duan");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.alpha(20));
            window.setNavigationBarColor(Color.argb(51, 0, 0, 0));
        }


        ImageView photo = (ImageView) findViewById(R.id.photo);
        ImageView shadowToolbar = (ImageView) findViewById(R.id.shadowToolbar);
        ImageView gradual = (ImageView) findViewById(R.id.gradual);
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.KITKAT){
            gradual.setVisibility(View.GONE);
        }
        if (new File(Environment.getExternalStorageDirectory() + "/Notes/image/" + id).exists()) {
            new ImageProcessing().imagePreview(photo,this,id);

            toolbar.setBackgroundColor(Color.alpha(0));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(0);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                shadowToolbar.setVisibility(View.VISIBLE);
            }else {
                shadowToolbar.setVisibility(View.GONE);
            }

        } else {
            setcolor();
        }

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory() + "/Notes/image/" + id);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(intent);

            }
        });
        Log.e("duan", "duan");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent().setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                sendIntent.putExtra(Intent.EXTRA_TEXT, title + "\n" + text);
                startActivity(sendIntent.createChooser(sendIntent, title));
                break;
            case R.id.action_edit:
                Intent intent = new Intent();
                intent.putExtra("title", title);
                intent.putExtra("text", text);
                intent.putExtra("id", id);
                intent.putExtra("clock", clock);
                intent.putExtra("time", time);
                intent.putExtra("toolbarheight", toolbar.getHeight());
                intent.setClass(this, NotesChangeActivity.class);
                startActivity(intent);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setcolor() {
        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 172);
        int b = sharedPreferences.getInt("b", 193);
        toolbar.setBackgroundColor(Color.rgb(r, g, b));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));

        }
    }




    public int Dp2Px(Context context, float dp) {

        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

}
