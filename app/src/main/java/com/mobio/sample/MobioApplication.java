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


        String tempJb = "{\n" +
                "  \"repeat\": false,\n" +
                "  \"type_repeat\": 1,\n" +
                "  \"concrete_time\": \"16h\",\n" +
                "  \"type_todo\":0,\n" +
                "  \"type\": 1,\n" +
                "  \"id\": 1,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"node_code\": \"EVENT\",\n" +
                "      \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de97\",\n" +
                "      \"node_name\": \"Phát sinh Event\",\n" +
                "      \"length\": 5,\n" +
                "      \"enable\": true,\n" +
                "      \"event_key\":\"sdk_mobile_test_time_visit_app\",\n" +
                "      \"event_data\": {\n" +
                "        \"time_visit\": 10,\n" +
                "        \"screen_name\": \"Home\"\n" +
                "      },\n" +
                "      \"data\": [\n" +
                "        {\n" +
                "          \"node_code\": \"PUSH_IN_APP\",\n" +
                "          \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d8\",\n" +
                "          \"node_name\": \"CTKM Thanh Toán Điện Thoại Viettel\",\n" +
                "          \"enable\": true,\n" +
                "          \"noti_response\": {\n" +
                "            \"type\": 0,\n" +
                "            \"source_screen\": \"Home\",\n" +
                "            \"des_screen\": \"Recharge\",\n" +
                "            \"title\": \"Home\",\n" +
                "            \"content\": \"Hello home\",\n" +
                "            \"data\": \"Hello home\"\n" +
                "          },\n" +
                "          \"data\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        String jb2 = "{\n" +
                "  \"repeat\": true,\n" +
                "  \"type_repeat\": 1,\n" +
                "  \"concrete_time\": \"16h\",\n" +
                "  \"type_todo\":0,\n" +
                "  \"type\": 1,\n" +
                "  \"id\": 2,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"node_code\": \"EVENT\",\n" +
                "      \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de97\",\n" +
                "      \"node_name\": \"Phát sinh Event\",\n" +
                "      \"length\": 5,\n" +
                "      \"enable\": true,\n" +
                "      \"event_key\":\"sdk_mobile_test_time_visit_app\",\n" +
                "      \"event_data\": {\n" +
                "        \"time_visit\": 10,\n" +
                "        \"screen_name\": \"Home\"\n" +
                "      },\n" +
                "      \"data\": [\n" +
                "        {\n" +
                "          \"node_code\": \"PUSH_IN_APP\",\n" +
                "          \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d8\",\n" +
                "          \"node_name\": \"CTKM Thanh Toán Điện Thoại Viettel\",\n" +
                "          \"enable\": true,\n" +
                "          \"noti_response\": {\n" +
                "            \"type\": 0,\n" +
                "            \"source_screen\": \"Home\",\n" +
                "            \"des_screen\": \"Recharge\",\n" +
                "            \"title\": \"Home 1\",\n" +
                "            \"content\": \"Hello home 1\",\n" +
                "            \"data\": \"Hello home\"\n" +
                "          },\n" +
                "          \"data\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        String jb3 = "{\n" +
                "  \"repeat\": true,\n" +
                "  \"type_repeat\": 1,\n" +
                "  \"concrete_time\": \"16h\",\n" +
                "  \"type_todo\":0,\n" +
                "  \"type\": 1,\n" +
                "  \"id\": 3,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"node_code\": \"EVENT\",\n" +
                "      \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de97\",\n" +
                "      \"node_name\": \"Phát sinh Event\",\n" +
                "      \"length\": 5,\n" +
                "      \"enable\": true,\n" +
                "      \"event_key\":\"sdk_mobile_test_time_visit_app\",\n" +
                "      \"event_data\": {\n" +
                "        \"time_visit\": 10,\n" +
                "        \"screen_name\": \"Home\"\n" +
                "      },\n" +
                "      \"data\": [\n" +
                "        {\n" +
                "          \"node_code\": \"PUSH_IN_APP\",\n" +
                "          \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d8\",\n" +
                "          \"node_name\": \"CTKM Thanh Toán Điện Thoại Viettel\",\n" +
                "          \"enable\": true,\n" +
                "          \"noti_response\": {\n" +
                "            \"type\": 0,\n" +
                "            \"source_screen\": \"Home\",\n" +
                "            \"des_screen\": \"Recharge\",\n" +
                "            \"title\": \"Home 2\",\n" +
                "            \"content\": \"Hello home 2\",\n" +
                "            \"data\": \"Hello home\"\n" +
                "          },\n" +
                "          \"data\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        JSONObject jsonObject = null;
        JSONObject jsonObject1 = null;
        JSONObject jsonObject2 = null;
        try {
            jsonObject = new JSONObject(tempJb);
            jsonObject1 = new JSONObject(jb2);
            jsonObject2 = new JSONObject(jb3);
            ValueMap vm = Analytics.getInstance().toMap(jsonObject);
            ValueMap vm2 = Analytics.getInstance().toMap(jsonObject1);
            ValueMap vm3 = Analytics.getInstance().toMap(jsonObject2);
            ArrayList<ValueMap> listJb = new ArrayList<>();
            listJb.add(vm);
            listJb.add(vm2);
            listJb.add(vm3);
            Analytics.getInstance().setJourneyJsonList(listJb);
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
