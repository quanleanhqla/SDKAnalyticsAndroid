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
    public void onNewToken(String token) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            try {
                Push push = createPush(remoteMessage.getData().toString());

                if (push.getAlert().getContentType().equals(Push.Alert.TYPE_POPUP)) {
                    long actionTime = System.currentTimeMillis();
                    MobioSDKClient.getInstance().track(ModelFactory.createBaseListForPopup(push, "popup", "receive", actionTime), actionTime);
                }

                if (SharedPreferencesUtils.getBool(this, SharedPreferencesUtils.KEY_APP_FOREGROUD)) {
                    MobioSDKClient.getInstance().showGlobalPopup(push);
                } else {
                    int reqId = (int) (Math.random() * 10000);
                    MobioSDKClient.getInstance().showGlobalNotification(push, reqId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {


            String title = remoteMessage.getNotification().getTitle();
            String detail = remoteMessage.getNotification().getBody();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

    private Push createPush(String remoteMessage) {
        Push message = Push.convertJsonStringtoPush(remoteMessage);
        Push.Alert alert = message.getAlert();
        String contentType = alert.getContentType();

        if (contentType.equals(Push.Alert.TYPE_POPUP)) {
            alert.putTitle("Thông báo");
            alert.putBody("Bạn có 1 thông báo mới!");
        }
        return message;
    }
}