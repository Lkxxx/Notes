package com.lk.notes.UI.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import com.lk.notes.NotesChangeActivity;
import com.lk.notes.NotesOpenHelper;
import com.lk.notes.R;

/**
 * Implementation of App Widget functionality.
 */
public class NotesAppWidget extends AppWidgetProvider {
    AppWidgetManager appWidgetManager;
    int id;
    Context context;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.appWidgetManager = appWidgetManager;
        this.context = context;
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i], strFirst(context));
            this.id = i;
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String[] str) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notes_app_widget);

        views.setTextViewText(R.id.widget_title, str[0]);
        views.setTextViewText(R.id.widget_text, str[1]);
        Intent intent = new Intent(context, NotesChangeActivity.class);
        intent.putExtra("title", str[0]);
        intent.putExtra("text", str[1]);
        intent.putExtra("id", str[2]);
        intent.putExtra("clock", str[3]);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.ll_widget, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private String[] strFirst(Context context) {
        NotesOpenHelper notesOpenHelper = new NotesOpenHelper(context);
        SQLiteDatabase db = notesOpenHelper.getReadableDatabase();
        Cursor c = db.query("notes", new String[]{"title", "text", "id", "clock"}, null, null, null, null, "_id DESC");
        String[] str = new String[4];
        if (c.moveToFirst()) {
            str[0] = c.getString(0);
            str[1] = c.getString(1);
            str[2] = c.getString(2);
            str[3] = c.getString(3);
        }
        db.close();
        c.close();
        return str;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}

