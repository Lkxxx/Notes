package com.lk.notes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lk.notes.swipemenulistview.SwipeMenuListView;

import java.util.List;

/**
 * Created by lk on 05/17.
 */
public class NotesAdapter extends BaseAdapter{
    private List<NotesInfo> mNotesInfos;
    private Context context;
    private NotesDao dao;
    private SwipeMenuListView list_view;
    private TranslateAnimation translateAnimation;
    public NotesAdapter(Context context, List<NotesInfo> mNotesInfos,NotesDao dao){
        this.mNotesInfos =mNotesInfos;
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
        if(view == null){
            view = View.inflate(context, R.layout.item_notes,null);

        }
        TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
        TextView tv_text = (TextView)view.findViewById(R.id.tv_text);
        TextView tv_time = (TextView)view.findViewById(R.id.tv_time);

        tv_text.setText(mNotesInfos.get(i).getText());
        tv_title.setText(mNotesInfos.get(i).getTitle());
        tv_time.setText(mNotesInfos.get(i).getTime());
        /*translateAnimation = new TranslateAnimation(-720,0,Animation.ZORDER_BOTTOM,0);
        translateAnimation.setDuration(500);
        view.startAnimation(translateAnimation);*/



        return view;


    }


}
