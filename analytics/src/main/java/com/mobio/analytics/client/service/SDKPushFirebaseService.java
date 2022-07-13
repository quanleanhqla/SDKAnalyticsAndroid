package com.mobio.analytics.client.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.inapp.notification.RichNotification;

public class SDKPushFirebaseService extends FirebaseMessagingService {
    public SDKPushFirebaseService() {
    }

    @Override
    public void onNewToken(@NonNull String token) {
        MobioSDKClient.getInstance().setDeviceToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        MobioSDKClient.getInstance().handlePushMessage(remoteMessage);

        if (remoteMessage.getNotification() != null) {


            String title = remoteMessage.getNotification().getTitle();
            String detail = remoteMessage.getNotification().getBody();
        }

    }
}