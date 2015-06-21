package com.lk.notes;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lk on 05/17.
 */
public class NotesAdapter extends BaseAdapter {
    private List<NotesInfo> mNotesInfos;
    private Context context;
    private NotesDao dao;
    private LayoutInflater inflater;

    public NotesAdapter(Context context, List<NotesInfo> mNotesInfos, NotesDao dao) {

        inflater = LayoutInflater.from(context);
        this.mNotesInfos = mNotesInfos;
        this.context = context;
        this.dao = dao;
    }


    @Override
    public int getCount() {
        return mNotesInfos.size();
    }

    @Override
    public Object getItem(int i) {

        return mNotesInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        final ViewHolder holder;

        String id = mNotesInfos.get(i).getId();

        String thumbnailPath = Environment.getExternalStorageDirectory() + "/Notes/image/" + id;
        final File file = new File(thumbnailPath);
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_notes, viewGroup, false);
            holder = new ViewHolder();
            holder.tv_clock = (TextView) view.findViewById(R.id.tv_clock);
            holder.iv_text = (ImageView) view.findViewById(R.id.iv_text);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.tv_text = (TextView) view.findViewById(R.id.tv_text);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_text.setVisibility(View.VISIBLE);

            holder.ll_clock = (LinearLayout) view.findViewById(R.id.ll_clock);
            holder.ll_clock.setVisibility(View.VISIBLE);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_text.setVisibility(View.VISIBLE);
        holder.iv_text.setVisibility(View.GONE);
        holder.tv_text.setText(mNotesInfos.get(i).getText());
        holder.tv_title.setText(mNotesInfos.get(i).getTitle());
        holder.tv_time.setText(convertTime(mNotesInfos.get(i).getTime(), id));


        if (mNotesInfos.get(i).getText().isEmpty()) {
            holder.tv_text.setVisibility(View.GONE);
        }
        if (file.exists()) {
            holder.iv_text.setVisibility(View.VISIBLE);
        }
        if (mNotesInfos.get(i).getClock() == null) {
            holder.ll_clock.setVisibility(View.GONE);
        } else {
            holder.tv_clock.setText(convertClock(mNotesInfos.get(i).getClock()));
        }
        return view;
    }

    static class ViewHolder {
        TextView tv_title;
        TextView tv_text;
        ImageView iv_text;
        TextView tv_time;
        LinearLayout ll_clock;
        TextView tv_clock;
    }


    public String convertTime(String time, String str) {
        long id = Long.parseLong(str.substring(0,10));
        String outputTime = "未知";
        int month = Integer.parseInt(time.substring(0, 2));
        int day = Integer.parseInt(time.substring(3, 5));
        int hour = Integer.parseInt(time.substring(6, 8));
        int minute = Integer.parseInt(time.substring(9, 11));

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        int yearnow = c.get(Calendar.YEAR);
        int monthnow = c.get(Calendar.MINUTE);
        int daynow = c.get(Calendar.DAY_OF_MONTH);
        int hournow = c.get(Calendar.HOUR_OF_DAY);
        int minutenow = c.get(Calendar.MINUTE);


        if (id - 31556926 * (yearnow - 1970) > 0 && id - 31556926 * (yearnow - 1970) <= 31556926) {
            if (month == monthnow) {
                if (daynow - day == 2) {
                    outputTime = "前天" + hour + "时";
                } else if (daynow - day == 1) {
                    outputTime = "昨天" + hour + "时";
                } else if (daynow == day) {
                    if (hournow - hour <= 1) {
                        if (minutenow - minute == 0) {
                            outputTime = "刚刚";
                        } else if (hournow - hour == 1 && minutenow - minute < 0) {
                            outputTime = (60 + minutenow - minute) + "分钟前";
                        } else {
                            outputTime = (minutenow - minute) + "分钟前";
                        }
                    } else {
                        outputTime = (hournow - hour) + "小时前";
                    }
                } else {
                    outputTime = day + "日" + hour + "时";
                }
            } else {
                outputTime = month + "月" + day + "日";
            }
        } else if (id - 31556926 * (yearnow - 1970) > 31556926 && id - 31556926 * (yearnow - 1970) <= 31556926) {
            outputTime = "去年" + month + "月" + day + "日";
        } else if (id - 31556926 * (yearnow - 1970) > 31556926) {
            outputTime = "前年" + month + "月" + day + "日";
        } else {
            outputTime = "3年前";
        }

        return outputTime;
    }

    public String convertClock(String str) {
        String outputTime = "未知";
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        Date d = null;
        try {
            d = formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeGetTime = d.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
                Locale.getDefault());
        String str_time = sdf.format(timeGetTime);
        int clock[] = new int[]{Integer.parseInt(str_time.substring(0,4)),
                Integer.parseInt(str_time.substring(5,7)),
                Integer.parseInt(str_time.substring(8,10)),
                Integer.parseInt(str_time.substring(11,13)),
                Integer.parseInt(str_time.substring(14,16))};
        int now[] = {c.get(Calendar.YEAR),c.get(Calendar.MINUTE), c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE)};
        if (clock[0]==now[0]){
            c.add(Calendar.DATE,1);
            if (clock[2] == c.get(Calendar.DAY_OF_MONTH)){
                outputTime = "明天" + clock[3]+":" + clock[4];
            }else {
                outputTime = clock[1]+"月"+clock[2]+"日"+ clock[3]+":" + clock[4];
            }
        }else {
            outputTime =  clock[0]+"年"+clock[1]+"月"+clock[2]+"日";
        }


        return outputTime;
    }

}
