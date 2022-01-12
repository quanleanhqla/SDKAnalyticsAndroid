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


        String event1 = "{\n" +
                "      \"node_code\": \"EVENT\",\n" +
                "      \"children_node\": [{\"id\":\"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d8\",\n" +
                "      \"priority\":\"immediate\", \"complete\":false}, {\"id\":\"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d7\", \"priority\":\"normal\", \"complete\":false}],\n" +
                "      \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de97\",\n" +
                "      \"node_name\": \"Phát sinh Event\",\n" +
                "      \"length\": 5,\n" +
                "      \"event_key\":\"sdk_mobile_test_time_visit_app\",\n" +
                "      \"event_data\": {\n" +
                "        \"time_visit\": 10,\n" +
                "        \"screen_name\": \"Home\"\n" +
                "      }\n" +
                "    }";

        String event2 = "{\n" +
                "  \"data_event\": //data event\n" +
                "    {\n" +
                "      \"node_code\": \"EVENT\",\n" +
                "      \"children_node\": [{\"id\":\"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d8\",\n" +
                "      \"priority\":\"immediate\", \"complete\":false}, {\"id\":\"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d7\", \"priority\":\"normal\", \"complete\":false}],\n" +
                "      \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de97\",\n" +
                "      \"node_name\": \"Phát sinh Event\",\n" +
                "      \"length\": 5,\n" +
                "      \"event_key\":\"sdk_mobile_test_time_visit_app\",\n" +
                "      \"event_data\": {\n" +
                "        \"time_visit\": 10,\n" +
                "        \"screen_name\": \"Saving\"\n" +
                "      }\n" +
                "    }\n" +
                "}";

        String push1 = "{\n" +
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
                "    }";

        String push2 = "{\n" +
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
                "    }";


        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(event1);
            ValueMap vm = Analytics.getInstance().toMap(jsonObject);
            ArrayList<ValueMap> events = new ArrayList<>();
            events.add(vm);

            jsonObject = new JSONObject(event2);
            vm = Analytics.getInstance().toMap(jsonObject);
            events.add(vm);

            jsonObject = new JSONObject(push1);
            vm = Analytics.getInstance().toMap(jsonObject);
            ArrayList<ValueMap> pushes = new ArrayList<>();
            pushes.add(vm);

            jsonObject = new JSONObject(push2);
            vm = Analytics.getInstance().toMap(jsonObject);
            pushes.add(vm);

            Analytics.getInstance().setBothEventAndPushList(events, pushes);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
