package com.mobio.analytics.client.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.ModelFactory;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.model.digienty.ValueMap;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;

public class SDKPushFirebaseService extends FirebaseMessagingService {
    public SDKPushFirebaseService() {
    }

    @Override
    public void onNewToken(String token) {
        LogMobio.logD("MyFirebaseMessagingService", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        LogMobio.logD("SDKPushFirebaseService", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            LogMobio.logD("SDKPushFirebaseService", "Message data payload: " + remoteMessage.getData());
            try {
                Push push = createPush(remoteMessage.getData().toString());

                MobioSDKClient.getInstance().track(ModelFactory.createBaseList(push, "receive"));

                if (SharedPreferencesUtils.getBool(this, SharedPreferencesUtils.KEY_APP_FOREGROUD)) {
                    MobioSDKClient.getInstance().showGlobalPopup(push);
                } else {
                    MobioSDKClient.getInstance().showGlobalNotification(push, (int) (Math.random() * 10000));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            LogMobio.logD("SDKPushFirebaseService", "Message Notification Body: " + remoteMessage.getNotification().getBody());



            String title = remoteMessage.getNotification().getTitle();
            String detail = remoteMessage.getNotification().getBody();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

    private Push createPush(String remoteMessage){
        Push message = Push.convertJsonStringtoPush(remoteMessage);
        Push.Alert alert = message.getAlert();
        String contentType = alert.getContentType();

        if(contentType.equals(Push.Alert.TYPE_POPUP)){
            alert.putTitle("Thông báo");
            alert.putBody("Bạn có 1 thông báo mới!");
        }
        LogMobio.logD("QuanLA", new Gson().toJson(message));
        return message;
    }
}