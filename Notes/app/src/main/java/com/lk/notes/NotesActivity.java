package com.lk.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lk.notes.anim.ShakeAnim;

import java.util.List;


public class NotesActivity extends ActionBarActivity implements View.OnClickListener {

    private FloatingActionButton fab;
    private ListView list_view;
    private Toolbar toolbar;
    private NotesDao dao;
    private NotesAdapter adapter;
    private List<NotesInfo> mNotesInfos;
    private String id;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView id_textView01_Menu, id_textView02_Menu,id_textView03_Menu;


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

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int selectedPosition = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        final NotesInfo notesInfo = mNotesInfos.get(selectedPosition);
        String title = notesInfo.getTitle();
        String text = notesInfo.getText();
        id = notesInfo.getId();
        switch (item.getItemId()) {
            case R.id.menuEdit:

                Intent intent = new Intent();
                intent.putExtra("title", title);
                intent.putExtra("text", text);
                intent.putExtra("id", id);
                Log.e("tag",id);
                intent.setClass(NotesActivity.this, NotesChangeActivity.class);
                startActivity(intent);
                break;
            case R.id.menuDelete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(NotesActivity.this);
                alertDialog.setTitle("提示");
                alertDialog.setMessage("你确定要删除吗");
                alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        boolean res = dao.delete(id);
                        Toast.makeText(NotesActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        mNotesInfos.remove(notesInfo);
                        adapter.notifyDataSetChanged();

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
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(sendIntent.createChooser(sendIntent,title));
        }
        return super.onOptionsItemSelected(item);


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
        initData();
        adapter.notifyDataSetChanged();
        super.onRestart();

    }


    private RotateAnimation ra;
    private TranslateAnimation ta;
    private AnimationSet as;
    private ShakeAnim shakeAnim;
    private AlphaAnimation aa;

    private void intiview() {

        SharedPreferences sharedPreferences =getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 159);
        int b = sharedPreferences.getInt("b", 175);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        as = new AnimationSet(true);
        as.setDuration(200);

        ra = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);
        as.addAnimation(ra);

        //       ta = new ScaleAnimation(1,5,1,5,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ta = new TranslateAnimation(Animation.ZORDER_BOTTOM, 500, Animation.ZORDER_BOTTOM, 0);
        ta.setDuration(200);
        as.addAnimation(ta);

        aa = new AlphaAnimation(0, 100);
        aa.setDuration(200);


        list_view = (ListView) findViewById(R.id.listview);
        list_view.setVerticalScrollBarEnabled(true);
        registerForContextMenu(list_view);



        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("Notes");
        toolbar.setTitleTextColor(Color.rgb(15, 15, 15));
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));
        window.setNavigationBarColor(Color.rgb(r, g, b));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout ll_dl_iv = (LinearLayout)findViewById(R.id.ll_dl_iv);
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
                        startActivity(intent);
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

    private long exitTime = 0;

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


}
