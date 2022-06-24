package com.mobio.sample;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobio.analytics.client.MobioSDKClient;

public class AppPushFirebaseService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        MobioSDKClient.getInstance().setDeviceToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        MobioSDKClient.getInstance().handlePushMessage(remoteMessage);
    }
}
