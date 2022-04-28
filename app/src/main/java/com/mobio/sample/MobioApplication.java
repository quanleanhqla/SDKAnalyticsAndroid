package com.mobio.sample;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.old.ScreenConfigObject;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.Utils;

import java.util.HashMap;

public class MobioApplication extends Application {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();

        HashMap<String, ScreenConfigObject> screenConfigObjectHashMap = new HashMap<>();
        screenConfigObjectHashMap.put("LoginActivity", new ScreenConfigObject("Login screen", "LoginActivity", new int[] {10}, LoginActivity.class, true));
        screenConfigObjectHashMap.put("HomeActivity", new ScreenConfigObject("Home", "HomeActivity", new int[] {10}, HomeActivity.class, false));
        screenConfigObjectHashMap.put("SendMoneyInActivity", new ScreenConfigObject("Transfer", "SendMoneyInActivity", new int[] {10}, SendMoneyInActivity.class, false));

        MobioSDKClient.Builder builder = new MobioSDKClient.Builder()
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



        MobioSDKClient.setSingletonInstance(builder.build());

//        String strEvent = "{\n" +
//                "  \"events\": [{\n" +
//                "    \"node_code\": \"EVENT\",\n" +
//                "    \"children_node\": [\n" +
//                "      {\n" +
//                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d5\",\n" +
//                "        \"expire\": 1675914503005,\n" +
//                "        \"complete\": false,\n" +
//                "        \"type\": \"action\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d1\",\n" +
//                "        \"expire\": 1675914503001,\n" +
//                "        \"complete\": false,\n" +
//                "        \"type\": \"action\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d4\",\n" +
//                "        \"expire\": 1675914503004,\n" +
//                "        \"complete\": false,\n" +
//                "        \"type\": \"action\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d3\",\n" +
//                "        \"expire\": 1675914503003,\n" +
//                "        \"complete\": false,\n" +
//                "        \"type\": \"action\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d2\",\n" +
//                "        \"expire\": 1675914503002,\n" +
//                "        \"complete\": false,\n" +
//                "        \"type\": \"action\"\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de97\",\n" +
//                "    \"node_name\": \"Phát sinh Event\",\n" +
//                "    \"length\": 5,\n" +
//                "    \"event_key\": \"sdk_mobile_test_time_visit_app\",\n" +
//                "    \"event_data\": {\n" +
//                "      \"time_visit\": 10,\n" +
//                "      \"screen_name\": \"Home\"\n" +
//                "    }\n" +
//                "  },\n" +
//                "  {\n" +
//                "    \"node_code\": \"EVENT\",\n" +
//                "    \"children_node\": [\n" +
//                "      {\n" +
//                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d10\",\n" +
//                "        \"expire\": 1675914503010,\n" +
//                "        \"complete\": false,\n" +
//                "        \"type\": \"action\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d6\",\n" +
//                "        \"expire\": 1675914503006,\n" +
//                "        \"complete\": false,\n" +
//                "        \"type\": \"action\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d7\",\n" +
//                "        \"expire\": 1675914503007,\n" +
//                "        \"complete\": false,\n" +
//                "        \"type\": \"action\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d8\",\n" +
//                "        \"expire\": 1675914503008,\n" +
//                "        \"complete\": false,\n" +
//                "        \"type\": \"action\"\n" +
//                "      },\n" +
//                "      {\n" +
//                "        \"id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d9\",\n" +
//                "        \"expire\": 1675914503009,\n" +
//                "        \"complete\": false,\n" +
//                "        \"type\": \"action\"\n" +
//                "      }\n" +
//                "    ],\n" +
//                "    \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de98\",\n" +
//                "    \"node_name\": \"Phát sinh Event\",\n" +
//                "    \"length\": 5,\n" +
//                "    \"event_key\": \"sdk_mobile_test_screen_end_in_app\",\n" +
//                "    \"event_data\": {\n" +
//                "      \"screen_name\": \"Saving\"\n" +
//                "    }\n" +
//                "  }\n" +
//                " ]\n" +
//                "}";
//
//        String strPush = "{\n" +
//                "  \"pushes\":\n" +
//                "    [{\n" +
//                "      \"type\":\"daily\",\n" +
//                "      \"time\":\"8h\",\n" +
//                "      \"jb_id\":\"abc\",\n" +
//                "      \"expire\":1675914503008,\n" +
//                "      \"node_code\": \"PUSH_IN_APP\",\n" +
//                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d8\",\n" +
//                "      \"node_name\": \"CTKM Thanh Toán Điện Thoại Viettel\",\n" +
//                "      \"noti_response\": {\n" +
//                "        \"type\": 0,\n" +
//                "        \"source_screen\": \"Home\",\n" +
//                "        \"des_screen\": \"Recharge\",\n" +
//                "        \"title\": \"CTKM 8 event2\",\n" +
//                "        \"content\": \"Thanh Toán Điện Thoại Viettel\",\n" +
//                "        \"data\": \"Hello home\" \n" +
//                "      },\n" +
//                "      \"events_to_show\":[\"sdk_mobile_test_screen_start_in_app\", \"sdk_mobile_test_screen_end_in_app\"]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"type\":\"daily\",\n" +
//                "      \"time\":\"8h\",\n" +
//                "      \"jb_id\":\"abc\",\n" +
//                "      \"expire\": 1675914503007,\n" +
//                "      \"node_code\": \"PUSH_IN_APP\",\n" +
//                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d7\",\n" +
//                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
//                "      \"noti_response\": {\n" +
//                "        \"type\": 0,\n" +
//                "        \"source_screen\": \"Home\",\n" +
//                "        \"des_screen\": \"Saving\",\n" +
//                "        \"title\": \"CTKM 7 event2\",\n" +
//                "        \"content\": \"Gửi tiết kiệm\",\n" +
//                "        \"data\": \"Hello home\" \n" +
//                "      },\n" +
//                "      \"events_to_show\":[\"sdk_mobile_test_screen_start_in_app\"]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"type\":\"daily\",\n" +
//                "      \"time\":\"8h\",\n" +
//                "      \"jb_id\":\"abc\",\n" +
//                "      \"expire\": 1675914503006,\n" +
//                "      \"node_code\": \"PUSH_IN_APP\",\n" +
//                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d6\",\n" +
//                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
//                "      \"noti_response\": {\n" +
//                "        \"type\": 0,\n" +
//                "        \"source_screen\": \"Home\",\n" +
//                "        \"des_screen\": \"Saving\",\n" +
//                "        \"title\": \"CTKM 6 event2\",\n" +
//                "        \"content\": \"Gửi tiết kiệm nhập số tiền đi\",\n" +
//                "        \"data\": \"Hello home\" \n" +
//                "      },\n" +
//                "      \"events_to_show\":[\"sdk_mobile_test_screen_start_in_app\"]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"type\":\"daily\",\n" +
//                "      \"time\":\"8h\",\n" +
//                "      \"jb_id\":\"abc\",\n" +
//                "      \"expire\": 1675914503005,\n" +
//                "      \"node_code\": \"PUSH_IN_APP\",\n" +
//                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d5\",\n" +
//                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
//                "      \"noti_response\": {\n" +
//                "        \"type\": 0,\n" +
//                "        \"source_screen\": \"Home\",\n" +
//                "        \"des_screen\": \"Saving\",\n" +
//                "        \"title\": \"CTKM 5 event1\",\n" +
//                "        \"content\": \"Gửi tiết kiệm nhập số tiền đi\",\n" +
//                "        \"data\": \"Hello home\" \n" +
//                "      },\n" +
//                "      \"events_to_show\":[\"sdk_mobile_test_screen_start_in_app\"]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"type\":\"daily\",\n" +
//                "      \"time\":\"8h\",\n" +
//                "      \"jb_id\":\"abc\",\n" +
//                "      \"expire\": 1675914503001,\n" +
//                "      \"node_code\": \"PUSH_IN_APP\",\n" +
//                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d1\",\n" +
//                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
//                "      \"noti_response\": {\n" +
//                "        \"type\": 0,\n" +
//                "        \"source_screen\": \"Home\",\n" +
//                "        \"des_screen\": \"Transfer\",\n" +
//                "        \"title\": \"CTKM 1 event1\",\n" +
//                "        \"content\": \"Gửi tiết kiệm nhập số tiền đi\",\n" +
//                "        \"data\": \"Hello home\" \n" +
//                "      },\n" +
//                "      \"events_to_show\":[\"sdk_mobile_test_screen_start_in_app\"]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"type\":\"daily\",\n" +
//                "      \"time\":\"8h\",\n" +
//                "      \"jb_id\":\"abc\",\n" +
//                "      \"expire\": 1675914503002,\n" +
//                "      \"node_code\": \"PUSH_IN_APP\",\n" +
//                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d2\",\n" +
//                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
//                "      \"noti_response\": {\n" +
//                "        \"type\": 0,\n" +
//                "        \"source_screen\": \"Home\",\n" +
//                "        \"des_screen\": \"Transfer\",\n" +
//                "        \"title\": \"CTKM 2 event1\",\n" +
//                "        \"content\": \"Gửi tiết kiệm nhập số tiền đi\",\n" +
//                "        \"data\": \"Hello home\" \n" +
//                "      },\n" +
//                "      \"events_to_show\":[\"sdk_mobile_test_screen_start_in_app\"]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"type\":\"daily\",\n" +
//                "      \"time\":\"8h\",\n" +
//                "      \"jb_id\":\"abc\",\n" +
//                "      \"expire\": 1675914503009,\n" +
//                "      \"node_code\": \"PUSH_IN_APP\",\n" +
//                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d9\",\n" +
//                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
//                "      \"noti_response\": {\n" +
//                "        \"type\": 0,\n" +
//                "        \"source_screen\": \"Home\",\n" +
//                "        \"des_screen\": \"Saving\",\n" +
//                "        \"title\": \"CTKM 9 event2\",\n" +
//                "        \"content\": \"Gửi tiết kiệm nhập số tiền đi\",\n" +
//                "        \"data\": \"Hello home\" \n" +
//                "      },\n" +
//                "      \"events_to_show\":[\"sdk_mobile_test_screen_start_in_app\"]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"type\":\"daily\",\n" +
//                "      \"time\":\"8h\",\n" +
//                "      \"jb_id\":\"abc\",\n" +
//                "      \"expire\": 1675914503004,\n" +
//                "      \"node_code\": \"PUSH_IN_APP\",\n" +
//                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d4\",\n" +
//                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
//                "      \"noti_response\": {\n" +
//                "        \"type\": 0,\n" +
//                "        \"source_screen\": \"Home\",\n" +
//                "        \"des_screen\": \"Saving\",\n" +
//                "        \"title\": \"CTKM 4 event1\",\n" +
//                "        \"content\": \"Gửi tiết kiệm nhập số tiền đi\",\n" +
//                "        \"data\": \"Hello home\" \n" +
//                "      },\n" +
//                "      \"events_to_show\":[\"sdk_mobile_test_screen_start_in_app\"]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"type\":\"daily\",\n" +
//                "      \"time\":\"8h\",\n" +
//                "      \"jb_id\":\"abc\",\n" +
//                "      \"expire\": 1675914503010,\n" +
//                "      \"node_code\": \"PUSH_IN_APP\",\n" +
//                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d10\",\n" +
//                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
//                "      \"noti_response\": {\n" +
//                "        \"type\": 0,\n" +
//                "        \"source_screen\": \"Home\",\n" +
//                "        \"des_screen\": \"Saving\",\n" +
//                "        \"title\": \"CTKM 10 event2\",\n" +
//                "        \"content\": \"Gửi tiết kiệm nhập số tiền đi\",\n" +
//                "        \"data\": \"Hello home\" \n" +
//                "      },\n" +
//                "      \"events_to_show\":[\"sdk_mobile_test_screen_start_in_app\"]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"type\":\"daily\",\n" +
//                "      \"time\":\"8h\",\n" +
//                "      \"jb_id\":\"abc\",\n" +
//                "      \"expire\": 1675914503003,\n" +
//                "      \"node_code\": \"PUSH_IN_APP\",\n" +
//                "      \"node_id\": \"92fe3c7f-b40e-4b7e-b9fb-a94f398bf6d3\",\n" +
//                "      \"node_name\": \"CTKM Gửi tiết kiệm\",\n" +
//                "      \"noti_response\": {\n" +
//                "        \"type\": 0,\n" +
//                "        \"source_screen\": \"Home\",\n" +
//                "        \"des_screen\": \"Saving\",\n" +
//                "        \"title\": \"CTKM 3 event1\",\n" +
//                "        \"content\": \"Gửi tiết kiệm nhập số tiền đi\",\n" +
//                "        \"data\": \"Hello home\" \n" +
//                "      },\n" +
//                "      \"events_to_show\":[\"sdk_mobile_test_screen_start_in_app\"]\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
//
//        String strJourney = "{\n" +
//                "  \"journeys\": [\n" +
//                "    {\n" +
//                "      \"journey_id\": \"23997788-f69a-4770-89cc-7ce8a00f6672\",\n" +
//                "      \"status\": \"todo\",\n" +
//                "      \"events\": [\n" +
//                "        {\n" +
//                "          \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de97\",\n" +
//                "          \"status\": \"done\",\n" +
//                "          \"event_key\": \"sdk_mobile_test_time_visit_app\",\n" +
//                "          \"event_data\": {\n" +
//                "            \"time_visit\": 10,\n" +
//                "            \"screen_name\": \"Home\"\n" +
//                "          }\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de98\",\n" +
//                "          \"status\": \"pending\",\n" +
//                "          \"event_key\": \"sdk_mobile_test_time_visit_app\",\n" +
//                "          \"event_data\": {\n" +
//                "            \"time_visit\": 10,\n" +
//                "            \"screen_name\": \"Transfer\"\n" +
//                "          }\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de96\",\n" +
//                "          \"status\": \"todo\",\n" +
//                "          \"event_key\": \"sdk_mobile_test_time_visit_app\",\n" +
//                "          \"event_data\": {\n" +
//                "            \"time_visit\": 10,\n" +
//                "            \"screen_name\": \"Home\"\n" +
//                "          }\n" +
//                "        }\n" +
//                "      ]\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"journey_id\": \"23997788-f69a-4770-89cc-7ce8a00f6673\",\n" +
//                "      \"status\": \"todo\",\n" +
//                "      \"events\": [\n" +
//                "        {\n" +
//                "          \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de95\",\n" +
//                "          \"status\": \"pending\",\n" +
//                "          \"event_key\": \"sdk_mobile_test_time_visit_app\",\n" +
//                "          \"event_data\": {\n" +
//                "            \"time_visit\": 10,\n" +
//                "            \"screen_name\": \"Recharge\"\n" +
//                "          }\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de94\",\n" +
//                "          \"status\": \"todo\",\n" +
//                "          \"event_key\": \"sdk_mobile_test_time_visit_app\",\n" +
//                "          \"event_data\": {\n" +
//                "            \"time_visit\": 10,\n" +
//                "            \"screen_name\": \"Home\"\n" +
//                "          }\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"node_id\": \"42319b1e-3b46-4a3b-8081-95e08c24de93\",\n" +
//                "          \"status\": \"todo\",\n" +
//                "          \"event_key\": \"sdk_mobile_test_time_visit_app\",\n" +
//                "          \"event_data\": {\n" +
//                "            \"time_visit\": 10,\n" +
//                "            \"screen_name\": \"Saving\"\n" +
//                "          }\n" +
//                "        }\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
//
//        MobioSDKClient.getInstance().setBothEventAndPushJson(strEvent, strPush);
//        MobioSDKClient.getInstance().setCurrentJsonJourney(strJourney);

        Utils.listAllActivities(this);
        initAndGenTokenFirebase(this);
    }

    private void initAndGenTokenFirebase(Context context){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setUserProperty("mobio_id", Utils.getIMEIDeviceId(context));

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            LogMobio.logD("Firbase ", "Fetching FCM registration token failed" + task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        MobioSDKClient.getInstance().setDeviceToken(token);
                        mFirebaseAnalytics.setUserProperty("token_id", token);

                        LogMobio.logD("Firbase ", token);

                    }
                });
    }
}
