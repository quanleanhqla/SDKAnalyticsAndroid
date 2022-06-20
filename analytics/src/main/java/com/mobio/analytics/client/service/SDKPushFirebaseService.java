package com.mobio.analytics.client.service;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.factory.ModelFactory;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.view.notification.RichNotification;

public class SDKPushFirebaseService extends FirebaseMessagingService {
    public SDKPushFirebaseService() {
    }

    @Override
    public void onNewToken(@NonNull String token) {
        MobioSDKClient.getInstance().handlePushNewToken(token);
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