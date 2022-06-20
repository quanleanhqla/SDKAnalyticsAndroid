package com.mobio.analytics.client.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.receiver.AlarmReceiver;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;

import java.util.ArrayList;

public class TerminateService extends Service {
    private AlarmManager alarmManager;

    public TerminateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            ArrayList<Properties> pendingJsonPush = MobioSDKClient.getInstance().getListFromSharePref(SharedPreferencesUtils.M_KEY_PENDING_PUSH);
            if (pendingJsonPush.size() > 0) {
                int countNoti = pendingJsonPush.size();
                long maxInterval = 60 * 1000;
                long minInterval = 2 * 1000;
                long intervel = Utils.getTimeInterval(maxInterval, minInterval, countNoti);
                long now = System.currentTimeMillis();

                for (int i = 0; i < countNoti; i++) {
                    Intent intent = new Intent(this, AlarmReceiver.class);
                    intent.setAction("ACTION_LAUNCH_NOTI");

                    PendingIntent alarmIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_IMMUTABLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC,  now + intervel * (i+1), alarmIntent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopSelf();
    }
}