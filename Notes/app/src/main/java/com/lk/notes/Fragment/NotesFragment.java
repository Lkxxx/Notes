package com.lk.notes.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.lk.notes.Adapter.NotesAdapter;
import com.lk.notes.EditNoteActivity;
import com.lk.notes.NotesChangeActivity;
import com.lk.notes.NotesDao;
import com.lk.notes.NotesInfo;
import com.lk.notes.NotesOpenHelper;
import com.lk.notes.PullToZoom.PullToZoomListViewEx;
import com.lk.notes.R;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final int Edit = 1;
    private static final int Change = 1;
    private static final int Refresh = 2;


    private static int SET = 11;
    private static int CANCEL = 12;
    private NotesDao dao;
    private View view;
    private PullToZoomListViewEx list_view;
    private LinearLayout ll_none;
    private NotesAdapter adapter;
    private List<NotesInfo> mNotesInfo;
    private String id, text, title;
    private FragmentActivity myContext;
    private ImageView iv_none;

    int[] date = new EditNoteActivity().getDate();

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


    public NotesFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notes, container, false);
        if (getActivity() != null) {
            initView();
            initData();
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        if (list_view != null) {
            setRefresh();
        }

        super.onResume();
    }

    private void initData() {
        dao = new NotesDao(getActivity());
        new Thread() {
            public void run() {
                mNotesInfo = dao.findNotes();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new NotesAdapter(getActivity(), mNotesInfo, dao, themeColor());
                        list_view.setAdapter(adapter);

                    }
                });
            }
        }.start();

    }

    private void initView() {
        ll_none = (LinearLayout) view.findViewById(R.id.ll_none);
        iv_none = (ImageView) view.findViewById(R.id.iv_none);

        if (getNone()) {
            ll_none.setVisibility(View.VISIBLE);
        } else {
            ll_none.setVisibility(View.GONE);
        }
        ll_none.setOnClickListener(this);

        list_view = (PullToZoomListViewEx) view.findViewById(R.id.listview);
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
                    notesInfo = mNotesInfo.get(i - 1);
                } else if (i == 0) {
                    notesInfo = mNotesInfo.get(i);
                }
                Intent intent = new Intent();
                intent.putExtra("title", notesInfo.getTitle());
                intent.putExtra("text", notesInfo.getText());
                intent.putExtra("id", notesInfo.getId());
                intent.putExtra("clock", notesInfo.getClock());
                Log.e("id", notesInfo.getId());
                intent.setClass(getActivity(), NotesChangeActivity.class);
                startActivityForResult(intent, Edit);

            }
        });

    }

    private AbsListView.LayoutParams localObject(String path) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
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
        if (getActivity() != null) {
            NotesOpenHelper notesOpenHelper = new NotesOpenHelper(getActivity());
            SQLiteDatabase db = notesOpenHelper.getReadableDatabase();
            Cursor c = db.query("notes", new String[]{"title", "text", "time", "id"}, null, null, null, null, "_id DESC");
            if (c.moveToFirst()) {
                path = Environment.getExternalStorageDirectory() + "/Notes/image/" + c.getString(3);
            }
            c.close();
            db.close();
        }
        return path;
    }

    public boolean getNone() {
        boolean none = true;
        NotesOpenHelper notesOpenHelper;
        notesOpenHelper = new NotesOpenHelper(getActivity());
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
    public void onClick(View view) {
        if (view.getId() == R.id.ll_none) {
            Intent intent = new Intent(getActivity(), EditNoteActivity.class);
            startActivityForResult(intent, Change);
        }
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
        final NotesInfo notesInfo = mNotesInfo.get(i);
        id = notesInfo.getId();
        String clock = notesInfo.getClock();
        title = notesInfo.getTitle();
        text = notesInfo.getText();

        switch (item.getItemId()) {
            case R.id.menuEdit:
                Intent intent = new Intent();
                intent.putExtra("title", notesInfo.getTitle());
                intent.putExtra("text", notesInfo.getText());
                intent.putExtra("id", id);
                intent.putExtra("clock", notesInfo.getClock());
                intent.setClass(getActivity(), NotesChangeActivity.class);
                startActivityForResult(intent, Change);
                break;
            case R.id.menuDelete:
                dao.deleteClock(id);
                date = new EditNoteActivity().getDate();
                new EditNoteActivity().setClock(null, null, id, getActivity(), CANCEL, date);
                menuDelete(notesInfo, selectedPosition);

                break;
            case R.id.menuShare:
                Intent sendIntent = new Intent().setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, notesInfo.getTitle());
                sendIntent.putExtra(Intent.EXTRA_TEXT, notesInfo.getTitle()+"\n"+notesInfo.getText());
                startActivity(sendIntent.createChooser(sendIntent, notesInfo.getTitle()));
                break;
            case 4:
                addOrChange(clock);
                break;
            case 5:
                dao.deleteClock(id);
                date = new EditNoteActivity().getDate();
                new EditNoteActivity().setClock(null, null, id, getActivity(), CANCEL, date);
                setRefresh();
                new EditNoteActivity().updateWidget(getActivity());
                break;
        }
        return super.onOptionsItemSelected(item);


    }

    private void menuDelete(final NotesInfo notesInfo, final int selectedPosition) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("提示");
        alertDialog.setMessage("你确定要删除吗");
        alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File f = new File(Environment.getExternalStorageDirectory() + "/Notes/image/" + id);
                dao.delete(id);
                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                mNotesInfo.remove(notesInfo);
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
                new EditNoteActivity().updateWidget(getActivity());
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

    public void addOrChange(String clock) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        if (clock != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
            try {
                Date d = formatter.parse(clock);
                long timeGetTime = d.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分", Locale.getDefault());
                String str_time = sdf.format(timeGetTime);
                date = new int[]{Integer.parseInt(str_time.substring(0, 4)), Integer.parseInt(str_time.substring(5, 7)),
                        Integer.parseInt(str_time.substring(8, 10)), Integer.parseInt(str_time.substring(11, 13)),
                        Integer.parseInt(str_time.substring(14, 16))};
                now.set(Calendar.YEAR, date[0]);
                now.set(Calendar.MONTH, date[1] - 1);
                now.set(Calendar.DAY_OF_MONTH, date[2]);
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(myContext.getSupportFragmentManager(), "Timepickerdialog");

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {

            date[3] = now.get(Calendar.HOUR_OF_DAY);
            date[4] = now.get(Calendar.MINUTE);
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(myContext.getSupportFragmentManager(), "Timepickerdialog");
        }

    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int i, int i1) {
        date[3] = i;
        date[4] = i1;
        dao.changeClock(id, date[0] + "年" + (date[1]) + "月" + date[2] + "日" + date[3] + "时" + date[4] + "分");
        new EditNoteActivity().setClock(title, text, id, getActivity(), SET, date);
        setRefresh();
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        date[0] = i;
        date[1] = i1 + 1;
        date[2] = i2;
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, date[3]);
        now.set(Calendar.MINUTE, date[4]);
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.show(myContext.getSupportFragmentManager(), "Timepickerdialog");

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
        Log.e("TAG", "setRefresh");
        new EditNoteActivity().updateWidget(getActivity());
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        myContext.getMenuInflater().inflate(R.menu.context, menu);

        final int selectedPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        int i = 0;
        if (selectedPosition >= 1) {
            i = selectedPosition - 1;
        } else if (selectedPosition == 0) {
            i = selectedPosition;
        }
        String clock = mNotesInfo.get(i).getClock();
        if (clock != null) {
            menu.add(0, 4, 0, "修改闹钟");
            menu.add(0, 5, 0, "删除闹钟");
        } else {
            menu.add(0, 4, 0, "增加闹钟");
        }

    }

    public int themeColor() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("color", Context.MODE_PRIVATE);
        int r = sharedPreferences.getInt("r", 0);
        int g = sharedPreferences.getInt("g", 172);
        int b = sharedPreferences.getInt("b", 193);
        if (r == 0 && g == 172 && b == 193) {
            return Color.rgb(40, 190, 120);
        } else {
            return Color.rgb(r, g, b);
        }
    }
}
