package com.mobio.analytics.client.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobio.analytics.client.utility.LogMobio;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            int notificationId = intent.getExtras().getInt("notificationId");
            /* Your code to handle the event here */

            //todo

            boolean isDelete = intent.getBooleanExtra("type_delete", false);
            if(isDelete) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
            }
        }
    }
}