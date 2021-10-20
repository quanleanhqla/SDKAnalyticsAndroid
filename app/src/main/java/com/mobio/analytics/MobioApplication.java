package com.mobio.analytics;

import android.app.Application;
import android.os.Build;

import com.mobio.analytics.client.Analytics;

public class MobioApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Analytics.Builder builder = new Analytics.Builder()
                .withApplication(this)
                .shouldTrackDeepLink(true)
                .shouldTrackLifecycle(true)
                .withIntervalSecond(10)
                .shouldRecordScreen(true)
                .shouldTrackScroll(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Analytics.setSingletonInstance(builder.build());
        }
    }
}
