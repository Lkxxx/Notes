package com.lk.notes.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.lk.notes.NotesChangeActivity;
import com.lk.notes.NotesDao;
import com.lk.notes.R;

public class ClockReceiver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String text = intent.getStringExtra("text");
        String title = intent.getStringExtra("title");
        String id = intent.getStringExtra("id");


        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setColor(Color.rgb(0, 172, 193));
        }

        builder.setSmallIcon(R.mipmap.notification_icon);
        builder.setDefaults(Notification.DEFAULT_ALL);

        Intent intent1 = new Intent(context, NotesChangeActivity.class);
        intent1.putExtra("title",title);
        intent1.putExtra("text",text);
        intent1.putExtra("id",id);
        Log.e("Notification", "title:" + title +"    text:"+ text);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        nm.notify(100, builder.build());


        NotesDao dao = new NotesDao(context);
        dao.deleteClock(id);

    }




}
