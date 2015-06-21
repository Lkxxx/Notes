package com.lk.notes;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;


public class MessageActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView tv_GitHub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        initView();
    }

    private void  initView(){

        int r = 0;
        int g = 159;
        int b = 175;
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("关于");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(15);
        toolbar.setPopupTheme(R.style.MyToolBar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));

        }
        tv_GitHub = (TextView)findViewById(R.id.tv_GitHub);
        tv_GitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://github.com/Lkxxx/Notes");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });
        final ImageView iv_launcher = (ImageView)findViewById(R.id.iv_launcher);
        final RotateAnimation animation = new RotateAnimation(0,7200, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(100000);
        iv_launcher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                iv_launcher.startAnimation(animation);
                return true;
            }
        });
        iv_launcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_launcher.clearAnimation();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
