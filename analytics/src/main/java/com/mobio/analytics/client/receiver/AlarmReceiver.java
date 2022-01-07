package com.mobio.analytics.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogMobio.logD("QuanLA", "alarm");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
        }
        else {
            if(intent.getAction().equals("ACTION_LAUNCH_NOTI")){
                LogMobio.logD("QuanLA", "alarm 1");
                String notiObject = intent.getStringExtra("NOTI_OBJECT");
                if(notiObject != null){
                    NotiResponseObject notiResponseObject = new Gson().fromJson(notiObject, NotiResponseObject.class);
                    if (SharedPreferencesUtils.getBool(context, SharedPreferencesUtils.KEY_APP_FOREGROUD)) {
                        Analytics.getInstance().showGlobalPopup(notiResponseObject);
                    } else {
                        Analytics.getInstance().showGlobalNotification(notiResponseObject);
                    }
                }
            }
        }
    }
}
