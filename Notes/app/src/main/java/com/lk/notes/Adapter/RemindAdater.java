package com.lk.notes.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lk.notes.ConvertTime;
import com.lk.notes.NotesDao;
import com.lk.notes.NotesInfo;
import com.lk.notes.R;

import java.util.List;

/**
 * Created by lk on 07/05.
 */
public class RemindAdater extends BaseAdapter {
    private List<NotesInfo> mNotesInfos;
    private Context context;
    private NotesDao dao;
    private LayoutInflater inflater;

    public RemindAdater(Context context, List<NotesInfo> mNotesInfos, NotesDao dao) {
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_remind, viewGroup, false);
            holder = new ViewHolder();
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.tv_text = (TextView)view.findViewById(R.id.tv_text);
            view.setTag(holder);
            SharedPreferences sharedPreferences = context.getSharedPreferences("color", context.MODE_PRIVATE);
            int r = sharedPreferences.getInt("r", 0);
            int g = sharedPreferences.getInt("g", 172);
            int b = sharedPreferences.getInt("b", 193);
            holder.tv_time.setTextColor(Color.rgb(r, g, b));
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.tv_time.setText(ConvertTime.convertClock(mNotesInfos.get(i).getClock()));
        holder.tv_title.setText(mNotesInfos.get(i).getTitle());
        if (mNotesInfos.get(i).getText() == null){
            holder.tv_text.setVisibility(View.GONE);
        }else {
            holder.tv_text.setText(mNotesInfos.get(i).getText());
        }
        return view;


    }

    static class ViewHolder {
        TextView tv_time;
        TextView tv_title;
        TextView tv_text;
    }
}
