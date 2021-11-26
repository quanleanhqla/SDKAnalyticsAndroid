package com.mobio.analytics.client.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;

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
        LogMobio.logD("SDKPushFirebaseService", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            LogMobio.logD("SDKPushFirebaseService", "Message data payload: " + remoteMessage.getData());
            try {
                JSONObject json= (JSONObject) new JSONTokener(remoteMessage.getData().toString()).nextValue();
                JSONObject json2 = json.getJSONObject("alert");
                String title = (String) json2.get("title");
                String body = (String) json2.get("body");

                LogMobio.logD("SDKPushFirebaseService", "title: " + title);
                LogMobio.logD("SDKPushFirebaseService", "body: " + body);
                NotiResponseObject notiResponseObject = new NotiResponseObject.Builder().withContent(body)
                        .withData(body).withTitle(title)
                        .build();
                if (title.contains("[Case Demo 1]")) {
                    notiResponseObject.setType(NotiResponseObject.TYPE_NATIVE);
                    notiResponseObject.setDes_screen("Recharge");
                } else if (title.contains("[Case Demo 2]")) {
                    notiResponseObject.setType(NotiResponseObject.TYPE_NATIVE);
                    notiResponseObject.setDes_screen("Saving");
                }
                else if(title.contains("[HTML]")){
                    notiResponseObject.setType(NotiResponseObject.TYPE_HTML);
                    notiResponseObject.setDes_screen("Recharge");
                    notiResponseObject.setData("<!doctype html>\n" +
                            "<html>\n" +
                            "<head>\n" +
                            "<meta charset=\"utf-8\">\n" +
                            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0\"/>\n" +
                            "<link href=\"https://fonts.googleapis.com/css?family=Roboto+Slab\" rel=\"stylesheet\">\n" +
                            "<title>freedownloads</title>\n" +
                            "</head>\n" +
                            "\n" +
                            "<body>\n" +
                            "<section class=\"popup-graybox\">\n" +
                            "<div class=\"freedownloads-popup-sec\" style=\"background: #8e8e41 url(images/freedownloads.jpg) no-repeat center center;background-size: cover;\" >\n" +
                            "  <h2 data-edit=\"text\" >CTKM</h2>\n" +
                            "  <h4 data-edit=\"text\" >Unlimited Free Downloads Of Brochures<br>\n" +
                            "    Flyers, Postcards and much more...</h4>\n" +
                            "  <div class=\"freedownloads-email-sec\">\n" +
                            "    <input type=\"email\" class=\"freedownloadsemail-input\" data-edit=\"placeholder\"   placeholder=\"Enter your email id\">\n" +
                            "    <button class=\"freedownloads-input-btn\" type=\"submit\" onclick=\"sdk.trackClick();\">signup</button>\n" +
                            "  </div>\n" +
                            "  <h6 data-edit=\"text\" >* Terms and Conditions Apply</h6>\n" +
                            "  <button class=\"freedownloads-cls-btn close-btn\" onclick=\"sdk.dismissMessage();\">X</button>\n" +
                            "</div>\n" +
                            "</section>\n" +
                            "</body>\n" +
                            "</html>\n");
                }

                if(SharedPreferencesUtils.getBool(this, SharedPreferencesUtils.KEY_APP_FOREGROUD)) {
                    Analytics.getInstance().showGlobalPopup(notiResponseObject);
                }
                else {
                    Analytics.getInstance().showGlobalNotification(notiResponseObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            LogMobio.logD("SDKPushFirebaseService", "Message Notification Body: " + remoteMessage.getNotification().getBody());



            String title = remoteMessage.getNotification().getTitle();
            String detail = remoteMessage.getNotification().getBody();

            NotiResponseObject notiResponseObject = new NotiResponseObject.Builder().withContent(detail)
                    .withData(detail).withTitle(title)
                    .build();
            if (title.contains("[Case Demo 1]")) {
                notiResponseObject.setDes_screen("Recharge");
            } else if (title.contains("[Case Demo 2]")) {
                notiResponseObject.setDes_screen("Saving");
            }
            else if(title.contains("[HTML]")){
                notiResponseObject.setType(NotiResponseObject.TYPE_HTML);
                notiResponseObject.setDes_screen("Recharge");
                notiResponseObject.setData("<!doctype html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta charset=\"utf-8\">\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0\"/>\n" +
                        "<link href=\"https://fonts.googleapis.com/css?family=Roboto+Slab\" rel=\"stylesheet\">\n" +
                        "<title>freedownloads</title>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body>\n" +
                        "<section class=\"popup-graybox\">\n" +
                        "<div class=\"freedownloads-popup-sec\" style=\"background: #8e8e41 url(images/freedownloads.jpg) no-repeat center center;background-size: cover;\" >\n" +
                        "  <h2 data-edit=\"text\" >CTKM</h2>\n" +
                        "  <h4 data-edit=\"text\" >Unlimited Free Downloads Of Brochures<br>\n" +
                        "    Flyers, Postcards and much more...</h4>\n" +
                        "  <div class=\"freedownloads-email-sec\">\n" +
                        "    <input type=\"email\" class=\"freedownloadsemail-input\" data-edit=\"placeholder\"   placeholder=\"Enter your email id\">\n" +
                        "    <button class=\"freedownloads-input-btn\" type=\"submit\" onclick=\"sdk.trackClick();\">signup</button>\n" +
                        "  </div>\n" +
                        "  <h6 data-edit=\"text\" >* Terms and Conditions Apply</h6>\n" +
                        "  <button class=\"freedownloads-cls-btn close-btn\" onclick=\"sdk.dismissMessage();\">X</button>\n" +
                        "</div>\n" +
                        "</section>\n" +
                        "</body>\n" +
                        "</html>\n");
            }

            if(SharedPreferencesUtils.getBool(this, SharedPreferencesUtils.KEY_APP_FOREGROUD)) {
                Analytics.getInstance().showGlobalPopup(notiResponseObject);
            }
            else {
                Analytics.getInstance().showGlobalNotification(notiResponseObject);
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }
}