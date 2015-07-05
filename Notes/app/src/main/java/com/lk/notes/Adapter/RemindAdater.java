package com.lk.notes.Adapter;

import android.content.Context;
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
            view.setTag(holder);
        } else {
           holder = (ViewHolder)view.getTag();
        }
        if (mNotesInfos.get(i).getClock() != null) {
        holder.tv_time.setText(ConvertTime.convertClock(mNotesInfos.get(i).getClock()));
        holder.tv_title.setText(mNotesInfos.get(i).getTitle());
        }
        return view;
    }

    static class ViewHolder {
        TextView tv_time;
        TextView tv_title;
    }
}
