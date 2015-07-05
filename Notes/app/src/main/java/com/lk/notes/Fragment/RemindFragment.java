package com.lk.notes.Fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lk.notes.Adapter.RemindAdater;
import com.lk.notes.NotesChangeActivity;
import com.lk.notes.NotesDao;
import com.lk.notes.NotesInfo;
import com.lk.notes.NotesOpenHelper;
import com.lk.notes.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemindFragment extends Fragment {
    private static final int Edit = 1;
    private ListView listview;
    private RemindAdater adapter;
     private List<NotesInfo> mNotesInfo;
    private NotesDao dao;

    private View view;
    public RemindFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_clock, container, false);

        initView();
        initData();

        return view;
    }

    private void initView() {
        listview = (ListView) view.findViewById(R.id.listview);
        if (getNone()){
            listview.setVisibility(View.GONE);
        }else {
            listview.setVisibility(View.VISIBLE);
        }
        registerForContextMenu(listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NotesInfo notesInfo = mNotesInfo.get(i);
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

    private void initData() {
        dao = new NotesDao(getActivity());
        new Thread() {
            public void run() {
                mNotesInfo = dao.findNotes();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new RemindAdater(getActivity(), mNotesInfo, dao);
                        listview.setAdapter(adapter);

                    }
                });
            }
        }.start();

    }
    public boolean getNone() {
        boolean none = true;
        NotesOpenHelper notesOpenHelper;
        notesOpenHelper = new NotesOpenHelper(getActivity());
        SQLiteDatabase db = notesOpenHelper.getReadableDatabase();
        Cursor c = db.query("notes", new String[]{"clock"}, null, null, null, null, "_id DESC");
        if (c.getCount() != 0) {
            none = false;
        }
        c.close();
        db.close();
        return none;
    }
}
