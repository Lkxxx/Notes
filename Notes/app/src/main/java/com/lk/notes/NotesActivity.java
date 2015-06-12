package com.lk.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.notes.PullToZoom.PullToZoomListViewEx;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NotesActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int Edit = 1;
    private static final int Change = 1;
    private static final int Refresh = 2;

    private FloatingActionButton fab;
    private PullToZoomListViewEx list_view;
    private Toolbar toolbar;
    private NotesDao dao;
    private NotesAdapter adapter;
    private List<NotesInfo> mNotesInfos;
    private String id;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView id_textView01_Menu, id_textView02_Menu, id_textView03_Menu;
    private RotateAnimation ra;


    private long exitTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        intiview();
        initData();


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

            ;
        }.start();

    }

    @Override
    protected void onRestart() {
        adapter.notifyDataSetChanged();

        super.onRestart();

    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    private void intiview() {

        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 159);
        int b = sharedPreferences.getInt("b", 175);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        ra = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);

        list_view = (PullToZoomListViewEx) findViewById(R.id.listview);
        if (getDataLastPath()!= null){
        list_view.setHeaderLayoutParams(localObject(getDataLastPath()));}
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
                Log.e("id", notesInfo.getId());
                intent.setClass(NotesActivity.this, NotesChangeActivity.class);
                startActivityForResult(intent, Edit);

            }
        });

        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("Notes");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));
        window.setNavigationBarColor(Color.rgb(r, g, b));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(15);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        LinearLayout ll_dl_iv = (LinearLayout) findViewById(R.id.ll_dl_iv);
        ll_dl_iv.setBackgroundColor(Color.rgb(r, g, b));


        fab.setOnClickListener(this);
        fab.setColorNormal(Color.rgb(r, g, b));
        fab.setColorPressed(Color.rgb((int) (r * 0.8), (int) (g * 0.8), (int) (b * 0.8)));


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.abc_action_bar_home_description, R.string.abc_action_bar_home_description_format);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        id_textView01_Menu = (TextView) this.findViewById(R.id.id_textView01_Menu);
        id_textView02_Menu = (TextView) this.findViewById(R.id.id_textView02_Menu);
        id_textView03_Menu = (TextView) this.findViewById(R.id.id_textView03_Menu);
        id_textView01_Menu.setOnClickListener(this);
        id_textView02_Menu.setOnClickListener(this);
        id_textView03_Menu.setOnClickListener(this);


    }


    private AbsListView.LayoutParams localObject(String path) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenWidth = localDisplayMetrics.widthPixels;

        AbsListView.LayoutParams localObject;
        if (new File(path).exists()) {
            localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        } else {
            localObject = new AbsListView.LayoutParams(mScreenWidth, (int) (0.1F * (mScreenWidth / 16.0F)));
        }
        return localObject;
    }

    public String getDataLastPath(){
        String path = null;
        NotesOpenHelper notesOpenHelper;
        notesOpenHelper = new NotesOpenHelper(this);
        SQLiteDatabase db = notesOpenHelper.getReadableDatabase();
        Cursor c = db.query("notes", new String[]{"title", "text", "time", "id"}, null, null, null, null, "id DESC");

        if (c.moveToFirst()) {
            path = Environment.getExternalStorageDirectory() + "/Notes/image/" + c.getString(3);
        }
        c.close();
        db.close();

        return path;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;

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
            case R.id.id_textView01_Menu:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivity(intent1);
                break;
            case R.id.id_textView02_Menu:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("提示");
                alertDialog.setMessage("你确定要退出吗");
                alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();

                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                alertDialog.show();
                break;
            case R.id.id_textView03_Menu:
                Intent intent3 = new Intent(this, ThemeActivity.class);
                startActivity(intent3);
                finish();
                break;
        }
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
        Log.e("position", String.valueOf(i));
        id = notesInfo.getId();
        Log.e("position", id);
        switch (item.getItemId()) {
            case R.id.menuEdit:

                Intent intent = new Intent();
                intent.putExtra("title", notesInfo.getTitle());
                intent.putExtra("text", notesInfo.getText());
                intent.putExtra("id", id);
                Log.e("tag", id);
                intent.setClass(NotesActivity.this, NotesChangeActivity.class);
                startActivityForResult(intent, Change);
                break;
            case R.id.menuDelete:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Change) {
            if (resultCode != 10000) {
                ImageView imageView = (ImageView) list_view.getZoomView();
                imageView.setImageBitmap(BitmapFactory.decodeFile(getDataLastPath()));
                list_view.setHeaderLayoutParams(localObject(getDataLastPath()));
                initData();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = Refresh ;
                        handler.sendMessage(message);
                    }
                },1000);
            }
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
           if (msg.what == Refresh){
               ImageView imageView = (ImageView) list_view.getZoomView();
               imageView.setImageBitmap(BitmapFactory.decodeFile(getDataLastPath()));
               list_view.setHeaderLayoutParams(localObject(getDataLastPath()));
           }
        }
    };
}
