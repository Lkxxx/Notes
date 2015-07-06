package com.lk.notes.Fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lk.notes.Adapter.RemindAdater;
import com.lk.notes.EditNoteActivity;
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
    private LinearLayout ll_none;
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

        ll_none = (LinearLayout) view.findViewById(R.id.ll_none);
        if (getNone()) {
            ll_none.setVisibility(View.VISIBLE);
        } else {
            ll_none.setVisibility(View.GONE);
        }
        ll_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditNoteActivity.class);
                startActivityForResult(intent, 1);
            }
        });



        listview = (ListView) view.findViewById(R.id.listview);
        listview.setVisibility(View.VISIBLE);
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
                intent.setClass(getActivity(), NotesChangeActivity.class);
                startActivityForResult(intent, Edit);
            }
        });
    }

    private void initData() {
        dao = new NotesDao(getActivity());
        new Thread() {
            public void run() {
                mNotesInfo = dao.findNotesC();
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
        NotesOpenHelper notesOpenHelper = new NotesOpenHelper(getActivity());
        SQLiteDatabase db = notesOpenHelper.getReadableDatabase();
        Cursor c = db.query("notes", new String[]{"clock"}, null, null, null, null, "_id DESC");
        if (c.getCount() != 0) {
            none = false;
        }
        while (c.moveToNext()){
            if (c.getString(0) ==null){
                none =true;
            }
        }
        c.close();
        db.close();
        return none;
    }
}
