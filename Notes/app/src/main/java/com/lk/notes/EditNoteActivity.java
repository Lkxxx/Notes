package com.lk.notes;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.lk.notes.Receiver.ClockReceiver;
import com.lk.notes.UI.Widget.NotesAppWidget;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class EditNoteActivity extends ActionBarActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Toolbar toolbar;
    private EditText et_title, et_text;
    private NotesDao dao;
    private ImageView iv_image,shadowToolbar,gradual;
    private LinearLayout ll_remind, ll_date;
    private TextView tv_date, tv_time;
    private Button bt_delete;
    public String str_id;
    private ProgressDialog progressDialog;

    private static final int STOP = 1;
    private static int SET = 11;
    private static int CANCEL = 12;

    int[] date = new int[5];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        initView();


    }

    private void initView() {

        ll_remind = (LinearLayout) findViewById(R.id.ll_remind);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
        bt_delete = (Button) findViewById(R.id.bt_delete);
        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        ll_remind.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        tv_time.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        ll_date.setVisibility(View.GONE);

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
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);

        shadowToolbar = (ImageView)findViewById(R.id.shadowToolbar);
        gradual =(ImageView)findViewById(R.id.gradual);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.alpha(20));
            window.setNavigationBarColor(Color.argb(51, 0, 0, 0));
        }
        iv_image.setMinimumHeight((int) (80 * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f));
        shadowToolbar.setVisibility(View.GONE);
        gradual.setVisibility(View.GONE);
        setcolor();
    }

    public void setcolor() {
        SharedPreferences sharedPreferences = getSharedPreferences("color", MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 172);
        int b = sharedPreferences.getInt("b", 193);

        toolbar.setBackgroundColor(Color.rgb(r, g, b));
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.rgb((int) (r * 0.9), (int) (g * 0.9), (int) (b * 0.9)));

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
                ViewGroup.LayoutParams params = iv_image.getLayoutParams();
                params.height = toolbar.getHeight()+getStatusBarHeight();
                iv_image.setLayoutParams(params);
                iv_image.setImageResource(R.drawable.mini);

                new File(Environment.getExternalStorageDirectory() + "/Notes/image/cache/cache").delete();
                gradual.setVisibility(View.GONE);
                shadowToolbar.setVisibility(View.GONE);
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
;
                iv_image.setVisibility(View.VISIBLE);
                new ImageProcessing().getPhotoCache(Environment.getExternalStorageDirectory() + "/Notes/image/cache/camera", getWindowManager());
                new ImageProcessing().imagePreview(iv_image, this, "cache");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(Color.alpha(0));
                }
                toolbar.setBackgroundColor(Color.alpha(0));
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
                    shadowToolbar.setVisibility(View.VISIBLE);
                    gradual.setVisibility(View.VISIBLE);
                }
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
                /*iv_image.setImageBitmap(bitmap);
                toolbar.setBackgroundColor(Color.alpha(0));
                new ImageProcessing().getPhotoRgb(getWindow(), bitmap);*/
                toolbar.setBackgroundColor(Color.alpha(0));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.alpha(0));}
                new ImageProcessing().imagePreview(iv_image, this, "cache");
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
                    shadowToolbar.setVisibility(View.VISIBLE);
                    gradual.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == STOP) {
                progressDialog.dismiss();
                Toast.makeText(EditNoteActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
                updateWidget(getApplicationContext());
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
                SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH时mm分",
                        Locale.getDefault());
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                str_id = String.valueOf(timeGetTime);
                String str_time = sdf.format(timeGetTime);
                Log.e("a", str_title + str_text + str_time);
                if (TextUtils.isEmpty(str_title) && TextUtils.isEmpty(str_text)) {
                    str_title = "记得加标题哦～";

                }
                String idurl = Environment.getExternalStorageDirectory() + "/Notes/image/cache/cache";
                File file = new File(idurl);
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(new ImageProcessing().getPhotopath());
                    new ImageProcessing().saveScalePhoto(bitmap, str_id, EditNoteActivity.this);
                    file.delete();
                }

                info.setTitle(str_title);
                info.setText(str_text);
                info.setTime(str_time);
                info.setId(str_id);
                Log.e("a", str_title + str_text + str_time);
                if (ll_remind.getVisibility() == View.GONE && ll_date.getVisibility() == View.VISIBLE) {
                    dao.add(str_title, str_text, str_time, str_id, String.valueOf(c.get(Calendar.YEAR)),
                            date[0] + "年" + date[1] + "月" + date[2] + "日" + date[3] + "时" + date[4] + "分");
                    setClock(str_title, str_text, str_id, EditNoteActivity.this, SET, date);
                } else {
                    setClock(str_title, str_text, str_id, EditNoteActivity.this, CANCEL, date);
                    dao.add(str_title, str_text, str_time, str_id, String.valueOf(c.get(Calendar.YEAR)), null);
                }
                Message msg = new Message();
                msg.what = STOP;
                handler.sendMessage(msg);
            }
        });

        setResult(1);

        thread.start();
    }

    public void setClock(String title, String str_text, String str_id, Context context, int SETorCANCEL, int date[]) {
        int requestCode = Integer.parseInt(str_id.substring(6, 13));
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (SETorCANCEL == SET) {
            Intent intent = new Intent(context, ClockReceiver.class);
            intent.putExtra("text", str_text);
            intent.putExtra("title", title);
            intent.putExtra("id", str_id);
            PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.set(Calendar.YEAR, date[0]);
            c.set(Calendar.MONTH, date[1] - 1);
            c.set(Calendar.DAY_OF_MONTH, date[2]);
            c.set(Calendar.HOUR_OF_DAY, date[3]);
            c.set(Calendar.MINUTE, date[4]);
            c.set(Calendar.SECOND, 0);
            am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            Log.e("clock", "闹钟设置成功");
        } else if (SETorCANCEL == CANCEL) {
            Intent intent1 = new Intent(context, ClockReceiver.class);
            PendingIntent piC = PendingIntent.getBroadcast(context, requestCode, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(piC);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String str_title = et_title.getText().toString().trim();
                String str_text = et_text.getText().toString().trim();
                if (TextUtils.isEmpty(str_title) && TextUtils.isEmpty(str_text)) {
                    setResult(10000);
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
                            setResult(10000);
                            finish();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String str_title = et_title.getText().toString().trim();
        String str_text = et_text.getText().toString().trim();

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {


            if (TextUtils.isEmpty(str_title) && TextUtils.isEmpty(str_text) && !new File(Environment.getExternalStorageDirectory() + "/Notes/image/cache/cache").exists()) {
                finish();
                setResult(10000);
            } else {
                save();
                Toast.makeText(EditNoteActivity.this, "已经保存", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    public int[] getDate() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        c.set(Calendar.HOUR_OF_DAY, 9);
        c.set(Calendar.MINUTE, 00);
        c.add(Calendar.DATE, 1);
        int[] date = new int[]{c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)};
        return date;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_remind:
                ll_remind.setVisibility(View.GONE);
                ll_date.setVisibility(View.VISIBLE);
                date = getDate();
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

    public void setTimeMenu() {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        PopupMenu popupMenu = new PopupMenu(this, tv_time);
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
                                EditNoteActivity.this,
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
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);

        PopupMenu popupMenu = new PopupMenu(this, tv_date);
        getMenuInflater().inflate(R.menu.menu_date, popupMenu.getMenu());
        popupMenu.getMenu().findItem(R.id.action_nextweek).setTitle("下周" + weekDay());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_today:
                        tv_date.setText("今天");
                        date[0] = year;
                        date[1] = month + 1;
                        date[2] = day;
                        break;
                    case R.id.action_tomorrow:
                        c.add(Calendar.DATE, 1);
                        tv_date.setText("明天");
                        date[0] = c.get(Calendar.YEAR);
                        date[1] = c.get(Calendar.MONTH) + 1;
                        date[2] = c.get(Calendar.DATE);

                        break;
                    case R.id.action_nextweek:
                        tv_date.setText("下周" + weekDay());
                        c.add(Calendar.DATE, 7);
                        date[0] = c.get(Calendar.YEAR);
                        date[1] = c.get(Calendar.MONTH) + 1;
                        date[2] = c.get(Calendar.DATE);
                        break;
                    case R.id.action_date_choose:

                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                EditNoteActivity.this,
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.show(getSupportFragmentManager(), "Timepickerdialog");
                        break;
                }

                return true;
            }
        });
        popupMenu.show();
    }


    public String weekDay() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "天";
        } else if ("2".equals(mWay)) {
            mWay = "一";
        } else if ("3".equals(mWay)) {
            mWay = "二";
        } else if ("4".equals(mWay)) {
            mWay = "三";
        } else if ("5".equals(mWay)) {
            mWay = "四";
        } else if ("6".equals(mWay)) {
            mWay = "五";
        } else if ("7".equals(mWay)) {
            mWay = "六";
        }
        return mWay;
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        long timeGetTime = new Date().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年",
                Locale.getDefault());
        int year = Integer.parseInt(sdf.format(timeGetTime).substring(0, 4));
        String str;
        if (year == i) {
            str = (i1 + 1) + "月" + i2 + "日";
        } else {
            str = i + "年" + (i1 + 1) + "月" + i2 + "日";
        }
        tv_date.setText(str);
        date[0] = i;
        date[1] = i1 + 1;
        date[2] = i2;
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i1) {
        String hourString = i < 10 ? "0" + i : "" + i;
        String minuteString = i1 < 10 ? "0" + i1 : "" + i1;
        String time = hourString + ":" + minuteString;
        tv_time.setText(time);
        date[3] = i;
        date[4] = i1;

    }

    public void updateWidget(Context context) {
        Log.e("updateWidget", "update");
        NotesOpenHelper notesOpenHelper = new NotesOpenHelper(context);
        SQLiteDatabase db = notesOpenHelper.getReadableDatabase();
        Cursor c = db.query("notes", new String[]{"title", "text", "id", "clock","time"}, null, null, null, null, "_id DESC");
        if (c.getCount() != 0) {
            c.moveToFirst();
            String str_title = c.getString(0);
            String str_text = c.getString(1);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notes_app_widget);
            views.setTextViewText(R.id.widget_title, str_title);
            views.setTextViewText(R.id.widget_text, str_text);
            Intent intent = new Intent(context, PreviewActivity.class);
            intent.putExtra("title", c.getString(0));
            intent.putExtra("text", c.getString(1));
            intent.putExtra("id", c.getString(2));
            intent.putExtra("clock", c.getString(3));
            intent.putExtra("time", c.getString(4));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.ll_widget, pendingIntent);


            Intent intent1 = new Intent(context, NotesChangeActivity.class);
            intent1.putExtra("title", c.getString(0));
            intent1.putExtra("text", c.getString(1));
            intent1.putExtra("id", c.getString(2));
            intent1.putExtra("clock", c.getString(3));
            intent1.putExtra("time", c.getString(4));
            PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.iv_edit, pendingIntent1);
            ComponentName componentName = new ComponentName(context, NotesAppWidget.class);
            appWidgetManager.updateAppWidget(componentName, views);
            db.close();
            c.close();
        }
    }
    public int getStatusBarHeight() {
        int result =0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }
}
