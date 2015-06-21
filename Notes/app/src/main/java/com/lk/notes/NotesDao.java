package com.lk.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lk on 05/17.
 */
public class NotesDao {

    private NotesOpenHelper notesOpenHelper;

    public NotesDao(Context context) {
        notesOpenHelper = new NotesOpenHelper(context);

    }

    public boolean add(String title, String text, String time, String id,String year,String clock) {
        SQLiteDatabase db = notesOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("text", text);
        values.put("time", time);
        values.put("id", id);
        values.put("year",year);
        values.put("clock",clock);

        long rowID = db.insert("notes", null, values);

        if (rowID == -1) {
            return false;
        } else {
            return true;
        }
    }


    public boolean delete(String id) {
        SQLiteDatabase db = notesOpenHelper.getWritableDatabase();
        int rowNumber = db.delete("notes", "id = ?", new String[]{id});
        if (rowNumber == 0) {
            return false;
        } else {
            return true;
        }
    }


    public boolean change(String newtitle, String newtext, String newtime, String id) {
        SQLiteDatabase db = notesOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", newtitle);
        values.put("text", newtext);
        values.put("time", newtime);
        int rowNumber = db.update("notes", values, "id = ?", new String[]{id});
        if (rowNumber == 0) {
            return false;
        } else {
            return true;
        }
    }


    public List<NotesInfo> findNotes() {
        SQLiteDatabase db = notesOpenHelper.getReadableDatabase();

        List<NotesInfo> lists = new ArrayList<NotesInfo>();
        Cursor c = db.query("notes", new String[]{"title", "text", "time", "id","year","clock"}, null, null, null, null, "_id DESC");
        while (c.moveToNext()) {
            NotesInfo info = new NotesInfo();
            info.setTitle(c.getString(0));
            info.setText(c.getString(1));
            info.setTime(c.getString(2));
            info.setId(c.getString(3));
            info.setYear(c.getString(4));
            info.setClock(c.getString(5));
            lists.add(info);

        }


        c.close();
        db.close();
        return lists;
    }
}
