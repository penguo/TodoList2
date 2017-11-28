package com.pepg.todolist.Optional;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.pepg.todolist.DataBase.DBManager;
import com.pepg.todolist.InfoActivity;
import com.pepg.todolist.MainActivity;
import com.pepg.todolist.Manager;
import com.pepg.todolist.R;

/**
 * Created by pengu on 2017-11-28.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("_id", -1);
        final DBManager dbManager = new DBManager(context, "todolist2.db", null, MainActivity.DBVERSION);
        Manager.notificationInfo(context, context.getResources(), id, dbManager);
    }
}
