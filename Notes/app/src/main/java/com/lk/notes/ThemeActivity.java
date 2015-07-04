package com.lk.notes;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;

import com.lk.notes.UI.ColorPickView;
import com.lk.notes.anim.ScaleAnim;


public class ThemeActivity extends ActionBarActivity implements View.OnClickListener, View.OnLongClickListener {


    private Toolbar toolbar;
    private AlertDialog dialog;
    private Button bt_black, bt_red, bt_orange, bt_yellow, bt_charttreuse, bt_lime, bt_springgreen, bt_aquamarine, bt_cyan, bt_royalblue, bt_darkviolet, bt_magenta, bt_mediumvioletred, bt_restore, bt_colorCustom;
    private ColorPickView colorPickView;
    private View iview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        view();
    }

    private void view() {
        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("主题");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));
        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 172);
        int b = sharedPreferences.getInt("b", 193);
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));


        }
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bt_red = (Button) findViewById(R.id.bt_red);
        bt_orange = (Button) findViewById(R.id.bt_orange);
        bt_yellow = (Button) findViewById(R.id.bt_yellow);
        bt_charttreuse = (Button) findViewById(R.id.bt_charttreuse);
        bt_lime = (Button) findViewById(R.id.bt_lime);
        bt_springgreen = (Button) findViewById(R.id.bt_springgreen);
        bt_aquamarine = (Button) findViewById(R.id.bt_aquamarine);
        bt_cyan = (Button) findViewById(R.id.bt_cyan);
        bt_royalblue = (Button) findViewById(R.id.bt_royalblue);
        bt_darkviolet = (Button) findViewById(R.id.bt_darkviolet);
        bt_magenta = (Button) findViewById(R.id.bt_magenta);
        bt_mediumvioletred = (Button) findViewById(R.id.bt_mediumvioletred);
        bt_restore = (Button) findViewById(R.id.bt_restore);
        bt_colorCustom = (Button) findViewById(R.id.bt_colorCustom);
        bt_black = (Button) findViewById(R.id.bt_black);

        iview = (View) findViewById(R.id.view);
        bt_red.setOnClickListener(this);
        bt_orange.setOnClickListener(this);
        bt_yellow.setOnClickListener(this);
        bt_charttreuse.setOnClickListener(this);
        bt_lime.setOnClickListener(this);
        bt_springgreen.setOnClickListener(this);
        bt_aquamarine.setOnClickListener(this);
        bt_cyan.setOnClickListener(this);
        bt_royalblue.setOnClickListener(this);
        bt_darkviolet.setOnClickListener(this);
        bt_magenta.setOnClickListener(this);
        bt_mediumvioletred.setOnClickListener(this);
        bt_restore.setOnClickListener(this);
        bt_colorCustom.setOnClickListener(this);
        bt_black.setOnClickListener(this);
        bt_red.setOnLongClickListener(this);
        bt_orange.setOnLongClickListener(this);
        bt_yellow.setOnLongClickListener(this);
        bt_charttreuse.setOnLongClickListener(this);
        bt_lime.setOnLongClickListener(this);
        bt_springgreen.setOnLongClickListener(this);
        bt_aquamarine.setOnLongClickListener(this);
        bt_cyan.setOnLongClickListener(this);
        bt_royalblue.setOnLongClickListener(this);
        bt_darkviolet.setOnLongClickListener(this);
        bt_magenta.setOnLongClickListener(this);
        bt_mediumvioletred.setOnLongClickListener(this);
        bt_restore.setOnLongClickListener(this);
        bt_colorCustom.setOnLongClickListener(this);
        bt_black.setOnLongClickListener(this);

    }


    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.bt_red:
                color(255, 0, 0);
                break;
            case R.id.bt_orange:
                color(255, 128, 0);
                break;
            case R.id.bt_yellow:
                color(255, 225, 0);
                break;
            case R.id.bt_charttreuse:
                color(128, 255, 0);
                break;
            case R.id.bt_lime:
                color(0, 255, 0);
                break;
            case R.id.bt_springgreen:
                color(0, 255, 128);
                break;
            case R.id.bt_aquamarine:
                color(102, 255, 179);
                break;
            case R.id.bt_cyan:
                color(0, 255, 255);
                break;
            case R.id.bt_royalblue:
                color(0, 127, 255);
                break;
            case R.id.bt_darkviolet:
                color(127, 0, 255);
                break;
            case R.id.bt_magenta:
                color(255, 0, 255);
                break;
            case R.id.bt_mediumvioletred:
                color(255, 0, 128);
                break;
            case R.id.bt_black:
                color(15, 15, 15);
                break;
            case R.id.bt_restore:

                int[] location = new int[2];
                bt_restore.getLocationOnScreen(location);


                iview.setVisibility(View.VISIBLE);
                //ScaleAnimation sa = new ScaleAnimation(1, 30, 1, 30, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                ScaleAnim sa = new ScaleAnim();
                sa.setDuration(500);


                sa.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        iview.setVisibility(View.GONE);
                        color(0, 172, 193);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                iview.startAnimation(sa);
                break;
            case R.id.bt_colorCustom:
                colorCustom();
                break;


        }
    }


    private int rgbR, rgbG, rgbB;

    private void colorCustom() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_custom, null);

        colorPickView = (ColorPickView) view.findViewById(R.id.color_picker_view);
        colorPickView.setOnColorChangedListener(new ColorPickView.OnColorChangedListener() {
            @Override
            public void onColorChange(int color) {
                rgbR = (color & 0xff0000) >> 16;
                rgbG = (color & 0x00ff00) >> 8;
                rgbB = (color & 0x0000ff);
                Window window = getWindow();
                toolbar.setBackgroundColor(Color.rgb(rgbR, rgbG, rgbB));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(Color.rgb((int) (rgbR * 0.9), (int) (rgbG * 0.9), (int) (rgbB * 0.9)));

                }

            }
        });

        Button dialog_buttoncancel = (Button) view.findViewById(R.id.dialog_buttoncancel);
        Button dialog_buttonconfirm = (Button) view.findViewById(R.id.dialog_buttonconfirm);


        dialog_buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color(rgbR, rgbG, rgbB);
                dialog.dismiss();
            }
        });

        dialog_buttoncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        builder.setView(view);

        dialog = builder.show();


    }

    public void color(int r, int g, int b) {
        Window window = getWindow();
        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));
            window.setNavigationBarColor(Color.rgb(r, g, b));
        }
        editor.putInt("r", r);
        editor.putInt("g", g);
        editor.putInt("b", b);
        editor.commit();

    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NotesActivity.finish.finish();
                Intent intent = new Intent(this, NotesActivity.class);
                startActivity(intent);

                finish();
                return true;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {


                Intent intent = new Intent(this, NotesActivity.class);
                startActivity(intent);

                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.bt_red:
                waveColor(255, 0, 0, 255, 74, 74);
                break;
            case R.id.bt_orange:
                waveColor(255, 128, 0, 255, 165, 74);
                break;
            case R.id.bt_yellow:
                waveColor(255, 225, 0, 255, 234, 74);
                break;
            case R.id.bt_charttreuse:
                waveColor(128, 255, 0, 165, 255, 74);
                break;
            case R.id.bt_lime:
                waveColor(0, 255, 0, 74, 255, 74);
                break;
            case R.id.bt_springgreen:
                waveColor(0, 255, 128, 74, 255, 165);
                break;
            case R.id.bt_aquamarine:
                waveColor(102, 255, 179, 146, 255, 201);
                break;
            case R.id.bt_cyan:
                waveColor(0, 255, 255, 74, 255, 255);
                break;
            case R.id.bt_royalblue:
                waveColor(0, 127, 255, 74, 164, 255);
                break;
            case R.id.bt_darkviolet:
                waveColor(127, 0, 255, 164, 74, 255);
                break;
            case R.id.bt_magenta:
                waveColor(255, 0, 255, 255, 74, 255);
                break;
            case R.id.bt_mediumvioletred:
                waveColor(255, 0, 128, 255, 74, 165);
                break;
            case R.id.bt_black:
                waveColor(15, 15, 15, 85, 85, 85);
                break;
            case R.id.bt_restore:
                waveColor(0, 159, 175, 74, 187, 198);
                break;
            case R.id.bt_colorCustom:
                colorCustom();
                break;

        }
        return false;
    }

    public void waveColor(int r, int g, int b, int r1, int g1, int b1) {
        Intent intent = new Intent();
        intent.putExtra("r", r);
        intent.putExtra("g", g);
        intent.putExtra("b", b);
        intent.putExtra("r1", r1);
        intent.putExtra("g1", g1);
        intent.putExtra("b1", b1);
        intent.setClass(ThemeActivity.this, FirstInActivity.class);
        startActivity(intent);
    }
}
