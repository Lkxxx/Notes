package com.lk.notes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thread.start();
    }

    public void re() {
        String title, text, time, id;
        NotesDao dao = new NotesDao(this);
        SQLiteDatabase database = op();
        Cursor c = database.query("notes", new String[]{"title", "text", "time", "id"}, null, null, null, null, "id");
        while (c.moveToNext()) {
            title = c.getString(0);
            text = c.getString(1);
            time = c.getString(2);
            id = c.getString(3);
            dao.add(title, text, time, id, null, null);

        }
        c.close();
        database.close();
    }


    public SQLiteDatabase op() {


        if (Path.exists()) {
            return SQLiteDatabase.openOrCreateDatabase(Path, null);
        } else {
            return null;
        }
    }


    public Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.e("path", Environment.getExternalStorageDirectory() + "/Notes/firstIn.db");
            try {
                deleteOldData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            re();
            new File(Environment.getExternalStorageDirectory() + "/Notes/firstIn.db").delete();


        }
    });


    public void deleteOldData() throws IOException {
        File dbFile = getApplicationContext().getDatabasePath("notes.db");
        new BackupTask(this).fileCopy(dbFile, Path);
        dbFile.delete();
        Log.e("olddata","olddata");
    }

    File Path = new File(Environment.getExternalStorageDirectory() + "/Notes/firstIn.db");
}
