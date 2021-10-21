package com.mobio.analytics;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.utility.LogMobio;

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
                .withApiToken("Basic f5e27185-b53d-4aee-a9b7-e0579c24d29d")
                .withMerchantId("1b99bdcf-d582-4f49-9715-1b61dfff3924");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Analytics.setSingletonInstance(builder.build());
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            LogMobio.logD("MobioApplication", "Fetching FCM registration token failed" + task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        Analytics.with(MobioApplication.this).setDeviceToken(token);

                        LogMobio.logD("MobioApplication", token);

                    }
                });
    }
}
