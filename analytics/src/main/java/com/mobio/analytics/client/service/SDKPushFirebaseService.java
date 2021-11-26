package com.mobio.analytics.client.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.utility.LogMobio;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
        LogMobio.logD("MyFirebaseMessagingService", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            LogMobio.logD("MyFirebaseMessagingService", "Message data payload: " + remoteMessage.getData());

            try {
                JSONObject json = (JSONObject) new JSONTokener(remoteMessage.getData().toString()).nextValue();
                JSONObject json2 = json.getJSONObject("alert");
                String title = (String) json2.get("title");
                String body = (String) json2.get("body");

                LogMobio.logD("MyFirebaseMessagingService", "title: " + title);
                LogMobio.logD("MyFirebaseMessagingService", "body: " + body);

                NotiResponseObject notiResponseObject = new NotiResponseObject.Builder()
                        .withType(NotiResponseObject.TYPE_NATIVE).withContent(body)
                        .withData(body).withTitle(title)
                        .build();

                if (title.contains("[Case demo 1]")) {
                    notiResponseObject.setDes_screen("Recharge");
                }

                Analytics.getInstance().showGlobalPopup(notiResponseObject);
            } catch (JSONException e) {
                e.printStackTrace();

                if (remoteMessage.getNotification() != null) {
                    LogMobio.logD("MyFirebaseMessagingService", "Message Notification Body: " + remoteMessage.getNotification().getBody());


                    String title = remoteMessage.getNotification().getTitle();
                    String detail = remoteMessage.getNotification().getBody();


                    NotiResponseObject notiResponseObject = new NotiResponseObject.Builder()
                            .withType(NotiResponseObject.TYPE_NATIVE).withContent(detail)
                            .withData(detail).withTitle(title)
                            .build();

                    Analytics.getInstance().showGlobalPopup(notiResponseObject);

//            Intent i = new Intent();
//            i.setAction(RECEIVE_NOTIFICATION_ACTION);
//            i.putExtra(INTENT_KEY_TITLE, title);
//            i.putExtra(INTENT_KEY_DETAIL, detail);
//            sendBroadcast(i);
                }

            }

            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
        }

    }
}