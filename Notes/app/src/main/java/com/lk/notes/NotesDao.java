package com.lk.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

    public boolean add(String title, String text, String time, String id, String year, String clock) {
        SQLiteDatabase db = notesOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("text", text);
        values.put("time", time);
        values.put("id", id);
        values.put("year", year);
        values.put("clock", clock);

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

    public boolean changeClock(String id, String clock) {
        SQLiteDatabase db = notesOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("clock", clock);
        int rowNumber = db.update("notes", values, "id = ?", new String[]{id});
        if (rowNumber == 0) {
            return false;
        } else {
            return true;
        }
    }


    public boolean deleteClock(String id) {
        SQLiteDatabase db = notesOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String clock = null;
        values.put("clock", clock);

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
        Cursor c = db.query("notes", new String[]{"title", "text", "time", "id", "year", "clock"}, null, null, null, null, "_id DESC");
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
        return lists;
    }

    public List<NotesInfo> findNotesC() {
        SQLiteDatabase db = notesOpenHelper.getReadableDatabase();

        List<NotesInfo> lists = new ArrayList<NotesInfo>();
        Cursor c = db.query("notes", new String[]{"title", "text", "time", "id", "year", "clock"}, null, null, null, null, "_id DESC");
        while (c.moveToNext()) {
            if (c.getString(5) != null) {
                NotesInfo info = new NotesInfo();
                info.setTitle(c.getString(0));
                info.setText(c.getString(1));
                info.setTime(c.getString(2));
                info.setId(c.getString(3));
                info.setYear(c.getString(4));
                info.setClock(c.getString(5));
                lists.add(info);
                Log.e("c", c.getString(5));
            }
        }
        c.close();

        return lists;
    }

    public boolean findNoneC() {
        boolean none = true;
        SQLiteDatabase db = notesOpenHelper.getReadableDatabase();
        Cursor c = db.query("notes", new String[]{"clock"}, null, null, null, null, "_id DESC");
        if (c.getCount() != 0) {
            none = false;
        }
        c.close();

        return none;
    }
}
