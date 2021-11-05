package com.mobio.sample;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.utility.LogMobio;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
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
                JSONObject json= (JSONObject) new JSONTokener(remoteMessage.getData().toString()).nextValue();
                JSONObject json2 = json.getJSONObject("alert");
                String title = (String) json2.get("title");
                String body = (String) json2.get("body");

                LogMobio.logD("MyFirebaseMessagingService", "title: " + title);
                LogMobio.logD("MyFirebaseMessagingService", "body: " + body);

//                Intent i = new Intent();
//                i.setAction(RECEIVE_NOTIFICATION_ACTION);
//                i.putExtra(INTENT_KEY_TITLE, title);
//                i.putExtra(INTENT_KEY_DETAIL, body);
//                sendBroadcast(i);

                Analytics.getInstance().showGlobalPopup(title, body, null, null, "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

            if (remoteMessage.getNotification() != null) {
                LogMobio.logD("MyFirebaseMessagingService", "Message Notification Body: " + remoteMessage.getNotification().getBody());



                String title = remoteMessage.getNotification().getTitle();
                String detail = remoteMessage.getNotification().getBody();


                Analytics.getInstance().showGlobalPopup(title, detail, null, null, "");

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

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance(this).beginWith(work).enqueue();

    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        LogMobio.logD("MyFirebaseMessagingService", "Short lived task is done.");
    }
}