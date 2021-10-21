package com.mobio.analytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.utility.LogMobio;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getExtras().getInt("notificationId");
        /* Your code to handle the event here */

        LogMobio.logD("NotificationDismissedReceiver", notificationId+"");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Analytics.with(context).track(Analytics.DEMO_EVENT, Analytics.TYPE_CLICK,"Click remove noti");
        }
    }
}