package com.lk.notes;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            holder.iv_text = (ImageView) view.findViewById(R.id.iv_text);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.tv_text = (TextView) view.findViewById(R.id.tv_text);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_text.setVisibility(View.VISIBLE);
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
            /*ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            imageLoader.displayImage("file:///" + thumbnailPath, holder.iv_text);*/
        }
        return view;
    }

    static class ViewHolder {
        TextView tv_title;
        TextView tv_text;
        ImageView iv_text;
        TextView tv_time;
    }


    public static String convertTime(String time, String str) {
        double id = Double.parseDouble(str.substring(0, 10));
        String outputTime = "未知";
        int month = Integer.parseInt(time.substring(0, 2));
        int day = Integer.parseInt(time.substring(3, 5));
        int hour = Integer.parseInt(time.substring(6, 8));
        int minute = Integer.parseInt(time.substring(9, 11));
        long timeGetTime = new Date().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分",
                Locale.getDefault());
        String timenow = sdf.format(timeGetTime);

        int yearnow = Integer.parseInt(timenow.substring(0, 4));
        int monthnow = Integer.parseInt(timenow.substring(5, 7));
        int daynow = Integer.parseInt(timenow.substring(8, 10));
        int hournow = Integer.parseInt(timenow.substring(11, 13));
        int minutenow = Integer.parseInt(timenow.substring(14, 16));

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


}
