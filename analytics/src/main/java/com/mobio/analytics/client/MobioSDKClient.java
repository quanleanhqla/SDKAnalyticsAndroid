package com.mobio.analytics.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobio.analytics.client.models.AppObject;
import com.mobio.analytics.client.models.DataObject;
import com.mobio.analytics.client.models.DeviceObject;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.models.OsObject;
import com.mobio.analytics.client.models.PropertiesObject;
import com.mobio.analytics.client.models.ValueMap;
import com.mobio.analytics.client.models.ScreenConfigObject;
import com.mobio.analytics.client.receiver.AlarmReceiver;
import com.mobio.analytics.client.receiver.NetworkChangeReceiver;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;
import com.mobio.analytics.client.view.GlobalNotification;
import com.mobio.analytics.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;


import retrofit2.Response;

public class MobioSDKClient {
    private static final String TAG = MobioSDKClient.class.getName();
    public static final String DEMO_EVENT = "android_event";
    public static final String SDK_Mobile_Test_Click_Button_In_App = "sdk_mobile_test_click_button_in_app";
    public static final String SDK_Mobile_Test_Identify_App = "sdk_mobile_test_identify_app";
    public static final String SDK_Mobile_Test_Time_Visit_App = "sdk_mobile_test_time_visit_app";
    public static final String SDK_Mobile_Test_Screen_End_In_App = "sdk_mobile_test_screen_end_in_app";
    public static final String SDK_Mobile_Test_Screen_Start_In_App = "sdk_mobile_test_screen_start_in_app";
    public static final String SDK_Mobile_Test_Open_App = "sdk_mobile_test_open_app";
    public static final String SDK_Mobile_Test_Open_Update_App = "sdk_mobile_test_open_update_app";
    public static final String SDK_Mobile_Test_Open_First_App = "sdk_mobile_test_open_first_app";
    public static final String SDK_Mobile_Test_Open_Notification_App = "sdk_mobile_test_open_notification_app";
    public static final String SDK_Mobile_Test_Close_Notification_App = "sdk_mobile_test_close_notification_app";
    public static final String SDK_Mobile_Test_Open_Popup_App = "sdk_mobile_test_open_popup_app";
    public static final String SDK_Mobile_Test_Close_Popup_App = "sdk_mobile_test_close_popup_app";
    public static final String SDK_Mobile_Test_Receive_Push_In_App = "sdk_mobile_test_receive_push_in_app";

    public static final int TYPE_LOGIN_SUCCESS = 1;
    public static final int TYPE_TRANSFER_SUCCESS = 2;
    public static final int TYPE_TRANSFER = 3;
    public static final int TYPE_CONFIRM_TRANSFER = 4;
    public static final int TYPE_APP_LIFECYCLE = 7;
    public static final int TYPE_SCREEN_LIFECYCLE = 8;
    public static final int TYPE_CLICK = 9;
    public static final int TYPE_SCREEN_START = 11;
    public static final int TYPE_SCREEN_END = 22;

    public static final int REQUEST_CODE = 1001;

    @SuppressLint("StaticFieldLeak")
    static volatile MobioSDKClient singleton = null;
    private Application application;
    private MobioSDKLifecycleCallback mobioSDKLifecycleCallback;
    private boolean shouldTrackAppLifecycle;
    private boolean shouldTrackScreenLifecycle;
    private boolean shouldTrackDeepLink;
    private boolean shouldRecordScreen;
    private boolean shouldTrackScroll;
    private DataObject cacheDataObject;
    private String apiToken;
    private String merchantId;
    private int intervalSecond;
    private String deviceToken;
    private ArrayList<ValueMap> listDataWaitToSend;
    private String domainURL;
    private String endPoint;
    private HashMap<String, ScreenConfigObject> configActivityMap;
    private ValueMap cacheValueMap;

    private ArrayList<ValueMap> currentJsonEvent;
    private ArrayList<ValueMap> currentJsonPush;
    private ArrayList<ValueMap> pendingJsonPush;

    private AlarmManager alarmManager;

    private final ExecutorService analyticsExecutor;
    private final ScheduledExecutorService sendSyncScheduler;

    public static MobioSDKClient getInstance() {
        synchronized (MobioSDKClient.class) {
            if (singleton == null) {
                singleton = new Builder().build();
            }
        }
        return singleton;
    }

    public MobioSDKClient(Builder builder) {
        application = builder.mApplication;
        shouldTrackAppLifecycle = builder.mShouldTrackAppLifecycle;
        shouldTrackScreenLifecycle = builder.mShouldTrackScreenLifecycle;
        shouldTrackDeepLink = builder.mShouldTrackDeepLink;
        shouldRecordScreen = builder.mShouldRecordScreen;
        intervalSecond = builder.mIntervalSecond;
        shouldTrackScroll = builder.mShouldTrackScroll;
        apiToken = builder.mApiToken;
        merchantId = builder.mMerchantId;
        deviceToken = builder.mDeviceToken;
        domainURL = builder.mDomainURL;
        endPoint = builder.mEndPoint;
        configActivityMap = builder.mActivityMap;

        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_MERCHANT_ID, merchantId);
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_API_TOKEN, apiToken);
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_BASE_URL, domainURL);
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_ENDPOINT, endPoint);

        analyticsExecutor = Executors.newSingleThreadExecutor();
        sendSyncScheduler = Executors.newScheduledThreadPool(1);
//        sendSyncScheduler.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//                //Todo
//                autoSendSync();
//
//            }
//        }, intervalSecond, intervalSecond, TimeUnit.SECONDS);

        mobioSDKLifecycleCallback = new MobioSDKLifecycleCallback(this, shouldTrackAppLifecycle, shouldTrackScreenLifecycle,
                shouldTrackDeepLink, shouldRecordScreen, shouldTrackScroll, application, configActivityMap);

        application.registerActivityLifecycleCallbacks(mobioSDKLifecycleCallback);

        if (application.getApplicationContext() != null) {
            alarmManager = (AlarmManager) application.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        }

        AppObject appObject = Utils.getAppObject(application);
        DeviceObject deviceObject = Utils.getDeviceObject();
        OsObject osObject = Utils.getOsObject();
        PropertiesObject propertiesObject = Utils.getProperties();

        cacheDataObject = new DataObject.Builder()
                .withContext(Utils.getContextObject(application))
                .withProfileInfoObject(Utils.getProfileCreateDeviceObject(application))
                .withAnonymousId(Build.ID)
                .withProperties(propertiesObject)
                .withTraits(Utils.getTraitsObject())
                .build();

        String cacheStr = new Gson().toJson(cacheDataObject);
        try {
            JSONObject jsonObject = new JSONObject(cacheStr);
            cacheValueMap = Utils.toMap(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                cacheValueMap = new ValueMap().put("anonymousId", Build.ID)
                        .put("context", new ValueMap().put("app", new ValueMap().put("build", appObject.getBuild())
                                .put("name", appObject.getName())
                                .put("namespace", appObject.getNamespace())
                                .put("version", appObject.getVersion()))
                                .put("device", new ValueMap().put("device_id", deviceObject.getDeviceId())
                                        .put("id", deviceObject.getId())
                                        .put("manufacturer", deviceObject.getManufacturer())
                                        .put("model", deviceObject.getModel())
                                        .put("name", deviceObject.getName())
                                        .put("type", deviceObject.getType()))
                                .put("os", new ValueMap().put("name", osObject.getName())
                                        .put("version", osObject.getVersion()))
                                .put("timezone", Utils.getTimeZone()))
                        .put("profile_info", new ValueMap().put("source", "APP")
                                .put("customer_id", Build.ID)
                                .put("device_id", Build.ID)
                                .put("push_id", new ValueMap().put("app_id", "ANDROID")
                                        .put("is_logged", true)
                                        .put("lang", "VI")
                                        .put("last_access", Utils.getTimeUTC())
                                        .put("os_type", 2)
                                        .put("push_id", SharedPreferencesUtils.getString(application.getApplicationContext(), SharedPreferencesUtils.KEY_DEVICE_TOKEN))))
                        .put("properties", new ValueMap().put("build", propertiesObject.getBuild())
                                .put("version", propertiesObject.getVersion()))
                        .put("event_data", new ValueMap().put("action_time", Utils.getTimeUTC()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        application.registerReceiver(networkChangeReceiver, intentFilter);
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_DEVICE_TOKEN, deviceToken);
        this.cacheDataObject.setProfileInfo(Utils.getProfileCreateDeviceObject(application));
        ValueMap pushIdVM = (ValueMap) ((ValueMap) cacheValueMap.get("profile_info")).get("push_id");
        if (pushIdVM != null) {
            pushIdVM.put("push_id", deviceToken);
            ((ValueMap) cacheValueMap.get("profile_info")).put("push_id", pushIdVM);
        }
    }

    public void setBothEventAndPushJson(String event, String push) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(event);
            ValueMap vm = Utils.toMap(jsonObject);
            if (vm.get("events") == null) {
                return;
            }

            List<ValueMap> events = (List<ValueMap>) vm.get("events");
            if (events != null && events.size() > 0) {
                currentJsonEvent = new ArrayList<ValueMap>(events);
            }

            jsonObject = new JSONObject(push);
            vm = Utils.toMap(jsonObject);
            if (vm.get("pushes") == null) {
                return;
            }

            List<ValueMap> pushes = (List<ValueMap>) vm.get("pushes");
            if (pushes != null && pushes.size() > 0) {
                currentJsonPush = new ArrayList<ValueMap>(pushes);
            }

            for (int i = 0; i < currentJsonEvent.size(); i++) {
                ValueMap tempEvent = currentJsonEvent.get(i);
                if (tempEvent != null) {
                    List<ValueMap> childrens = (List<ValueMap>) tempEvent.get("children_node");
                    if (childrens != null && childrens.size() > 0) {
                        for (int j = 0; j < childrens.size() - 1; j++) {
                            for (int k = 0; k < childrens.size() - j - 1; k++) {
                                if ((long) childrens.get(k).get("expire") > (long) childrens.get(k + 1).get("expire")) {
                                    ValueMap temp = childrens.get(k);
                                    childrens.set(k, childrens.get(k + 1));
                                    childrens.set(k + 1, temp);
                                }
                            }
                        }
                        tempEvent.put("children_node", childrens);
                        currentJsonEvent.set(i, tempEvent);
                    }
                }
            }

            String jsonEvent = new Gson().toJson(currentJsonEvent, new TypeToken<ArrayList<ValueMap>>() {
            }.getType());
            SharedPreferencesUtils.editString(application, SharedPreferencesUtils.KEY_EVENT, jsonEvent);

            String jsonPush = new Gson().toJson(currentJsonPush, new TypeToken<ArrayList<ValueMap>>() {
            }.getType());
            SharedPreferencesUtils.editString(application, SharedPreferencesUtils.KEY_PUSH, jsonPush);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showGlobalPopup(NotiResponseObject notiResponseObject) {
        if (mobioSDKLifecycleCallback != null) {
            mobioSDKLifecycleCallback.showPopup(notiResponseObject);
        }
    }

    private void showListAdsPopup(ArrayList<NotiResponseObject> notiResponseObjectArrayList) {
        if (mobioSDKLifecycleCallback != null) {
            mobioSDKLifecycleCallback.showPopup(notiResponseObjectArrayList);
        }
    }

    public void showGlobalNotification(NotiResponseObject notiResponseObject, int id){
        new GlobalNotification(id, notiResponseObject, configActivityMap, application).show();
    }

    public void track(String eventKey, ValueMap traits) {
        analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (traits != null && cacheValueMap != null) {
                    traits.put("action_time", Utils.getTimeUTC());
                    cacheValueMap.put("event_data", traits);
                    cacheValueMap.put("event_key", eventKey);
                    cacheValueMap.put("type", "track");
                    processSync(cacheValueMap);
                }
            }
        });

    }

    public String getVersionCode() {
        PackageInfo packageInfo = getPackageInfo(application);
        return String.valueOf(packageInfo.versionCode);
    }

    public String getVersionBuild() {
        PackageInfo packageInfo = getPackageInfo(application);
        return packageInfo.versionName;
    }

    public Map<String, Object> parameters(Object obj) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj));
            } catch (Exception e) {
            }
        }
        return map;
    }

    public ArrayList<ValueMap> getPendingJsonPush() {
        String strPendingPush = SharedPreferencesUtils.getString(application, SharedPreferencesUtils.KEY_PENDING_PUSH);
        JSONObject jsonObject = null;
        try {
            if(strPendingPush != null) {
                jsonObject = new JSONObject(strPendingPush);
                ValueMap vm = Utils.toMap(jsonObject);
                if (vm.get("key_pending_push") != null) {
                    return new ArrayList<>((List<ValueMap>) vm.get("key_pending_push"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public ArrayList<ValueMap> getEventQueue() {
        String strPendingPush = SharedPreferencesUtils.getString(application, SharedPreferencesUtils.KEY_EVENT_QUEUE);
        JSONObject jsonObject = null;
        try {
            if(strPendingPush != null) {
                jsonObject = new JSONObject(strPendingPush);
                ValueMap vm = Utils.toMap(jsonObject);
                if (vm.get("key_event_queue") != null) {
                    return new ArrayList<>((List<ValueMap>) vm.get("key_event_queue"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    private void updatePendingJsonPush(ArrayList<ValueMap> pendingJsonPush) {
        ValueMap vm = new ValueMap().put("key_pending_push", pendingJsonPush);
        String jsonEvent = new Gson().toJson(vm, new TypeToken<ValueMap>() {
        }.getType());
        SharedPreferencesUtils.editString(application, SharedPreferencesUtils.KEY_PENDING_PUSH, jsonEvent);
    }

    public void updateEventQueue(ArrayList<ValueMap> pendingJsonPush) {
        ValueMap vm = new ValueMap().put("key_event_queue", pendingJsonPush);
        String jsonEvent = new Gson().toJson(vm, new TypeToken<ValueMap>() {
        }.getType());
        SharedPreferencesUtils.editString(application, SharedPreferencesUtils.KEY_EVENT_QUEUE, jsonEvent);
    }

    public void processPendingJson() {
        analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                pendingJsonPush = getPendingJsonPush();
                if (pendingJsonPush.size() > 0) {
                    int countNoti = pendingJsonPush.size();
                    long maxInterval = 60 * 1000;
                    long minInterval = 2 * 1000;
                    long diff = maxInterval - minInterval;
                    long intervel = diff / countNoti + minInterval;
                    long now = System.currentTimeMillis();

                    for (int i = 0; i < countNoti; i++) {
                        ValueMap noti = (ValueMap) pendingJsonPush.get(i).get("noti_response");
                        String notiStr = new Gson().toJson(noti);
                        Intent intent = new Intent(application, AlarmReceiver.class);
                        intent.setAction("ACTION_LAUNCH_NOTI");
                        intent.putExtra("NOTI_OBJECT", notiStr);
                        intent.putExtra("NOTI_ID", i+((int) (Math.random() * 10000)));
                        intent.putExtra("NODE_ID", (String) ((ValueMap) pendingJsonPush.get(i)).get("node_id"));

                        PendingIntent alarmIntent = PendingIntent.getBroadcast(application, i, intent, PendingIntent.FLAG_IMMUTABLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            alarmManager.setExact(AlarmManager.RTC,  now + intervel * (i+1), alarmIntent);
                        }
                    }
                }
            }
        });
    }

    private void processEventBeforeSync(ValueMap dataObject) {
        pendingJsonPush = getPendingJsonPush();
        String eventKey = (String) dataObject.get("event_key");
        ValueMap eventData = (ValueMap) dataObject.get("event_data");
        String actionTime = null;
        if (eventData != null) {
            actionTime = (String) ((ValueMap) dataObject.get("event_data")).get("action_time");
        }

        if (eventKey == null || actionTime == null) {
            return;
        }

        if (currentJsonEvent == null || currentJsonEvent.size() == 0) {
            return;
        }

        boolean checkEvent = false; //check case nếu các jsonpush complete hết rồi thì show pendingpush
        boolean eventKeyEqual = false; //check case tất cả eventkey không thoả mãn thì show pendingpush

        for (int i = 0; i < currentJsonEvent.size(); i++) {
            ValueMap tempEvent = currentJsonEvent.get(i);
            String tpEventKey = (String) tempEvent.get("event_key");

            if (tpEventKey == null || !tpEventKey.equals(eventKey) || tpEventKey.equals("")) {
                continue;
            }
            ValueMap tpEventData = (ValueMap) tempEvent.get("event_data");
            if (tpEventData == null) {
                return;
            }
            String edStr = new Gson().toJson(tpEventData);
            String eventStr = new Gson().toJson(eventData);
            if (Utils.compareTwoJson(edStr, eventStr)) {
                eventKeyEqual = true;
                List<ValueMap> childrens = (List<ValueMap>) tempEvent.get("children_node");

                if (childrens == null || childrens.size() <= 0) {
                    return;
                }

                boolean runFirstPushDone = false;
                for (int j = 0; j < childrens.size(); j++) {
                    ValueMap tempChildren = childrens.get(j);
                    if (tempChildren == null) {
                        return;
                    }
                    boolean complete = (boolean) tempChildren.get("complete");

                    String type = (String) tempChildren.get("type");
                    if (type == null) return;
                    if (!complete) {
                        checkEvent = true;
                        String childrenId = (String) tempChildren.get("id");
                        for (int k = 0; k < currentJsonPush.size(); k++) {
                            ValueMap tempPush = currentJsonPush.get(k);
                            if (tempPush == null) {
                                return;
                            }
                            String pushId = (String) tempPush.get("node_id");
                            if (pushId == null) {
                                return;
                            }
                            if (pushId.equals(childrenId)) {
                                if (!runFirstPushDone) {
                                    ValueMap noti = (ValueMap) tempPush.get("noti_response");
                                    String notiStr = new Gson().toJson(noti);
                                    NotiResponseObject notiResponseObject = new Gson().fromJson(notiStr, NotiResponseObject.class);
                                    notiResponseObject.setPushId(pushId);
                                    showPushInApp(notiResponseObject);
                                } else {
                                    if (pendingJsonPush.size() == 0) {
                                        pendingJsonPush.add(tempPush);
                                    } else {
                                        if ((long) tempPush.get("expire") <= (long) pendingJsonPush.get(0).get("expire")) {
                                            pendingJsonPush.add(0, tempPush);
                                        } else if ((long) tempPush.get("expire") >= (long) pendingJsonPush.get(pendingJsonPush.size() - 1).get("expire")) {
                                            pendingJsonPush.add(tempPush);
                                        } else {
                                            for (int l = 0; l < pendingJsonPush.size(); l++) {
                                                if ((long) tempPush.get("expire") >= (long) pendingJsonPush.get(l).get("expire")
                                                        && (long) tempPush.get("expire") <= (long) pendingJsonPush.get(l + 1).get("expire")) {
                                                    pendingJsonPush.add(l + 1, tempPush);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    updatePendingJsonPush(pendingJsonPush);
                                }
                                tempChildren.put("complete", true);
                                childrens.set(j, tempChildren);
                                tempEvent.put("children_node", childrens);
                                currentJsonEvent.set(i, tempEvent);
                                runFirstPushDone = true;
                            }
                        }
                    }
                }
                break;
            }
        }

        if((!eventKeyEqual || !checkEvent) && pendingJsonPush.size() > 0){
            ValueMap tempPush = pendingJsonPush.get(0);
            ValueMap noti = (ValueMap) tempPush.get("noti_response");
            List<String> eventsCanShow = (List<String>) tempPush.get("events_to_show");
            if(eventsCanShow != null && eventsCanShow.contains(eventKey)) {
                String pushId = (String) tempPush.get("node_id");
                String notiStr = new Gson().toJson(noti);
                NotiResponseObject notiResponseObject = new Gson().fromJson(notiStr, NotiResponseObject.class);
                notiResponseObject.setPushId(pushId);
                showPushInApp(notiResponseObject);
            }
            pendingJsonPush.remove(0);
            updatePendingJsonPush(pendingJsonPush);
        }
    }

    private void showPushInApp(NotiResponseObject notiResponseObject){
        if (SharedPreferencesUtils.getBool(application, SharedPreferencesUtils.KEY_APP_FOREGROUD)) {
            showGlobalPopup(notiResponseObject);
        } else {
            int randomId = (int) (Math.random()*10000);
            showGlobalNotification(notiResponseObject, randomId);
        }
    }

    public boolean sendSync(ValueMap dataObject){
        String url = SharedPreferencesUtils.getString(application, SharedPreferencesUtils.KEY_BASE_URL);
        String endpoint = SharedPreferencesUtils.getString(application, SharedPreferencesUtils.KEY_ENDPOINT);
        try {
            ValueMap bodyMap = new ValueMap().put("data", dataObject);
            LogMobio.logD("Analytics", "send = " + new Gson().toJson(bodyMap));
            Response<Void> response = RetrofitClient.getInstance(url).getMyApi().sendSync(Utils.getHeader(application.getApplicationContext()), bodyMap, endpoint).execute();
            LogMobio.logD(TAG, "code = " + response.code());
            if (response.code() != 200) {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                LogMobio.logD(TAG, "body = " + jObjError.toString());
                return false;
            }
            else {
                return true;
            }
        } catch (IOException | JSONException e) {
            LogMobio.logD(TAG, "api dies ");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            LogMobio.logD(TAG, "api dies hard");
            e.printStackTrace();
            return false;
        }
    }

    private void processSync(ValueMap dataObject) {
//        if(isAppropriateTimeToShow()){
            processEventBeforeSync(dataObject); // 2 list json
//        }

        listDataWaitToSend = getEventQueue();

        if(Utils.isOnline(application)) {
            if(listDataWaitToSend != null && listDataWaitToSend.size() > 0){
                for (ValueMap vm: listDataWaitToSend) {
                    if(sendSync(vm)){
                        listDataWaitToSend.remove(vm);
                        updateEventQueue(listDataWaitToSend);
                    }
                }
            }
            if(!sendSync(dataObject)){
                addEventQueue(dataObject);
            }
        }
        else {
            addEventQueue(dataObject);
        }
    }

    private void addEventQueue(ValueMap vm){
        if(listDataWaitToSend == null){
            listDataWaitToSend = new ArrayList<>();
        }
        listDataWaitToSend.add(vm);
        updateEventQueue(listDataWaitToSend);
    }

    private boolean isAppropriateTimeToShow(){
        Calendar now = Calendar.getInstance();
//        int year = now.get(Calendar.YEAR);
//        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
//        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);

        LogMobio.logD("QuanLA", ""+hour+":"+minute+":"+second+":"+millis);

        return hour == 8 || hour == 13;
    }

    public void identify(ValueMap profile) {
        if (cacheValueMap != null) {
            Future<?> future = analyticsExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    ValueMap profileParam = (ValueMap) profile.clone();
                    profileParam.put("action_time", Utils.getTimeUTC());

                    cacheValueMap.put("type", "identify");
                    cacheValueMap.put("event_key", SDK_Mobile_Test_Identify_App);
                    ValueMap profileVM = (ValueMap) ((ValueMap) cacheValueMap.get("profile_info")).remove("source").clone();
                    cacheValueMap.put("profile_info", profile);


                    cacheValueMap.put("event_data", profileParam);
                    ((ValueMap) cacheValueMap.get("profile_info")).put("customer_id", Build.ID);
                    ((ValueMap) cacheValueMap.get("profile_info")).put("source", "APP");
                    ((ValueMap) cacheValueMap.get("profile_info")).put("device_id", Build.ID);
                    ((ValueMap) cacheValueMap.get("profile_info")).put("push_id", profileVM.get("push_id"));
                    //cacheValueMap.get("context").put("traits", profile.put("action_time", Utils.getTimeUTC()));
                    processSync(cacheValueMap);
                    //listDataWaitToSend.add(cacheDataObject);
                }
            });
        }
    }

    public void trackDeepLink(Activity activity) {
        Intent intent = activity.getIntent();
        if (intent == null || intent.getData() == null) {
            LogMobio.logD(TAG, "deeplink 1");
            return;
        }

        Uri referrer = Utils.getReferrer(activity);
        if (referrer != null) {
            //Todo save this link
            LogMobio.logD(TAG, referrer.toString());
        }

        Uri uri = intent.getData();
        LogMobio.logD(TAG, uri.toString());
        try {
            for (String parameter : uri.getQueryParameterNames()) {
                String value = uri.getQueryParameter(parameter);
                if (value != null && !value.trim().isEmpty()) {
                    //Todo save
                    LogMobio.logD(TAG, "parameter: " + parameter + " value: " + value);
                }
            }
        } catch (Exception e) {
            LogMobio.logE(TAG, e.toString());
        }

        //analytics.track("Deep Link Opened");
    }

    void recordScreen(ValueMap traits) {
        if (cacheValueMap != null) {
            Future<?> future = analyticsExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    traits.put("action_time", Utils.getTimeUTC());
                    cacheValueMap.put("event_data", traits);
                    cacheValueMap.put("event_key", SDK_Mobile_Test_Time_Visit_App);
                    cacheValueMap.put("type", "screen");
                    processSync(cacheValueMap);
                }
            });
        }
    }

    PackageInfo getPackageInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("Package not found: " + context.getPackageName());
        }
    }

    void trackApplicationLifecycleEvents() {
        // Get the current version.
        PackageInfo packageInfo = getPackageInfo(application);
        String currentVersionName = packageInfo.versionName;
        int currentVersionCode = packageInfo.versionCode;

        LogMobio.logD(TAG, "currentVersionName " + currentVersionName);
        LogMobio.logD(TAG, "currentVersionCode " + currentVersionCode);

        // Get the previous recorded version.
        String previousVersionName = SharedPreferencesUtils.getString(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_NAME);
        int previousVersionCode = SharedPreferencesUtils.getInt(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_CODE);

        // Check and track Application Updated.
        if (currentVersionCode != previousVersionCode) {
            SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_NAME, currentVersionName);
            SharedPreferencesUtils.editInt(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_CODE, currentVersionCode);
            //track(DEMO_EVENT, TYPE_APP_LIFECYCLE,"Application updated");
            track(MobioSDKClient.SDK_Mobile_Test_Open_Update_App, new ValueMap().put("build", currentVersionName)
                    .put("version", String.valueOf(currentVersionCode)));
        }
    }

    public static void setSingletonInstance(MobioSDKClient mobioSDKClient) {
        synchronized (MobioSDKClient.class) {
            if (singleton != null) {
                throw new IllegalStateException("Singleton instance already exists.");
            }
            singleton = mobioSDKClient;
        }
    }

    public static class Builder {
        private Application mApplication;
        private boolean mShouldTrackAppLifecycle = false;
        private boolean mShouldTrackScreenLifecycle = false;
        private boolean mShouldTrackDeepLink = false;
        private boolean mShouldRecordScreen = false;
        private boolean mShouldTrackScroll = false;
        private String mApiToken;
        private String mMerchantId;
        private int mIntervalSecond = 30;
        private String mDeviceToken;
        private String mDomainURL;
        private String mEndPoint;
        private HashMap<String, ScreenConfigObject> mActivityMap;

        public Builder() {
        }

        public Builder withApplication(Application application) {
            mApplication = application;
            return this;
        }

        public Builder shouldTrackScreenLifeCycle(boolean shouldTrackScreenLifeCycle) {
            mShouldTrackScreenLifecycle = shouldTrackScreenLifeCycle;
            return this;
        }

        public Builder shouldTrackAppLifeCycle(boolean shouldTrackLifecycle) {
            mShouldTrackAppLifecycle = shouldTrackLifecycle;
            return this;
        }

        public Builder shouldTrackDeepLink(boolean shouldTrackDeepLink) {
            mShouldTrackDeepLink = shouldTrackDeepLink;
            return this;
        }

        public Builder shouldRecordScreen(boolean shouldRecordScreen) {
            mShouldRecordScreen = shouldRecordScreen;
            return this;
        }

        public Builder withIntervalSecond(int second) {
            mIntervalSecond = second;
            return this;
        }

        public Builder shouldTrackScroll(boolean shouldTrackScroll) {
            mShouldTrackScroll = shouldTrackScroll;
            return this;
        }

        public Builder withMerchantId(String merchantId) {
            mMerchantId = merchantId;
            return this;
        }

        public Builder withApiToken(String apiToken) {
            mApiToken = apiToken;
            return this;
        }

        public Builder withDeviceToken(String deviceToken) {
            mDeviceToken = deviceToken;
            return this;
        }

        public Builder withDomainURL(String domainURL) {
            mDomainURL = domainURL;
            return this;
        }

        public Builder withEndPoint(String endPoint) {
            mEndPoint = endPoint;
            return this;
        }

        public Builder withActivityMap(HashMap<String, ScreenConfigObject> activityMap) {
            mActivityMap = activityMap;
            return this;
        }

        public MobioSDKClient build() {
            return new MobioSDKClient(this);
        }
    }
}
