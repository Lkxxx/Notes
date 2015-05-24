package com.lk.notes;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ThemeActivity extends ActionBarActivity implements View.OnClickListener {


    private Toolbar toolbar;
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        view();
    }

    private void view() {

        toolbar = (Toolbar)findViewById(R.id.tl_custom);
        toolbar.setTitle("Notes");
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
        Button bt_red = (Button) findViewById(R.id.bt_red);
        Button bt_orange = (Button) findViewById(R.id.bt_orange);
        Button bt_yellow = (Button) findViewById(R.id.bt_yellow);
        Button bt_charttreuse = (Button) findViewById(R.id.bt_charttreuse);
        Button bt_lime = (Button) findViewById(R.id.bt_lime);
        Button bt_springgreen = (Button) findViewById(R.id.bt_springgreen);
        Button bt_aquamarine = (Button) findViewById(R.id.bt_aquamarine);
        Button bt_cyan = (Button) findViewById(R.id.bt_cyan);
        Button bt_royalblue = (Button) findViewById(R.id.bt_royalblue);
        Button bt_darkviolet = (Button) findViewById(R.id.bt_darkviolet);
        Button bt_magenta = (Button) findViewById(R.id.bt_magenta);
        Button bt_mediumvioletred = (Button) findViewById(R.id.bt_mediumvioletred);
        Button bt_restore = (Button)findViewById(R.id.bt_restore);
        Button bt_colorCustom = (Button)findViewById(R.id.bt_colorCustom);
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
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_red:
                color(255, 0, 0);
                break;
            case R.id.bt_orange:
                color(255,128,0);
                break;
            case R.id.bt_yellow:
                color(255,225,0);
                break;
            case R.id.bt_charttreuse:
                color(128,255,0);
                break;
            case R.id.bt_lime:
                color(0,255,0);
                break;
            case R.id.bt_springgreen:
                color(0,255,128);
                break;
            case R.id.bt_aquamarine:
                color(102,255,179);
                break;
            case R.id.bt_cyan:
                color(0,255,255);
                break;
            case R.id.bt_royalblue:
                color(0,127,255);
                break;
            case R.id.bt_darkviolet:
                color(127,0,255);
                break;
            case R.id.bt_magenta:
                color(255,0,255);
                break;
            case R.id.bt_mediumvioletred:
                color(255,0,128);
                break;
            case R.id.bt_restore:
                color(0,159,175);
                break;
            case R.id.bt_colorCustom:
                colorCustom();
                break;



        }
    }

    private void colorCustom() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_custom, null);


        Button dialog_buttoncancel = (Button) view.findViewById(R.id.dialog_buttoncancel);
        Button dialog_buttonconfirm = (Button) view.findViewById(R.id.dialog_buttonconfirm);

        final EditText dialog_text_R = (EditText) view.findViewById(R.id.dialog_text_R);
        final EditText dialog_text_G = (EditText)view.findViewById(R.id.dialog_text_G);
        final EditText dialog_text_B = (EditText) view.findViewById(R.id.dialog_text_B);
        dialog_buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strR = dialog_text_R.getText().toString().trim();
                String strG = dialog_text_G.getText().toString().trim();
                String strB = dialog_text_B.getText().toString().trim();


                if(!(TextUtils.isEmpty(strR)||TextUtils.isEmpty(strR)||TextUtils.isEmpty(strR))) {
                    int rgbR = Integer.parseInt(strR);
                    int rgbG = Integer.parseInt(strG);
                    int rgbB = Integer.parseInt(strB);
                    if (rgbR >= 0 && rgbR <= 255 && rgbG >= 0 && rgbG <= 255 && rgbB >= 0 && rgbB <= 255) {
                        Log.e("COLOR", String.valueOf(rgbR) + String.valueOf(rgbG) + String.valueOf(rgbB));
                        color(rgbR, rgbG, rgbB);

                    } else {
                        Toast.makeText(ThemeActivity.this, "请输入0-255的值", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ThemeActivity.this, "请输入0-255的值", Toast.LENGTH_SHORT).show();
                }
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

    public void color(int r,int g,int b){
        Window window = getWindow();
        SharedPreferences sharedPreferences = getSharedPreferences("color",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));
        editor.putInt("r", r);
        editor.putInt("g",g);
        editor.putInt("b", b);
        editor.commit();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this,NotesActivity.class);
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
                Intent intent = new Intent(this,NotesActivity.class);
                startActivity(intent);

                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
