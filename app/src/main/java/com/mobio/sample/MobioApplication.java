package com.mobio.sample;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.models.ScreenConfigObject;
import com.mobio.analytics.client.models.ValueMap;
import com.mobio.analytics.client.utility.LogMobio;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MobioApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        HashMap<String, ScreenConfigObject> screenConfigObjectHashMap = new HashMap<>();
        screenConfigObjectHashMap.put("LoginActivity", new ScreenConfigObject("Login screen", "LoginActivity", new int[] {5, 10, 15}, LoginActivity.class, true));
        screenConfigObjectHashMap.put("HomeActivity", new ScreenConfigObject("Home", "HomeActivity", new int[] {10}, HomeActivity.class, false));
        screenConfigObjectHashMap.put("SendMoneyInActivity", new ScreenConfigObject("Transfer", "SendMoneyInActivity", new int[] {10}, SendMoneyInActivity.class, false));

        Analytics.Builder builder = new Analytics.Builder()
                .withApplication(this)
                .shouldTrackDeepLink(true)
                .shouldTrackAppLifeCycle(true)
                .shouldTrackScreenLifeCycle(true)
                .withActivityMap(screenConfigObjectHashMap)
                .withIntervalSecond(10)
                .shouldRecordScreen(true)
                .withDomainURL("https://api-test1.mobio.vn/dynamic-event/api/v1.0/")
                .withEndPoint("sync")
                .withApiToken("Basic f5e27185-b53d-4aee-a9b7-e0579c24d29d")
                .withMerchantId("1b99bdcf-d582-4f49-9715-1b61dfff3924");



        Analytics.setSingletonInstance(builder.build());

        String strEvent = "{\n" +
                "  \"events\": [{\n" +
                "    \"node_code\": \"EVENT\",\n" +
                "    \"children_node\": [\n" +
                "      {\n" +
                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d8\",\n" +
                "        \"priority\": \"normal\",\n" +
                "        \"complete\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d7\",\n" +
                "        \"priority\": \"immediate\",\n" +
                "        \"complete\": false\n" +
                "      }\n" +
                "    ],\n" +
                "    \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de97\",\n" +
                "    \"node_name\": \"Phát sinh Event\",\n" +
                "    \"length\": 5,\n" +
                "    \"event_key\": \"sdk_mobile_test_time_visit_app\",\n" +
                "    \"event_data\": {\n" +
                "      \"time_visit\": 10,\n" +
                "      \"screen_name\": \"Home\"\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"node_code\": \"EVENT\",\n" +
                "    \"children_node\": [\n" +
                "      {\n" +
                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d6\",\n" +
                "        \"priority\": \"immediate\",\n" +
                "        \"complete\": false\n" +
                "      }\n" +
                "    ],\n" +
                "    \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de97\",\n" +
                "    \"node_name\": \"Phát sinh Event\",\n" +
                "    \"length\": 5,\n" +
                "    \"event_key\": \"sdk_mobile_test_time_visit_app\",\n" +
                "    \"event_data\": {\n" +
                "      \"time_visit\": 10,\n" +
                "      \"screen_name\": \"Saving\"\n" +
                "    }\n" +
                "  }\n" +
                " ]\n" +
                "}";

        String strPush = "{\n" +
                "  \"pushes\": //data push in app\n" +
                "    [{\n" +
                "      \"type\":\"daily\",\n" +
                "      \"time\":\"8h\",\n" +
                "      \"jb_id\":\"abc\",\n" +
                "      \"node_code\": \"PUSH_IN_APP\",\n" +
                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d8\",\n" +
                "      \"node_name\": \"CTKM Thanh Toán Điện Thoại Viettel\",\n" +
                "      \"noti_response\": {\n" +
                "        \"type\": 0,\n" +
                "        \"source_screen\": \"Home\",\n" +
                "        \"des_screen\": \"Recharge\",\n" +
                "        \"title\": \"CTKM 1\",\n" +
                "        \"content\": \"Thanh Toán Điện Thoại Viettel\",\n" +
                "        \"data\": \"Hello home\" \n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\":\"daily\",\n" +
                "      \"time\":\"8h\",\n" +
                "      \"jb_id\":\"abc\",\n" +
                "      \"node_code\": \"PUSH_IN_APP\",\n" +
                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d7\",\n" +
                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
                "      \"noti_response\": {\n" +
                "        \"type\": 0,\n" +
                "        \"source_screen\": \"Home\",\n" +
                "        \"des_screen\": \"Saving\",\n" +
                "        \"title\": \"CTKM 2\",\n" +
                "        \"content\": \"Gửi tiết kiệm\",\n" +
                "        \"data\": \"Hello home\" \n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\":\"daily\",\n" +
                "      \"time\":\"8h\",\n" +
                "      \"jb_id\":\"abc\",\n" +
                "      \"node_code\": \"PUSH_IN_APP\",\n" +
                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d6\",\n" +
                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
                "      \"noti_response\": {\n" +
                "        \"type\": 0,\n" +
                "        \"source_screen\": \"Home\",\n" +
                "        \"des_screen\": \"Saving\",\n" +
                "        \"title\": \"CTKM 3\",\n" +
                "        \"content\": \"Gửi tiết kiệm nhập số tiền đi\",\n" +
                "        \"data\": \"Hello home\" \n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Analytics.getInstance().setBothEventAndPushJson(strEvent, strPush);

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

                        Analytics.getInstance().setDeviceToken(token);

                        LogMobio.logD("MobioApplication", token);

                    }
                });
    }
}
