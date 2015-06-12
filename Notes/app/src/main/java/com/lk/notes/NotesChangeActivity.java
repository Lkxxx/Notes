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
import android.net.Uri;
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
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NotesChangeActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private EditText et_title, et_text;
    private NotesDao dao;
    private String id;
    private String str_title;
    private String str_text;
    private ImageView iv_image;
    private ProgressDialog progressDialog;
    private static final int STOP = 1;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        initView();


    }

    private void initView() {

        scrollView = (ScrollView)findViewById(R.id.scrollView);



        et_text = (EditText) findViewById(R.id.et_text);
        et_title = (EditText) findViewById(R.id.et_title);

        Intent intent = this.getIntent();
        String title = intent.getStringExtra("title");
        String text = intent.getStringExtra("text");

        id = intent.getStringExtra("id");
        et_title.setText(title);
        et_text.setText(text);
        et_title.setSelection(title.length());
        et_text.setSelection(text.length());
        et_title.setFocusable(true);
        et_title.setFocusableInTouchMode(true);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        registerForContextMenu(iv_image);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("编辑");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));
        setcolor();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(15);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        String idurl = Environment.getExternalStorageDirectory() + "/Notes/image/" + id;
        final File file = new File(idurl);
        if (file.exists()) {
            toolbar.setBackgroundColor(Color.alpha(0));
            File newFile = new File(new ImageProcessing().getPhotopath());
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                byte[] dataPhoto = new byte[1024];
                while (fileInputStream.read(dataPhoto) != -1) {
                    fileOutputStream.write(dataPhoto);
                }
                fileInputStream.close();
                fileOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = new ImageProcessing().setPhoto(getWindowManager(), idurl);

            new ImageProcessing().getPhotoRgb(getWindow(), bitmap);

            iv_image.setImageBitmap(bitmap);
        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_check:
                        change();

                        finish();
                        break;
                    case R.id.action_camera:
                        iv_image.setVisibility(View.VISIBLE);
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
                return true;
            }
        });
        scrollView.smoothScrollBy(0,200);

    }


    public void setcolor() {
        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 159);
        int b = sharedPreferences.getInt("b", 175);
        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));
        window.setNavigationBarColor(Color.rgb(r, g, b));

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == STOP) {
                progressDialog.dismiss();
                finish();
            }
        }
    };


    private void change() {

        NotesInfo info = new NotesInfo();
        dao = new NotesDao(this);
        str_title = et_title.getText().toString().trim();
        str_text = et_text.getText().toString().trim();
        long timeGetTime = new Date().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH时mm分",
                Locale.getDefault());
        String str_time = sdf.format(timeGetTime);
        Log.e("a", str_title + str_text + str_time + id);
        progressDialog = ProgressDialog.show(this, "", "正在保存", true, false);
        if (TextUtils.isEmpty(str_title) && TextUtils.isEmpty(str_text)) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_LONG).show();
        } else {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    File IdFile = new File(Environment.getExternalStorageDirectory() + "/Notes/image/" + id);
                    if (!IdFile.exists()) {
                        File cacheFile = new File(Environment.getExternalStorageDirectory() + "/Notes/image/cache/cache");
                        if (cacheFile.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(new ImageProcessing().getPhotopath());
                            new ImageProcessing().saveScalePhoto(bitmap, id, NotesChangeActivity.this);
                            cacheFile.delete();
                        }
                        Message msg = new Message();
                        msg.what = STOP;
                        handler.sendMessage(msg);
                    }
                }
            });
            thread.start();
            info.setTitle(str_title);
            info.setText(str_text);
            info.setTime(str_time);
            Log.e("a", str_title + str_text + str_time);
            dao.change(str_title, str_text, str_time, id);
            new File(Environment.getExternalStorageDirectory() + "/Notes/image/cache/cache.jpg").delete();
            Toast.makeText(NotesChangeActivity.this, "修改已经保存", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(NotesChangeActivity.this);

                alertDialog.setTitle("提示");
                alertDialog.setMessage("保存？");
                alertDialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        change();
                        finish();

                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                alertDialog.show();

                return true;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            change();
            finish();

        }


        return super.onKeyDown(keyCode, event);
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
                Log.e("a", new ImageProcessing().getPhotopath());
                startActivity(intent);
                break;
            case R.id.menuDelete:
                setcolor();
                iv_image.setImageResource(R.drawable.mini);
                new File(Environment.getExternalStorageDirectory() + "/Notes/image/" + id).delete();
                new File(Environment.getExternalStorageDirectory() + "/Notes/image/cache/cache").delete();
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Window window = getWindow();
                iv_image.setVisibility(View.VISIBLE);
                new ImageProcessing().getPhotoCache(Environment.getExternalStorageDirectory() + "/Notes/image/cache/camera", getWindowManager());
                Bitmap bitmap = new ImageProcessing().setPhoto(getWindowManager(), new ImageProcessing().getPhotopath());
                iv_image.setImageBitmap(bitmap);
                new File(Environment.getExternalStorageDirectory() + "/Notes/image/" + id).delete();
                toolbar.setBackgroundColor(Color.alpha(0));
                new ImageProcessing().getPhotoRgb(window, bitmap);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "取消", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                iv_image.setVisibility(View.VISIBLE);
                new File(Environment.getExternalStorageDirectory() + "/Notes/image/" + id).delete();
                String path = new ImageProcessing().uri2path(data.getData(), this);
                new ImageProcessing().getPhotoCache(path, getWindowManager());
                Bitmap bitmap = new ImageProcessing().setPhoto(getWindowManager(), path);
                iv_image.setImageBitmap(bitmap);
                toolbar.setBackgroundColor(Color.alpha(0));
                new ImageProcessing().getPhotoRgb(getWindow(), bitmap);
            }
        }


    }




}

