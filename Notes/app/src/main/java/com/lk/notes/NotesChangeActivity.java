package com.lk.notes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NotesChangeActivity extends ActionBarActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == STOP) {
                progressDialog.dismiss();
                finish();
            }
        }
    };

    private LinearLayout ll_remind,ll_date;
    private TextView tv_date,tv_time;
    private Button bt_delete;

    int[] date = new int [5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        initView();


    }

    private void initView() {


        ll_remind = (LinearLayout)findViewById(R.id.ll_remind);
        tv_date = (TextView)findViewById(R.id.tv_date);
        tv_time = (TextView)findViewById(R.id.tv_time);
        bt_delete = (Button)findViewById(R.id.bt_delete);
        ll_date = (LinearLayout)findViewById(R.id.ll_date);
        ll_remind.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        bt_delete.setOnClickListener(this);


        scrollView = (ScrollView)findViewById(R.id.scrollView);



        et_text = (EditText) findViewById(R.id.et_text);
        et_title = (EditText) findViewById(R.id.et_title);
        et_text.setMinLines(17);
        Intent intent = this.getIntent();
        String title = intent.getStringExtra("title");//
        String text = intent.getStringExtra("text");
        id = intent.getStringExtra("id");
        String clock = intent.getStringExtra("clock");


        Log.e("changetext", "title:" + title +"    text:"+ text);
        et_title.setText(title);
        et_text.setText(text);
        et_title.setSelection(title.length());
        et_text.setSelection(text.length());
        et_title.setFocusable(true);
        et_title.setFocusableInTouchMode(true);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        registerForContextMenu(iv_image);

        toolbar = (Toolbar) findViewById(R.id.tl_custom);
        toolbar.setTitle("编辑");
        toolbar.setTitleTextColor(Color.rgb(238, 238, 238));
        setcolor();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(15);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        if (clock != null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
            try {
                Date d = formatter.parse(clock);
                long timeGetTime = d.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
                        Locale.getDefault());
                String str_time = sdf.format(timeGetTime);
                date = new int[]{Integer.parseInt(str_time.substring(0,4)),Integer.parseInt(str_time.substring(5,7)),
                        Integer.parseInt(str_time.substring(8,10)),
                        Integer.parseInt(str_time.substring(11,13)),
                        Integer.parseInt(str_time.substring(14,16))};

                ll_remind.setVisibility(View.GONE);
                ll_date.setVisibility(View.VISIBLE);
                tv_date.setText(Integer.parseInt(str_time.substring(5, 7)) + "月" + Integer.parseInt(str_time.substring(8, 10)) + "日");
                tv_time.setText(Integer.parseInt(str_time.substring(11,13))+":"+Integer.parseInt(str_time.substring(14,16))+"");
                Log.e("date",Integer.parseInt(str_time.substring(5, 7)) + "月" + Integer.parseInt(str_time.substring(8, 10)) + "日");

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }else {
            ll_remind.setVisibility(View.VISIBLE);
            ll_date.setVisibility(View.GONE);
        }

        new File(Environment.getExternalStorageDirectory() + "/Notes/image/cache/cache").delete();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));

        }
    }




    private void change() {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        NotesInfo info = new NotesInfo();
        dao = new NotesDao(this);
        str_title = et_title.getText().toString().trim();
        str_text = et_text.getText().toString().trim();
        long timeGetTime = new Date().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH时mm分",
                Locale.getDefault());
        final String str_time = sdf.format(timeGetTime);
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
            //dao.change(str_title, str_text, str_time, id);
            dao.delete(id);

            if(ll_remind.getVisibility() == View.GONE &&  ll_date.getVisibility() == View.VISIBLE) {
                dao.add(str_title, str_text, str_time, id,String.valueOf(c.get(Calendar.YEAR)),
                        date[0] + "年" + date[1] + "月" + date[2] + "日" + date[3] + "时" + date[4] + "分");
            }else {
                dao.add(str_title, str_text, str_time, id,String.valueOf(c.get(Calendar.YEAR)),null);
            }

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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_remind:
                ll_remind.setVisibility(View.GONE);
                ll_date.setVisibility(View.VISIBLE);
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                c.set(Calendar.HOUR_OF_DAY, 9);
                c.set(Calendar.MINUTE, 00);
                c.add(Calendar.DATE, 1);
                date = new int[]{c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),
                        c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE)};
                break;
            case R.id.tv_date:
                tv_date.setTextColor(Color.parseColor("#f7333333"));
                tv_time.setTextColor(Color.parseColor("#c4707070"));
                setDateMenu();
                break;
            case R.id.tv_time:
                tv_date.setTextColor(Color.parseColor("#c4707070"));
                tv_time.setTextColor(Color.parseColor("#f7333333"));
                setTimeMenu();
                break;
            case R.id.bt_delete:
                ll_remind.setVisibility(View.VISIBLE);
                ll_date.setVisibility(View.GONE);
                date = null;
                break;
    }
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        long timeGetTime = new Date().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年",
                Locale.getDefault());
        int year = Integer.parseInt(sdf.format(timeGetTime).substring(0,4));
        String str ;
        if (year == i ){
            str = (i1+1)+"月"+i2+"日";
        }else {
            str = i+"年"+(i1+1)+"月"+i2+"日";
        }
        tv_date.setText(str);
        date[0] = i;
        date[1] = i1;
        date[2] = i2;
    }
    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i1) {
        String hourString = i < 10 ? "0"+i : ""+i;
        String minuteString = i1 < 10 ? "0"+i1 : ""+i1;
        String time = hourString+":"+minuteString;
        tv_time.setText(time);
        date[3] = i;
        date[4] = i1;

    }


    public void setTimeMenu() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        PopupMenu  popupMenu = new PopupMenu(this, tv_time);
        getMenuInflater().inflate(R.menu.menu_time, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_morning:
                        tv_time.setText("上午");//9:00
                        c.set(Calendar.HOUR_OF_DAY, 9);
                        c.set(Calendar.MINUTE, 00);
                        date[3] = c.get(Calendar.HOUR_OF_DAY);
                        date[4] = c.get(Calendar.MINUTE);
                        break;
                    case R.id.action_afternoon:
                        tv_time.setText("下午");//14:00
                        c.set(Calendar.HOUR_OF_DAY, 14);
                        c.set(Calendar.MINUTE, 00);
                        date[3] = c.get(Calendar.HOUR_OF_DAY);
                        date[4] = c.get(Calendar.MINUTE);
                        break;
                    case R.id.action_evening:
                        tv_time.setText("傍晚");//17:00
                        c.set(Calendar.HOUR_OF_DAY, 17);
                        c.set(Calendar.MINUTE, 00);
                        date[3] = c.get(Calendar.HOUR_OF_DAY);
                        date[4] = c.get(Calendar.MINUTE);
                        break;
                    case R.id.action_night:
                        tv_time.setText("晚上");//20:00
                        c.set(Calendar.HOUR_OF_DAY, 20);
                        c.set(Calendar.MINUTE, 00);
                        date[3] = c.get(Calendar.HOUR_OF_DAY);
                        date[4] = c.get(Calendar.MINUTE);
                        break;
                    case R.id.action_time_choose:
                        tv_time.setText("选择时间");
                        Calendar now = Calendar.getInstance();
                        TimePickerDialog tpd = TimePickerDialog.newInstance(
                                NotesChangeActivity.this,
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE),
                                true
                        );
                        tpd.show(getSupportFragmentManager(), "Timepickerdialog");
                        break;


                }
                return true;
            }
        });
        popupMenu.show();


    }





    public void setDateMenu() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        final int  day = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);



        PopupMenu  popupMenu = new PopupMenu(this, tv_date);
        getMenuInflater().inflate(R.menu.menu_date, popupMenu.getMenu());
        popupMenu.getMenu().findItem(R.id.action_nextweek).setTitle("下周" + weekDay());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_today:
                        tv_date.setText("今天");
                        date[0] = year;
                        date[1]= month+1;
                        date[2] = day;
                        break;
                    case R.id.action_tomorrow:
                        c.add(Calendar.DATE, 1);
                        tv_date.setText("明天");
                        date[0] = c.get(Calendar.YEAR);
                        date[1] = c.get(Calendar.MONTH)+1;
                        date[2] = c.get(Calendar.DATE);

                        break;
                    case R.id.action_nextweek:
                        tv_date.setText("下周"+weekDay());
                        c.add(Calendar.DATE,7);
                        date[0] = c.get(Calendar.YEAR);
                        date[1] = c.get(Calendar.MONTH)+1;
                        date[2] = c.get(Calendar.DATE);
                        break;
                    case R.id.action_date_choose:

                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                NotesChangeActivity.this,
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.show(getSupportFragmentManager(),"Timepickerdialog");
                        break;
                }

                return true;
            }
        });
        popupMenu.show();
    }



    public String weekDay(){
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return mWay;
    }

}

