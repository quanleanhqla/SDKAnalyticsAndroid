package com.mobio.analytics.client.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.utility.LogMobio;

public class TerminateService extends Service {
    public TerminateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogMobio.logD("TerminateService", "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogMobio.logD("TerminateService", "onTaskRemoved");
        //Analytics.getInstance().track(Analytics.DEMO_EVENT, Analytics.TYPE_APP_LIFECYCLE, "Application terminated");
        stopSelf();
    }
}