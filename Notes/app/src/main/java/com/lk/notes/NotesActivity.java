package com.lk.notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.notes.PullToZoom.PullToZoomListViewEx;
import com.lk.notes.UI.ScrimInsetsFrameLayout;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NotesActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int Edit = 1;
    private static final int Change = 1;
    private static final int Refresh = 2;
    static Activity finish;

    private FloatingActionButton fab;
    private PullToZoomListViewEx list_view;
    private Toolbar toolbar;
    private NotesDao dao;
    private NotesAdapter adapter;
    private List<NotesInfo> mNotesInfos;
    private String id;
    private RotateAnimation ra;
    private LinearLayout ll_none, ll_notes, ll_label, ll_remind, ll_theme, ll_setting, ll_message, ll_suggest;
    private DrawerLayout drawerLayout;
    private ImageView iv_notes;
    private TextView tv_notes;
    private long exitTime = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Refresh) {
                ImageView imageView = (ImageView) list_view.getZoomView();
                if (getDataLastPath() != null) {
                    imageView.setImageBitmap(BitmapFactory.decodeFile(getDataLastPath()));
                    list_view.setHeaderLayoutParams(localObject(getDataLastPath()));
                }

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        intiview();
        finish = this;
        SharedPreferences sp = getSharedPreferences("isFirstIn", NotesActivity.MODE_PRIVATE);
        boolean isFirstIn = sp.getBoolean("isFirstInWith1.14", true);
        SharedPreferences.Editor editor = sp.edit();
        if (isFirstIn) {
            Log.e("dbpath", String.valueOf(getDatabasePath("notes.db")));
            startActivity(new Intent(this, FirstInActivity.class));
            finish();
        } else {
            initData();
        }


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context, menu);

    }


    private void initData() {
        dao = new NotesDao(this);
        new Thread() {
            public void run() {
                mNotesInfos = dao.findNotes();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new NotesAdapter(NotesActivity.this, mNotesInfos, dao);
                        list_view.setAdapter(adapter);

                    }
                });
            }
        }.start();

    }

    @Override
    protected void onRestart() {
        adapter.notifyDataSetChanged();
        super.onRestart();

    }

    private void intiview() {
        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 172);
        int b = sharedPreferences.getInt("b", 193);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        ra = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);

        ll_none = (LinearLayout) findViewById(R.id.ll_none);
        if (getNone()) {
            ll_none.setVisibility(View.VISIBLE);
        } else {
            ll_none.setVisibility(View.GONE);
        }
        ll_none.setOnClickListener(this);

        list_view = (PullToZoomListViewEx) findViewById(R.id.listview);
        if (getDataLastPath() != null) {
            list_view.setHeaderLayoutParams(localObject(getDataLastPath()));
        }


        list_view.setVerticalScrollBarEnabled(true);
        registerForContextMenu(list_view.getPullRootView());
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NotesInfo notesInfo = null;
                if (i >= 1) {
                    notesInfo = mNotesInfos.get(i - 1);
                } else if (i == 0) {
                    notesInfo = mNotesInfos.get(i);
                }
                Intent intent = new Intent();
                intent.putExtra("title", notesInfo.getTitle());
                intent.putExtra("text", notesInfo.getText());
                intent.putExtra("id", notesInfo.getId());
                intent.putExtra("clock", notesInfo.getClock());
                Log.e("id", notesInfo.getId());
                intent.setClass(NotesActivity.this, NotesChangeActivity.class);
                startActivityForResult(intent, Edit);

            }
        });

        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("简记");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(15);
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

        ll_notes.setOnClickListener(this);
        ll_label.setOnClickListener(this);
        ll_remind.setOnClickListener(this);
        ll_theme.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_message.setOnClickListener(this);
        ll_notes.setPressed(true);

iv_notes = (ImageView)findViewById(R.id.iv_notes);


    }


    private AbsListView.LayoutParams localObject(String path) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenWidth = localDisplayMetrics.widthPixels;
        AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (0.1F * (mScreenWidth / 16.0F)));
        if (path != null) {
            if (new File(path).exists()) {
                localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
            } else {
                localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (0.1F * (mScreenWidth / 16.0F)));
            }
        }
        return localObject;
    }

    public String getDataLastPath() {
        String path = null;
        NotesOpenHelper notesOpenHelper;
        notesOpenHelper = new NotesOpenHelper(this);
        SQLiteDatabase db = notesOpenHelper.getReadableDatabase();
        Cursor c = db.query("notes", new String[]{"title", "text", "time", "id"}, null, null, null, null, "_id DESC");
        if (c.moveToFirst()) {
            path = Environment.getExternalStorageDirectory() + "/Notes/image/" + c.getString(3);
        }
        c.close();
        db.close();
        return path;
    }

    public boolean getNone() {
        boolean none = true;
        NotesOpenHelper notesOpenHelper;
        notesOpenHelper = new NotesOpenHelper(this);
        SQLiteDatabase db = notesOpenHelper.getReadableDatabase();
        Cursor c = db.query("notes", new String[]{"title", "text", "time", "id"}, null, null, null, null, "_id DESC");
        if (c.getCount() != 0) {
            none = false;
        }
        c.close();
        db.close();
        return none;
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
    public boolean onContextItemSelected(MenuItem item) {

        final int selectedPosition = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        int i = 0;
        if (selectedPosition >= 1) {
            i = selectedPosition - 1;
        } else if (selectedPosition == 0) {
            i = selectedPosition;
        }
        final NotesInfo notesInfo = mNotesInfos.get(i);
        id = notesInfo.getId();
        switch (item.getItemId()) {
            case R.id.menuEdit:
                Intent intent = new Intent();
                intent.putExtra("title", notesInfo.getTitle());
                intent.putExtra("text", notesInfo.getText());
                intent.putExtra("id", id);
                intent.putExtra("clock", notesInfo.getClock());
                Log.e("tag", id);
                intent.setClass(NotesActivity.this, NotesChangeActivity.class);
                startActivityForResult(intent, Change);
                break;
            case R.id.menuDelete:
                menuDelete(notesInfo, selectedPosition);
                break;
            case R.id.menuShare:
                Intent sendIntent = new Intent().setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, notesInfo.getTitle());
                sendIntent.putExtra(Intent.EXTRA_TEXT, notesInfo.getText());
                startActivity(sendIntent.createChooser(sendIntent, notesInfo.getTitle()));
        }
        return super.onOptionsItemSelected(item);


    }

    private void menuDelete(final NotesInfo notesInfo, final int selectedPosition) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NotesActivity.this);
        alertDialog.setTitle("提示");
        alertDialog.setMessage("你确定要删除吗");
        alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File f = new File(Environment.getExternalStorageDirectory() + "/Notes/image/" + id);
                dao.delete(id);
                Toast.makeText(NotesActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                mNotesInfos.remove(notesInfo);
                adapter.notifyDataSetChanged();
                if (getNone()) {
                    ll_none.setVisibility(View.VISIBLE);
                } else {
                    ll_none.setVisibility(View.GONE);
                }
                if (f.exists()) {
                    f.delete();
                    ImageView imageView = (ImageView) list_view.getZoomView();
                    if (selectedPosition == 0 || selectedPosition == 1) {
                        imageView.setImageBitmap(BitmapFactory.decodeFile(getDataLastPath()));
                        list_view.setHeaderLayoutParams(localObject(getDataLastPath()));
                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Change) {
            Log.e("result", "Change");
            if (resultCode != 10000) {
                Log.e("result", "10000");
                setRefresh();
            }
        }
    }


    private void setRefresh() {
        ImageView imageView = (ImageView) list_view.getZoomView();
        if (getDataLastPath() != null) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(getDataLastPath()));
            list_view.setHeaderLayoutParams(localObject(getDataLastPath()));
            initData();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = Refresh;
                    handler.sendMessage(message);
                }
            }, 1000);
            if (getNone()) {
                ll_none.setVisibility(View.VISIBLE);
            } else {
                ll_none.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes, menu);
        return true;
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
                ll_notes.setSelected(true);
                ll_notes.setPressed(true);
                drawerLayout.closeDrawer(Gravity.START);

                break;
            case R.id.ll_label:
                ll_label.setSelected(true);
                ll_label.setPressed(true);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.ll_remind:
                ll_remind.setSelected(true);
                ll_remind.setPressed(true);
                drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.ll_theme:
                Intent intent3 = new Intent(NotesActivity.this, ThemeActivity.class);
                startActivity(intent3);
                finish();
                break;
            case R.id.ll_setting:
                Intent intent4 = new Intent(NotesActivity.this, SettingActivity.class);
                startActivityForResult(intent4, Change);
                break;
            case R.id.ll_message:
                Intent intent5 = new Intent(NotesActivity.this, MessageActivity.class);
                startActivity(intent5);
                break;

        }
    }
}
