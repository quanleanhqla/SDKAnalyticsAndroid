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
import com.mobio.analytics.client.model.ModelFactory;
import com.mobio.analytics.client.model.digienty.DataIdentity;
import com.mobio.analytics.client.model.digienty.DataNotification;
import com.mobio.analytics.client.model.digienty.DataTrack;
import com.mobio.analytics.client.model.digienty.Device;
import com.mobio.analytics.client.model.digienty.Event;
import com.mobio.analytics.client.model.digienty.Identity;
import com.mobio.analytics.client.model.digienty.IdentityDetail;
import com.mobio.analytics.client.model.digienty.Notification;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.model.digienty.Track;
import com.mobio.analytics.client.model.old.ScreenConfigObject;
import com.mobio.analytics.client.model.reponse.SendEventResponse;
import com.mobio.analytics.client.receiver.AlarmReceiver;
import com.mobio.analytics.client.receiver.NetworkChangeReceiver;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;
import com.mobio.analytics.client.view.GlobalNotification;
import com.mobio.analytics.client.view.popup.PermissionDialog;
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
import java.util.Objects;
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
    private String apiToken;
    private String merchantId;
    private int intervalSecond;
    private String deviceToken;
    private ArrayList<Properties> listDataWaitToSend;
    private String domainURL;
    private String endPoint;
    private HashMap<String, ScreenConfigObject> configActivityMap;

    private DataTrack cacheValueTrack;
    private DataIdentity cacheValueIdentity;
    private DataNotification cacheValueNotification;

    private ArrayList<Properties> currentJsonEvent;
    private ArrayList<Properties> currentJsonPush;
    private ArrayList<Properties> pendingJsonPush;
    private ArrayList<Properties> currentJsonJourney;

    private AlarmManager alarmManager;

    private ExecutorService analyticsExecutor;
    private ScheduledExecutorService sendSyncScheduler;

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


        saveNetworkProperties(merchantId, apiToken, domainURL, endPoint);
        initExecutors();
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

        LogMobio.logD("QuanLA", "1");
        initIdentityCache();
        initNotificationCache();
        initTrackCache();
        initNetworkReceiver();
        LogMobio.logD("QuanLA", "2");
    }

    private void saveNetworkProperties(String merchantId, String apiToken, String domainURL, String endPoint) {
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_MERCHANT_ID, merchantId);
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_API_TOKEN, apiToken);
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_BASE_URL, domainURL);
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_ENDPOINT, endPoint);
    }

    private void initExecutors() {
        analyticsExecutor = Executors.newSingleThreadExecutor();
        sendSyncScheduler = Executors.newScheduledThreadPool(1);
    }

    private void initTrackCache() {
        cacheValueTrack = ModelFactory.getDataTrack(application);
    }

    private void initIdentityCache() {
        cacheValueIdentity = ModelFactory.getDataIdentity(application);
    }

    private void initNotificationCache() {
        cacheValueNotification = ModelFactory.getDataNotification(application);
    }

    private void initNetworkReceiver() {
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        application.registerReceiver(networkChangeReceiver, intentFilter);
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;

        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_DEVICE_TOKEN, deviceToken);

        updateNotificationToken(deviceToken);
    }

    private void updateNotificationToken(String token){
        LogMobio.logD("Send", "updateNotificationToken");
        if(cacheValueNotification == null){
            initNotificationCache();
        }

        cacheValueNotification.getNotification().getDetail().putToken(token);
        analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                processSend(cacheValueNotification);
            }
        });
    }

    public void updateNotificationPermission(String permission){
        LogMobio.logD("Send", "updateNotificationPermission");
        if(cacheValueNotification == null){
            initNotificationCache();
        }

        cacheValueNotification.getNotification().getDetail().putPermission(permission);
        analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                processSend(cacheValueNotification);
            }
        });
    }

    public String getCurrentNotiPermissionInValue(){
        if(cacheValueIdentity != null) {
            Identity currentIdentity = cacheValueIdentity.getValueMap("identity", Identity.class);

            if (currentIdentity == null) return null;
            IdentityDetail currentIdentityDetail = currentIdentity.getValueMap("identity_detail", IdentityDetail.class);

            if (currentIdentityDetail == null) return null;
            Notification currentNotification = currentIdentityDetail.getValueMap("notification", Notification.class);

            if (currentNotification == null) return null;

            return currentNotification.getString("permission");
        }

        return null;
    }

    public void trackNotificationOnOff(Activity activity){
        if(!Utils.areNotificationsEnabled(activity)){
            if(activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new PermissionDialog(activity).show();
                    }
                });
            }
        }
        else {
            if(cacheValueNotification == null){
                initNotificationCache();
            }
            else {
                String permission = cacheValueNotification.getNotification().getDetail().getPermission();

                LogMobio.logD("Send", " "+permission);

                if(permission == null) return;
                if(!permission.equals(Notification.KEY_GRANTED)){
                    updateNotificationPermission(Notification.KEY_GRANTED);
                }
            }
        }
    }

    public void setCurrentJsonJourney(String journeyJson) {
        Properties vm = Properties.convertJsonStringtoProperties(journeyJson);
        if (vm.get("journeys") == null) {
            return;
        }

        List<Properties> journeys = vm.getList("journeys", Properties.class);
        if (journeys != null && journeys.size() > 0) {
            currentJsonJourney = new ArrayList<Properties>(journeys);
        }

        String jsonJourney = new Gson().toJson(currentJsonJourney, new TypeToken<ArrayList<Properties>>() {
        }.getType());
        SharedPreferencesUtils.editString(application, SharedPreferencesUtils.KEY_JOURNEY, jsonJourney);
    }

    public void setBothEventAndPushJson(String event, String push) {
        Properties eventP = Properties.convertJsonStringtoProperties(event);
        if (eventP.get("events") == null) {
            return;
        }
        List<Properties> events = eventP.getList("events", Properties.class);
        if (events != null && events.size() > 0) {
            currentJsonEvent = new ArrayList<Properties>(events);
        }

        Properties pushP = Properties.convertJsonStringtoProperties(push);
        if (pushP.get("pushes") == null) {
            return;
        }
        List<Properties> pushes = pushP.getList("pushes", Properties.class);
        if (pushP != null && pushP.size() > 0) {
            currentJsonPush = new ArrayList<Properties>(pushes);
        }

        for (int i = 0; i < currentJsonEvent.size(); i++) {
            Properties tempEvent = currentJsonEvent.get(i);
            if (tempEvent != null) {
                List<Properties> childrens = tempEvent.getList("children_node", Properties.class);
                if (childrens != null && childrens.size() > 0) {
                    for (int j = 0; j < childrens.size() - 1; j++) {
                        for (int k = 0; k < childrens.size() - j - 1; k++) {
                            if (childrens.get(k).getLong("expire", 0) > childrens.get(k + 1).getLong("expire", 0)) {
                                Properties temp = childrens.get(k);
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

        String jsonEvent = new Gson().toJson(currentJsonEvent, new TypeToken<ArrayList<Properties>>() {
        }.getType());
        SharedPreferencesUtils.editString(application, SharedPreferencesUtils.KEY_EVENT, jsonEvent);

        String jsonPush = new Gson().toJson(currentJsonPush, new TypeToken<ArrayList<Properties>>() {
        }.getType());
        SharedPreferencesUtils.editString(application, SharedPreferencesUtils.KEY_PUSH, jsonPush);
    }

    public void showGlobalPopup(Push push) {
        if (mobioSDKLifecycleCallback != null) {
            mobioSDKLifecycleCallback.showPopup(push);
        }
    }

    public void showGlobalNotification(Push push, int id) {
        new GlobalNotification(id, push, configActivityMap, application).show();
    }

    public void track(String eventKey, Properties eventData) {
        LogMobio.logD("QuanLA", "Track 1");
        if (cacheValueTrack == null) {
            initTrackCache();
            LogMobio.logD("QuanLA", "Track 3 "+cacheValueTrack.toString());
        }
        analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (eventData != null) {
                    LogMobio.logD("QuanLA", "Track 4");
                    processTrack(eventKey, eventData);
                }
                LogMobio.logD("QuanLA", "Track 5");
            }
        });
        LogMobio.logD("QuanLA", "Track 2");

    }

    private void processTrack(String eventKey, Properties eventData) {
        LogMobio.logD("QuanLA", "Track 8");
        eventData.put("action_time", System.currentTimeMillis());
        cacheValueTrack.getValueMap("track", Track.class)
                .putEvents(Utils.createListEvent(Utils.createDynamicListEvent(eventKey, eventData)))
                .putActionTime(System.currentTimeMillis());

        LogMobio.logD("QuanLA", "Track 9 "+cacheValueTrack.toString());

        if (!checkEventExistInJourneyWeb(eventKey, eventData)) {
            processCommonPushBeforeSync(eventKey, eventData);
        }

        processSend(cacheValueTrack);

        LogMobio.logD("QuanLA", "Track 6");
    }

    private void updateAllCacheValue(SendEventResponse sendEventResponse){

        String d_id = sendEventResponse.getData().getdId();
        if(d_id != null) {
            if(SharedPreferencesUtils.getString(application, SharedPreferencesUtils.KEY_D_ID) == null) {
                SharedPreferencesUtils.editString(application, SharedPreferencesUtils.KEY_D_ID, d_id);

                Track track = cacheValueTrack.getTrack();
                Device device = track.getDevice();
                device.putDId(d_id);

                Identity identity = cacheValueIdentity.getIdentity();
                IdentityDetail identityDetail = identity.getDetail();
                identityDetail.putDId(d_id);

                Notification notification = cacheValueNotification.getNotification();
                IdentityDetail deviceNoti = notification.getDevice();
                deviceNoti.putDId(d_id);
            }
        }
    }

    public boolean sendv2(Properties value) {
        try {
            Response<SendEventResponse> response = null;
            LogMobio.logD("Send event v2", "send = " + new Gson().toJson(value));
            String typeOfValue = Utils.getTypeOfData(value);

            if (typeOfValue == null) return false;
            switch (typeOfValue) {
                case "track":
                    response = RetrofitClient.getInstance().getMyApi().sendEvent(value).execute();
                    break;
                case "identity":
                    response = RetrofitClient.getInstance().getMyApi().sendDevice(value).execute();
                    break;
                case "notification":
                    response = RetrofitClient.getInstance().getMyApi().sendNotification(value).execute();
                    break;
            }

            if (response == null) return false;

            LogMobio.logD("Send event v2", "code = " + response.code());

            if (response.code() != 200) {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                LogMobio.logD("Send event v2", "response error body = " + jObjError.toString());
                return false;
            } else {
                SendEventResponse sendEventResponse = response.body();
                if(sendEventResponse != null){
                    LogMobio.logD("Send event v2", "response body = " + sendEventResponse.toString());
                    updateAllCacheValue(sendEventResponse);
                }
                return true;
            }
        } catch (IOException | JSONException e) {
            LogMobio.logD("Send event v2", "api dies ");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            LogMobio.logD("Send event v2", "api dies hard");
            e.printStackTrace();
            return false;
        }
    }

    public void track(List<Event> eventList) {
        analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (cacheValueTrack == null) {
                    initTrackCache();
                }

                if (eventList == null || eventList.size() == 0) return;

                cacheValueTrack.getValueMap("track", Track.class).put("events", eventList);
                processSend(cacheValueTrack);
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
                e.printStackTrace();
            }
        }
        return map;
    }

    public ArrayList<Properties> getListFromSharePref(String key) {
        String strJson = SharedPreferencesUtils.getString(application, key);
        Properties vm = Properties.convertJsonStringtoProperties(strJson);
        if (vm == null) return new ArrayList<>();
        if (vm.get(key) != null) {
            return new ArrayList<>(vm.getList(key, Properties.class));
        }
        return new ArrayList<>();
    }

    public void updateListSharePref(ArrayList<Properties> list, String key) {
        Properties vm = new Properties().putValue(key, list);
        String jsonEvent = new Gson().toJson(vm, new TypeToken<Properties>() {
        }.getType());
        SharedPreferencesUtils.editString(application, key, jsonEvent);
    }

    public void processPendingJson() {
        analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                pendingJsonPush = getListFromSharePref(SharedPreferencesUtils.KEY_PENDING_PUSH);
                if (pendingJsonPush.size() > 0) {
                    int countNoti = pendingJsonPush.size();
                    long maxInterval = 60 * 1000;
                    long minInterval = 2 * 1000;
                    long intervel = Utils.getTimeInterval(maxInterval, minInterval, countNoti);
                    long now = System.currentTimeMillis();

                    for (int i = 0; i < countNoti; i++) {
                        Intent intent = new Intent(application, AlarmReceiver.class);
                        intent.setAction("ACTION_LAUNCH_NOTI");

                        PendingIntent alarmIntent = PendingIntent.getBroadcast(application, i, intent, PendingIntent.FLAG_IMMUTABLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            alarmManager.setExact(AlarmManager.RTC, now + intervel * (i + 1), alarmIntent);
                        }
                    }
                }
            }
        });
    }

    private void processCommonPushBeforeSync(String eventKey, Properties eventData) {
        pendingJsonPush = getListFromSharePref(SharedPreferencesUtils.KEY_PENDING_PUSH);

        if (eventKey == null) {
            return;
        }

        if (currentJsonEvent == null || currentJsonEvent.size() == 0) {
            return;
        }

        boolean checkEvent = false; //check case nếu các jsonpush complete hết rồi thì show pendingpush
        boolean eventKeyEqual = false; //check case tất cả eventkey không thoả mãn thì show pendingpush

        for (int i = 0; i < currentJsonEvent.size(); i++) {
            Properties tempEvent = currentJsonEvent.get(i);
            String tpEventKey = tempEvent.getString("event_key");

            if (tpEventKey == null || !tpEventKey.equals(eventKey) || tpEventKey.equals("")) {
                continue;
            }
            Properties tpEventData = tempEvent.getValueMap("event_data", Properties.class);
            if (tpEventData == null) {
                return;
            }
            String edStr = new Gson().toJson(tpEventData);
            String eventStr = new Gson().toJson(eventData);
            if (Utils.compareTwoJson(edStr, eventStr)) {
                eventKeyEqual = true;
                List<Properties> childrens = tempEvent.getList("children_node", Properties.class);

                if (childrens == null || childrens.size() <= 0) {
                    return;
                }

                boolean runFirstPushDone = false;
                for (int j = 0; j < childrens.size(); j++) {
                    Properties tempChildren = childrens.get(j);
                    if (tempChildren == null) {
                        return;
                    }
                    boolean complete = tempChildren.getBoolean("complete", false);

                    String type = tempChildren.getString("type");
                    if (type == null) return;
                    if (!complete) {
                        checkEvent = true;
                        String childrenId = tempChildren.getString("id");
                        for (int k = 0; k < currentJsonPush.size(); k++) {
                            Properties tempPush = currentJsonPush.get(k);
                            if (tempPush == null) {
                                return;
                            }
                            String pushId = tempPush.getString("node_id");
                            if (pushId == null) {
                                return;
                            }
                            if (pushId.equals(childrenId)) {
                                if (!runFirstPushDone) {
                                    Properties noti = tempPush.getValueMap("noti_response", Properties.class);
                                    showPushInApp(noti);
                                } else {
                                    if (pendingJsonPush.size() == 0) {
                                        pendingJsonPush.add(tempPush);
                                    } else {
                                        if (tempPush.getLong("expire", 0) <= pendingJsonPush.get(0).getLong("expire", 0)) {
                                            pendingJsonPush.add(0, tempPush);
                                        } else if (tempPush.getLong("expire", 0)
                                                >= pendingJsonPush.get(pendingJsonPush.size() - 1).getLong("expire", 0)) {
                                            pendingJsonPush.add(tempPush);
                                        } else {
                                            for (int l = 0; l < pendingJsonPush.size(); l++) {
                                                if (tempPush.getLong("expire", 0) >= pendingJsonPush.get(l).getLong("expire", 0)
                                                        && tempPush.getLong("expire", 0) <= pendingJsonPush.get(l + 1).getLong("expire", 0)) {
                                                    pendingJsonPush.add(l + 1, tempPush);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    updateListSharePref(pendingJsonPush, SharedPreferencesUtils.KEY_PENDING_PUSH);
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

        if ((!eventKeyEqual || !checkEvent) && pendingJsonPush.size() > 0) {
            Properties tempPush = pendingJsonPush.get(0);
            Properties noti = tempPush.getValueMap("noti_response", Properties.class);
            List<String> eventsCanShow = (List<String>) tempPush.get("events_to_show");
            if (eventsCanShow != null && eventsCanShow.contains(eventKey)) {
                showPushInApp(noti);
                pendingJsonPush.remove(0);
            }
            updateListSharePref(pendingJsonPush, SharedPreferencesUtils.KEY_PENDING_PUSH);

        }
    }

    public void showPushInApp(Properties noti) {
        String contentType;

        if(noti == null) return;

        Push.Alert alert = new Push.Alert()
                .putBody(noti.getString("content"))
                .putTitle(noti.getString("title"));
        Push push = new Push().putDestinationScreen(noti.getString("des_screen")).putFromScreen(noti.getString("source_screen"));

        int type = noti.getInt("type", 0);
        if(type == 0){
            contentType = Push.Alert.TYPE_TEXT;
        }
        else if(type == 1){
            contentType = Push.Alert.TYPE_HTML;
            alert.putBodyHTML(noti.getString("data"));
        }
        else{
            contentType = Push.Alert.TYPE_POPUP;
            alert.putPopupUrl(noti.getString("data"));
        }

        alert.putContentType(contentType);
        push.putAlert(alert);

        showPushInApp(push);
    }

    private void showPushInApp(Push push) {
        if (SharedPreferencesUtils.getBool(application, SharedPreferencesUtils.KEY_APP_FOREGROUD)) {
            showGlobalPopup(push);
        } else {
            int randomId = (int) (Math.random() * 10000);
            showGlobalNotification(push, randomId);
        }
    }

    private boolean checkEventExistInJourneyWeb(String eventKey, Properties eventData) {

        if (currentJsonJourney.size() == 0) {
            return false;
        }

        for (Properties journey : currentJsonJourney) {
            String statusJb = (String) journey.get("status");
            if (statusJb == null) continue;
            if (statusJb.equals("todo")) {
                List<Properties> listEvent = (List<Properties>) journey.get("events");
                if (listEvent == null || listEvent.size() == 0) {
                    continue;
                }
                for (Properties event : listEvent) {
                    String mkey = event.getString("event_key");
                    Properties mData = (Properties) event.get("event_data");
                    String statusEvent = event.getString("status");

                    if (mkey == null || mData == null || statusEvent == null) continue;
                    String edStr = new Gson().toJson(mData);
                    String eventStr = new Gson().toJson(eventData);
                    if (mkey.equals(eventKey)
                            && Utils.compareTwoJson(edStr, eventStr)
                            && statusEvent.equals("pending")) {
                        event.put("status", "done");
                        if (listEvent.indexOf(event) + 1 < listEvent.size()) {
                            Properties eventNext = listEvent.get(listEvent.indexOf(event) + 1);
                            eventNext.put("status", "pending");
                            listEvent.set(listEvent.indexOf(event) + 1, eventNext);
                        }
                        listEvent.set(listEvent.indexOf(event), event);
                        journey.put("events", listEvent);
                        currentJsonJourney.set(currentJsonJourney.indexOf(journey), journey);
                        updateListSharePref(currentJsonJourney, SharedPreferencesUtils.KEY_JOURNEY);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void processSend(Properties data) {
        LogMobio.logD("QuanLA", "Track 7");
        listDataWaitToSend = getListFromSharePref(SharedPreferencesUtils.KEY_SEND_QUEUE);
        sendv2(data);

//        if (Utils.isOnline(application)) {
//            if (listDataWaitToSend != null && listDataWaitToSend.size() > 0) {
//                for (Properties vm : listDataWaitToSend) {
//                    if (sendv2(vm)) {
//                        listDataWaitToSend.remove(vm);
//                        updateListSharePref(listDataWaitToSend, SharedPreferencesUtils.KEY_SEND_QUEUE);
//                    }
//                }
//            }
//            if (!sendv2(data)) {
//                addSendQueue(data);
//            }
//        } else {
//            addSendQueue(data);
//        }
    }

    private void addSendQueue(Properties vm) {
        if (listDataWaitToSend == null) {
            listDataWaitToSend = new ArrayList<>();
        }
        listDataWaitToSend.add(vm);
        updateListSharePref(listDataWaitToSend, SharedPreferencesUtils.KEY_SEND_QUEUE);
    }

    private boolean isAppropriateTimeToShow() {
        Calendar now = Calendar.getInstance();
//        int year = now.get(Calendar.YEAR);
//        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
//        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        int millis = now.get(Calendar.MILLISECOND);

        LogMobio.logD("QuanLA", "" + hour + ":" + minute + ":" + second + ":" + millis);

        return hour == 8 || hour == 13;
    }

    public void identify() {
        LogMobio.logD("QuanLA", "identify()");
        Future<?> future = analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                processIdentity();
            }
        });
    }

    private void processIdentity() {
        if (cacheValueIdentity == null) {
            initIdentityCache();
        }

        processSend(cacheValueIdentity);
    }

    public void trackDeepLink(Activity activity) {
        Intent intent = activity.getIntent();
        if (intent == null || intent.getData() == null) {
            LogMobio.logD(TAG, "deeplink null");
            return;
        }

        Uri referrer = Utils.getReferrer(activity);
        if (referrer != null) {
            //Todo save this link
            LogMobio.logD(TAG, "deeplink " + referrer.toString());
            LogMobio.logD(TAG, "deeplink " + referrer.getAuthority());
        }

        Uri uri = intent.getData();
        LogMobio.logD(TAG, "deeplink " + uri.toString());
        try {
            for (String parameter : uri.getQueryParameterNames()) {
                String value = uri.getQueryParameter(parameter);
                if (value != null && !value.trim().isEmpty()) {
                    //Todo save
                    LogMobio.logD(TAG, "deeplink parameter: " + parameter + " value: " + value);
                }
            }
        } catch (Exception e) {
            LogMobio.logE(TAG, e.toString());
        }
    }

    void recordScreen(Properties eventData) {
        if (cacheValueTrack == null) {
            initTrackCache();
        }
        Future<?> future = analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                processTrack(SDK_Mobile_Test_Time_Visit_App, eventData);
            }
        });
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
            track(MobioSDKClient.SDK_Mobile_Test_Open_Update_App, new Properties().putValue("build", currentVersionName)
                    .putValue("version", String.valueOf(currentVersionCode)));
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
