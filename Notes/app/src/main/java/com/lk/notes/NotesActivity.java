package com.lk.notes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.notes.Fragment.RemindFragment;
import com.lk.notes.Fragment.LabelFragment;
import com.lk.notes.Fragment.MessageFragment;
import com.lk.notes.Fragment.NotesFragment;
import com.lk.notes.Fragment.SettingFragment;
import com.lk.notes.UI.ScrimInsetsFrameLayout;

import java.util.Timer;
import java.util.TimerTask;


public class NotesActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int Change = 1;
    private static final int PROGREE = 3;
    private static final int ADD = 4;
    private static final int REMIND = 5;
    public static Activity finish;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private FrameLayout sticky_content;
    private RotateAnimation ra;
    private LinearLayout ll_notes, ll_label, ll_remind, ll_theme, ll_setting, ll_message, ll_suggest;
    private DrawerLayout drawerLayout;
    private ImageView iv_fab_shadow;
    private TextView tv_notes, tv_message, tv_label, tv_remind, tv_setting;
    private ImageView iv_notes, iv_message, iv_label, iv_remind, iv_setting;
    private ProgressBar progressBar;
    private long exitTime = 0;
    int d = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        intiview();
        finish = this;
        SharedPreferences sp = getSharedPreferences("isFirstIn", NotesActivity.MODE_PRIVATE);
        boolean isFirstIn = sp.getBoolean("isFirstInWith1.14", true);
        SharedPreferences.Editor editor = sp.edit();
        if (isFirstIn && getApplicationContext().getDatabasePath("notes.db").exists()) {
            Log.e("dbpath", String.valueOf(getDatabasePath("notes.db")));
            startActivity(new Intent(this, FirstInActivity.class));
            finish();
        }
    }

    private void intiview() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sticky_content = (FrameLayout) findViewById(R.id.sticky_content);
        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 172);
        int b = sharedPreferences.getInt("b", 193);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        iv_fab_shadow = (ImageView) findViewById(R.id.iv_fab_shadow);

        ra = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);

        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("简记");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        setSupportActionBar(toolbar);


        toolbar.setPopupTheme(R.style.MyToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ScrimInsetsFrameLayout frameLayout = (ScrimInsetsFrameLayout) findViewById(R.id.frameLayout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawerLayout.setStatusBarBackgroundColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            drawerLayout.setFitsSystemWindows(false);
            frameLayout.setFitsSystemWindows(false);
        }
        final ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.abc_action_bar_home_description, R.string.abc_action_bar_home_description_format);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);

        fab.setColorNormal(Color.rgb(76, 175, 80));
        fab.setColorPressed(Color.rgb((int) (76 * 0.8), (int) (175 * 0.8), (int) (80 * 0.8)));
        fab.setOnClickListener(this);

        ll_notes = (LinearLayout) findViewById(R.id.ll_notes);
        ll_label = (LinearLayout) findViewById(R.id.ll_label);
        ll_remind = (LinearLayout) findViewById(R.id.ll_remind);
        ll_theme = (LinearLayout) findViewById(R.id.ll_theme);
        ll_setting = (LinearLayout) findViewById(R.id.ll_setting);
        ll_message = (LinearLayout) findViewById(R.id.ll_message);
        ll_suggest = (LinearLayout) findViewById(R.id.ll_suggest);

        ll_notes.setOnClickListener(this);
        ll_label.setOnClickListener(this);
        ll_remind.setOnClickListener(this);
        ll_theme.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_suggest.setOnClickListener(this);

        tv_notes = (TextView) findViewById(R.id.tv_notes);
        tv_label = (TextView) findViewById(R.id.tv_label);
        tv_remind = (TextView) findViewById(R.id.tv_remind);
        tv_setting = (TextView) findViewById(R.id.tv_setting);
        tv_message = (TextView) findViewById(R.id.tv_message);
        iv_notes = (ImageView) findViewById(R.id.iv_notes);
        iv_label = (ImageView) findViewById(R.id.iv_label);
        iv_remind = (ImageView) findViewById(R.id.iv_remind);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_message = (ImageView) findViewById(R.id.iv_message);

        addNotes();
        fisrtDrawerBackground();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出应用程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                fab.startAnimation(ra);
                ra.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(NotesActivity.this, EditNoteActivity.class);
                        startActivityForResult(intent, Change);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                break;
            case R.id.ll_none:
                Intent intent = new Intent(NotesActivity.this, EditNoteActivity.class);
                startActivityForResult(intent, Change);
                break;
            case R.id.ll_notes:
                if (d == 0) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    d = 0;
                    addNotes();
                    setDrawerBackground(ll_notes, tv_notes, iv_notes);
                }
                break;
            case R.id.ll_label:
                if (d == 1) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    LabelFragment labelFragment = new LabelFragment();
                    labelFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.sticky_content, labelFragment).commit();
                    d = 1;
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    iv_fab_shadow.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                    toolbar.setTitle("标签");
                    setDrawerBackground(ll_label, tv_label, iv_label);

                }
                break;
            case R.id.ll_remind:
                if (d == 2) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    sticky_content.removeAllViews();
                    progressBar.setVisibility(View.VISIBLE);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = PROGREE;
                            message.arg1 = REMIND;
                            handler.sendMessage(message);

                        }
                    }, 400);
                    d = 2;
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    iv_fab_shadow.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                    toolbar.setTitle("提醒");
                    setDrawerBackground(ll_remind, tv_remind, iv_remind);
                }
                break;
            case R.id.ll_theme:
                Intent intent3 = new Intent(NotesActivity.this, ThemeActivity.class);
                startActivity(intent3);
                finish();
                break;
            case R.id.ll_setting:
                if (d == 3) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    d = 3;
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    SettingFragment settingFragment = new SettingFragment();
                    settingFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.sticky_content, settingFragment).commit();
                    iv_fab_shadow.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                    toolbar.setTitle("设置");
                    setDrawerBackground(ll_setting, tv_setting, iv_setting);
                }
                break;
            case R.id.ll_message:
                if (d == 4) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    d = 4;
                    MessageFragment messageFragment = new MessageFragment();
                    messageFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.sticky_content, messageFragment).commit();
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    fab.setVisibility(View.GONE);
                    iv_fab_shadow.setVisibility(View.GONE);
                    toolbar.setTitle("关于");
                    setDrawerBackground(ll_message, tv_message, iv_message);
                }
                break;
            case R.id.ll_suggest:
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:luoshuidao@gmail.com"));
                startActivity(data);
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PROGREE) {
                if (msg.arg1 == ADD) {
                    NotesFragment notesFragment = new NotesFragment();
                    notesFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.sticky_content, notesFragment).commit();
                    progressBar.setVisibility(View.GONE);
                } else if (msg.arg1 == REMIND) {
                    RemindFragment remindFragment = new RemindFragment();
                    remindFragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().replace(R.id.sticky_content, remindFragment).commit();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    };

    private void addNotes() {
        sticky_content.removeAllViews();
        drawerLayout.closeDrawer(Gravity.LEFT);
        progressBar.setVisibility(View.VISIBLE);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = PROGREE;
                message.arg1 = ADD;
                handler.sendMessage(message);

            }
        }, 400);

        fab.setVisibility(View.VISIBLE);
        iv_fab_shadow.setVisibility(View.VISIBLE);
        toolbar.setTitle("笔记");
    }

    public void setDrawerBackground(LinearLayout ll, TextView textView, ImageView imageView) {
        ll_notes.setBackgroundResource(R.drawable.button_transparent);
        ll_label.setBackgroundResource(R.drawable.button_transparent);
        ll_remind.setBackgroundResource(R.drawable.button_transparent);
        ll_setting.setBackgroundResource(R.drawable.button_transparent);
        ll_message.setBackgroundResource(R.drawable.button_transparent);

        tv_notes.setTextColor(getResources().getColor(R.color.drawableTextColor));
        tv_label.setTextColor(getResources().getColor(R.color.drawableTextColor));
        tv_remind.setTextColor(getResources().getColor(R.color.drawableTextColor));
        tv_setting.setTextColor(getResources().getColor(R.color.drawableTextColor));
        tv_message.setTextColor(getResources().getColor(R.color.drawableTextColor));
        iv_notes.setImageResource(R.drawable.selector_ic_view_list);
        iv_label.setImageResource(R.drawable.selector_ic_label);
        iv_remind.setImageResource(R.drawable.selector_ic_clock);
        iv_setting.setImageResource(R.drawable.selector_ic_setting);
        iv_message.setImageResource(R.drawable.selector_ic_message);

        switch (d) {
            case 0:
                iv_notes.setImageResource(R.mipmap.ic_view_list_grey600_48dp_press);
                break;
            case 1:
                iv_label.setImageResource(R.mipmap.ic_label_grey600_48dp_press);
                break;
            case 2:
                iv_remind.setImageResource(R.mipmap.ic_clock_grey600_48dp_press);
                break;
            case 3:
                iv_setting.setImageResource(R.mipmap.ic_settings_grey600_48dp_press);
                break;
            case 4:
                iv_message.setImageResource(R.mipmap.ic_information_outline_grey600_48dp_press);
                break;

        }
        textView.setTextColor(Color.parseColor("#FF00ACC1"));
        ll.setBackgroundColor(Color.parseColor("#13000000"));
    }

    private void fisrtDrawerBackground() {
        iv_notes.setImageResource(R.mipmap.ic_view_list_grey600_48dp_press);
        tv_notes.setTextColor(Color.parseColor("#FF00ACC1"));
        ll_notes.setBackgroundColor(Color.parseColor("#13000000"));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Change) {
            Log.e("result", "Change");
            if (resultCode != 10000) {
                Log.e("result", "10000");

            }
        }
    }

}
