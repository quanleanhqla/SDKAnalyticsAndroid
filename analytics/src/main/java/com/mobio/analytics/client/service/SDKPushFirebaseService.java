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
                            "<html lang=\"en\">\n" +
                            "<head>\n" +
                            "    <title>Lunar -  Free Bootstrap Modal and Popups  </title>\n" +
                            "    <!-- Required meta tags -->\n" +
                            "    <meta charset=\"utf-8\">\n" +
                            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=0\">\n" +
                            "    <!-- Bootstrap CSS -->\n" +
                            "    <link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/bootstrap/css/bootstrap.min.css\">\n" +
                            "    <!-- Lunar CSS -->\n" +
                            "    <link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/css/lunar.css\">\n" +
                            "    <!--<link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/css/demo.css\">-->\n" +
                            "    <!-- Fonts -->\n" +
                            "    <link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/css/animate.min.css\">\n" +
                            "    <link href=\"https://fonts.googleapis.com/css?family=Work+Sans:600\" rel=\"stylesheet\">\n" +
                            "    <link href=\"https://fonts.googleapis.com/css?family=Overpass:300,400,600,700,800,900\" rel=\"stylesheet\">\n" +
                            "    <link rel=\"icon\" type=\"image/x-icon\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/img/lunar.png\"/>\n" +
                            "    <link rel=\"icon\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/img/lunar.png\" type=\"image/png\" sizes=\"16x16\">\n" +
                            "</head>\n" +
                            "<body class=\"modal-open\">\n" +
                            "    <!-- Modal -->\n" +
                            "    <div class=\"modal fade modal-bottom-right show\" id=\"demoModal\"  tabindex=\"-1\" role=\"dialog\"\n" +
                            "         aria-labelledby=\"demoModal\" aria-hidden=\"true\" style=\"display: block;\">\n" +
                            "\n" +
                            "        <div class=\"modal-dialog  modal-sm\" role=\"document\">\n" +
                            "\n" +
                            "            <div class=\"modal-content\">\n" +
                            "                <button type=\"button\" class=\"close size-sm light\" data-dismiss=\"modal\"\n" +
                            "                        aria-label=\"Close\" onclick=\"sdk.dismissMessage();\">\n" +
                            "                    <span aria-hidden=\"true\">&times;</span>\n" +
                            "                </button>\n" +
                            "                <div class=\"modal-body bg-rhino px-sm-3 py-sm-3\" >\n" +
                            "                    <div class=\"text-center pb-2\"><img src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/img/megaphone.png\" alt=\"\"></div>\n" +
                            "                    <h3 class=\"text-white text-center \">CTKM</h3>\n" +
                            "                    <p class=\"text-white-50\">CTKM Thanh toán điện thoại Viettel được tặng 10% chỉ có tại Mobio Bank trong hôm nay!</p>\n" +
                            "                    <div class=\"pt-2 text-center\">\n" +
                            "                        <a class=\"btn btn-cstm-light \" data-dismiss=\"modal\" aria-label=\"Close\" onclick=\"sdk.trackClick(); \">Đồng ý</a>\n" +
                            "                    </div>\n" +
                            "\n" +
                            "                </div>\n" +
                            "\n" +
                            "\n" +
                            "            </div>\n" +
                            "        </div>\n" +
                            "    </div>\n" +
                            "    <!-- Modal Ends -->\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "<!--end content here-->\n" +
                            "<div id=\"image\"></div>\n" +
                            "<script src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/js/jquery.min.js\"></script>\n" +
                            "<script src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/js/popper.min.js\"></script>\n" +
                            "<script src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/bootstrap/js/bootstrap.min.js\"></script>\n" +
                            "<div class=\"modal-backdrop show\"></div>\n" +
                            "</body>\n" +
                            "</html>");
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
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <title>Lunar -  Free Bootstrap Modal and Popups  </title>\n" +
                        "    <!-- Required meta tags -->\n" +
                        "    <meta charset=\"utf-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=0\">\n" +
                        "    <!-- Bootstrap CSS -->\n" +
                        "    <link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/bootstrap/css/bootstrap.min.css\">\n" +
                        "    <!-- Lunar CSS -->\n" +
                        "    <link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/css/lunar.css\">\n" +
                        "    <!--<link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/css/demo.css\">-->\n" +
                        "    <!-- Fonts -->\n" +
                        "    <link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/css/animate.min.css\">\n" +
                        "    <link href=\"https://fonts.googleapis.com/css?family=Work+Sans:600\" rel=\"stylesheet\">\n" +
                        "    <link href=\"https://fonts.googleapis.com/css?family=Overpass:300,400,600,700,800,900\" rel=\"stylesheet\">\n" +
                        "    <link rel=\"icon\" type=\"image/x-icon\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/img/lunar.png\"/>\n" +
                        "    <link rel=\"icon\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/img/lunar.png\" type=\"image/png\" sizes=\"16x16\">\n" +
                        "</head>\n" +
                        "<body class=\"modal-open\">\n" +
                        "    <!-- Modal -->\n" +
                        "    <div class=\"modal fade modal-bottom-right show\" id=\"demoModal\"  tabindex=\"-1\" role=\"dialog\"\n" +
                        "         aria-labelledby=\"demoModal\" aria-hidden=\"true\" style=\"display: block;\">\n" +
                        "\n" +
                        "        <div class=\"modal-dialog  modal-sm\" role=\"document\">\n" +
                        "\n" +
                        "            <div class=\"modal-content\">\n" +
                        "                <button type=\"button\" class=\"close size-sm light\" data-dismiss=\"modal\"\n" +
                        "                        aria-label=\"Close\" onclick=\"sdk.dismissMessage();\">\n" +
                        "                    <span aria-hidden=\"true\">&times;</span>\n" +
                        "                </button>\n" +
                        "                <div class=\"modal-body bg-rhino px-sm-3 py-sm-3\" >\n" +
                        "                    <div class=\"text-center pb-2\"><img src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/img/megaphone.png\" alt=\"\"></div>\n" +
                        "                    <h3 class=\"text-white text-center \">CTKM</h3>\n" +
                        "                    <p class=\"text-white-50\">CTKM Thanh toán điện thoại Viettel được tặng 10% chỉ có tại Mobio Bank trong hôm nay!</p>\n" +
                        "                    <div class=\"pt-2 text-center\">\n" +
                        "                        <a class=\"btn btn-cstm-light \" data-dismiss=\"modal\" aria-label=\"Close\" onclick=\"sdk.trackClick(); \">Đồng ý</a>\n" +
                        "                    </div>\n" +
                        "\n" +
                        "                </div>\n" +
                        "\n" +
                        "\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "    <!-- Modal Ends -->\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "<!--end content here-->\n" +
                        "<div id=\"image\"></div>\n" +
                        "<script src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/js/jquery.min.js\"></script>\n" +
                        "<script src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/js/popper.min.js\"></script>\n" +
                        "<script src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/bootstrap/js/bootstrap.min.js\"></script>\n" +
                        "<div class=\"modal-backdrop show\"></div>\n" +
                        "</body>\n" +
                        "</html>");
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