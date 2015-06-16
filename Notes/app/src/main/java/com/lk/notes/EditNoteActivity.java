package com.lk.notes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class EditNoteActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private EditText et_title, et_text;
    private NotesDao dao;
    private ImageView iv_image;

    private RelativeLayout rl_edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        initView();


    }

    private void initView() {

        String pathUrl = Environment.getExternalStorageDirectory().getPath() + "/Notes/image/";
        File file = new File(pathUrl);
        file.mkdirs();
        new File(new ImageProcessing().getPhotopath()).delete();
        et_text = (EditText) findViewById(R.id.et_text);
        et_title = (EditText) findViewById(R.id.et_title);

        iv_image = (ImageView) findViewById(R.id.iv_image);
        registerForContextMenu(iv_image);


        final Intent intent = this.getIntent();
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");
        et_title.setText(title);
        et_text.setText(text);


        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("新建");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));
        setcolor();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(15);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


    }

    public void setcolor() {
        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 159);
        int b = sharedPreferences.getInt("b", 175);

        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));
            window.setNavigationBarColor(Color.rgb(r, g, b));
        }


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager) et_title.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(et_title, 0);
                           }

                       },
                500);

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.imagecontext, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuView:
                File file = new File(new ImageProcessing().getPhotopath());
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(intent);
                break;
            case R.id.menuDelete:
                setcolor();
                iv_image.setImageResource(R.drawable.mini);
                new File(Environment.getExternalStorageDirectory() + "/Notes/image/cache/cache").delete();
                break;
        }

        return super.onContextItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Window window = getWindow();
                iv_image.setVisibility(View.VISIBLE);
                new ImageProcessing().getPhotoCache(Environment.getExternalStorageDirectory() + "/Notes/image/cache/camera", getWindowManager());
                Bitmap bitmap = new ImageProcessing().setPhoto(getWindowManager(), new ImageProcessing().getPhotopath());
                iv_image.setImageBitmap(bitmap);
                toolbar.setBackgroundColor(Color.alpha(0));
                new ImageProcessing().getPhotoRgb(window, bitmap);

            } else if (resultCode == RESULT_CANCELED) {
            }
        }
        //图片
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                iv_image.setVisibility(View.VISIBLE);
                Uri uri = data.getData();
                String path = new ImageProcessing().uri2path(uri, this);
                Bitmap bitmap = new ImageProcessing().getPhotoCache(path, getWindowManager());
                iv_image.setImageBitmap(bitmap);
                toolbar.setBackgroundColor(Color.alpha(0));
                new ImageProcessing().getPhotoRgb(getWindow(), bitmap);

                Rect frame = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top;
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){


                }
            }
        }
    }


    public String str_id;
    private ProgressDialog progressDialog;
    private static final int STOP = 1;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == STOP) {
                progressDialog.dismiss();
                Toast.makeText(EditNoteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };


    private void save() {

        progressDialog = ProgressDialog.show(this, "", "正在保存", true, false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final NotesInfo info = new NotesInfo();
                dao = new NotesDao(EditNoteActivity.this);
                String str_title = et_title.getText().toString().trim();
                String str_text = et_text.getText().toString().trim();
                long timeGetTime = new Date().getTime();
                str_id = String.valueOf(timeGetTime);
                Log.e("msg", str_id);
                SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH时mm分",
                        Locale.getDefault());
                String str_time = sdf.format(timeGetTime);
                Log.e("a", str_title + str_text + str_time);
                if (TextUtils.isEmpty(str_title) && TextUtils.isEmpty(str_text)) {
                    str_title = "标题";

                }
                String idurl = Environment.getExternalStorageDirectory() + "/Notes/image/cache/cache";
                File file = new File(idurl);
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(new ImageProcessing().getPhotopath());
                    new ImageProcessing().saveScalePhoto(bitmap, str_id, EditNoteActivity.this);
                    file.delete();
                }
                Message msg = new Message();
                msg.what = STOP;
                handler.sendMessage(msg);
                info.setTitle(str_title);
                info.setText(str_text);
                info.setTime(str_time);
                info.setId(str_id);
                Log.e("a", str_title + str_text + str_time);
                dao.add(str_title, str_text, str_time, str_id);
            }
        });
        thread.start();
    }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            switch (item.getItemId()) {
                case android.R.id.home:
                    String str_title = et_title.getText().toString().trim();
                    String str_text = et_text.getText().toString().trim();

                    if (TextUtils.isEmpty(str_title) && TextUtils.isEmpty(str_text)) {
                        finish();

                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditNoteActivity.this);
                        alertDialog.setTitle("提示");
                        alertDialog.setMessage("保存？");
                        alertDialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                save();

                            }
                        });
                        alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                setResult(10000);
                            }
                        });
                        alertDialog.show();
                    }
                    break;
                case R.id.action_check:
                    save();

                    break;
                case R.id.action_camera:

                    Intent capIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    capIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    File out = new File(Environment.getExternalStorageDirectory() + "/Notes/image/cache/camera");
                    Uri uri = Uri.fromFile(out);
                    capIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(capIntent, 100);
                    break;
                case R.id.action_photo:
                    Intent photoIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(photoIntent, 1000);


            }
            return super.onOptionsItemSelected(item);
        }


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.menu_edit_note, menu);
            return true;
        }


        @Override
        public boolean onKeyDown ( int keyCode, KeyEvent event){
            String str_title = et_title.getText().toString().trim();
            String str_text = et_text.getText().toString().trim();

            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {


                if (TextUtils.isEmpty(str_title) && TextUtils.isEmpty(str_text)&&!new File(Environment.getExternalStorageDirectory() + "/Notes/image/cache/cache").exists()) {
                    finish();
                } else {
                    save();
                    Toast.makeText(EditNoteActivity.this, "已经保存", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            return super.onKeyDown(keyCode, event);
        }



    }
