package com.lk.notes.Adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lk.notes.ConvertTime;
import com.lk.notes.NotesDao;
import com.lk.notes.NotesInfo;
import com.lk.notes.R;

import java.io.File;
import java.util.List;

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
        Log.e("getview", mNotesInfos.get(i).getTitle());
        holder.ll_clock.setVisibility(View.VISIBLE);
        holder.tv_text.setVisibility(View.VISIBLE);
        holder.iv_text.setVisibility(View.GONE);
        holder.tv_text.setText(mNotesInfos.get(i).getText());
        holder.tv_title.setText(mNotesInfos.get(i).getTitle());
        holder.tv_time.setText(ConvertTime.convertTime(mNotesInfos.get(i).getTime(), id));


        if (mNotesInfos.get(i).getText().isEmpty()) {
            holder.tv_text.setVisibility(View.GONE);
        }
        if (file.exists()) {
            holder.iv_text.setVisibility(View.VISIBLE);
        }
        if (mNotesInfos.get(i).getClock() != null) {
            holder.tv_clock.setText(ConvertTime.convertClock(mNotesInfos.get(i).getClock()));
        } else {
            holder.ll_clock.setVisibility(View.GONE);
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



}
