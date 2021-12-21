package com.mobio.analytics.client;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import com.google.gson.Gson;
import com.mobio.analytics.R;
import com.mobio.analytics.client.models.AppObject;
import com.mobio.analytics.client.models.DataItem;
import com.mobio.analytics.client.models.DataObject;
import com.mobio.analytics.client.models.DeviceObject;
import com.mobio.analytics.client.models.EventData;
import com.mobio.analytics.client.models.JourneyObject;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.models.OsObject;
import com.mobio.analytics.client.models.PropertiesObject;
import com.mobio.analytics.client.models.ValueMap;
import com.mobio.analytics.client.models.ScreenConfigObject;
import com.mobio.analytics.client.receiver.NotificationDismissedReceiver;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;
import com.mobio.analytics.network.RetrofitClient;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;


import retrofit2.Response;

public class Analytics {
    private static final String TAG = Analytics.class.getName();
    public static final String DEMO_EVENT = "android_event";
    public static final String SDK_Mobile_Test_Click_Button_In_App = "sdk_mobile_test_click_button_in_app";
    public static final String SDK_Mobile_Test_Identify_App = "sdk_mobile_test_identify_app";
    public static final String SDK_Mobile_Test_Time_Visit_App = "sdk_mobile_test_time_visit_app";
    public static final String SDK_Mobile_Test_Screen_End_In_App = "sdk_mobile_test_screen_end_in_app";
    public static final String SDK_Mobile_Test_Screen_Start_In_App = "sdk_mobile_test_screen_start_in_app";
    public static final String SDK_Mobile_Test_Open_App = "sdk_mobile_test_open_app";
    public static final String SDK_Mobile_Test_Open_Update_App = "sdk_mobile_test_open_update_app";
    public static final String SDK_Mobile_Test_Open_First_App = "sdk_mobile_test_open_first_app";

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
    static volatile Analytics singleton = null;
    private Application application;
    private AnalyticsLifecycleCallback analyticsLifecycleCallback;
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
    private ArrayList<DataObject> listDataWaitToSend;
    private String domainURL;
    private String endPoint;
    private HashMap<String, ScreenConfigObject> configActivityMap;
    private ValueMap cacheValueMap;
    private NotificationManager notificationManager;
    private ArrayList<JourneyObject> currentJbList;
    private DataItem pendingNode;
    private JourneyObject currentJb;

    private final ExecutorService analyticsExecutor;
    private final ScheduledExecutorService sendSyncScheduler;

    public static Analytics getInstance() {
        synchronized (Analytics.class) {
            if (singleton == null) {
                singleton = new Builder().build();
            }
        }
        return singleton;
    }

    public Analytics(Builder builder) {
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
        notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);

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

        listDataWaitToSend = new ArrayList<DataObject>();

        analyticsLifecycleCallback = new AnalyticsLifecycleCallback(this, shouldTrackAppLifecycle, shouldTrackScreenLifecycle,
                shouldTrackDeepLink, shouldRecordScreen, shouldTrackScroll, application, configActivityMap);

        application.registerActivityLifecycleCallbacks(analyticsLifecycleCallback);

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
            cacheValueMap = toMap(jsonObject);
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
            }
            catch (Exception exception){
                exception.printStackTrace();
            }
        }

    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_DEVICE_TOKEN, deviceToken);
        this.cacheDataObject.setProfileInfo(Utils.getProfileCreateDeviceObject(application));
        ValueMap pushIdVM =(ValueMap) ((ValueMap) cacheValueMap.get("profile_info")).get("push_id");
        if (pushIdVM != null) {
            pushIdVM.put("push_id", deviceToken);
            ((ValueMap) cacheValueMap.get("profile_info")).put("push_id", pushIdVM);
        }
    }

    public void setJourneyList(ArrayList<JourneyObject> journeyList){
        currentJbList = journeyList;
    }

    public void addJourney(JourneyObject journeyObject){
        if(currentJbList == null){
            currentJbList = new ArrayList<>();
        }
        currentJbList.add(journeyObject);
    }

    private ValueMap toMap(JSONObject object) throws JSONException {
        ValueMap map = new ValueMap();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public void showGlobalPopup(NotiResponseObject notiResponseObject){
        if(analyticsLifecycleCallback != null){
            analyticsLifecycleCallback.showPopup(notiResponseObject);
        }
    }

    public void showGlobalNotification(NotiResponseObject notiResponseObject){
        Class classDes = null;
        Class classInitial = null;
        Intent intent = null;
        for (int i = 0; i < configActivityMap.values().size(); i++) {
            ScreenConfigObject screenConfigObject = (ScreenConfigObject) configActivityMap.values().toArray()[i];
            if (screenConfigObject.getTitle().equals(notiResponseObject.getDes_screen())) {
                classDes = screenConfigObject.getClassName();
                break;
            }
            if(screenConfigObject.isInitialScreen()){
                classInitial = screenConfigObject.getClassName();
            }
        }
        if(classDes == null) {
            classDes = classInitial;
        }
        intent = new Intent(application.getApplicationContext(), classDes);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("OPEN_FROM_NOTI", 1);

        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(application.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon)
                .setLargeIcon(BitmapFactory.decodeResource(application.getResources(),
                        R.mipmap.icon))
                .setContentTitle(notiResponseObject.getTitle())
                .setContentText(notiResponseObject.getContent())
                .setDeleteIntent(createOnDismissedIntent(application.getApplicationContext(), REQUEST_CODE, false))
                .setContentIntent(classDes != null ? PendingIntent.getActivity(application.getApplicationContext(), REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE) : null)
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(REQUEST_CODE, notification); // 0 is the request code, it should be unique id
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId, boolean isDelete) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("type_delete", isDelete);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, PendingIntent.FLAG_IMMUTABLE);
        return pendingIntent;
    }

    private List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public void track(String eventKey, ValueMap traits) {
        analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if(traits != null && cacheValueMap != null) {
                    traits.put("action_time", Utils.getTimeUTC());
                    cacheValueMap.put("event_data", traits);
                    cacheValueMap.put("event_key", eventKey);
                    cacheValueMap.put("type", "track");
                    sendSync(cacheValueMap);
                }
            }
        });

    }

    public String getVersionCode(){
        PackageInfo packageInfo = getPackageInfo(application);
        return String.valueOf(packageInfo.versionCode);
    }

    public String getVersionBuild(){
        PackageInfo packageInfo = getPackageInfo(application);
        return packageInfo.versionName;
    }

    public void autoSendSync() {
        LogMobio.logD("Analytics", "Scheduler");
        if (listDataWaitToSend.size() > 0) {
            ArrayList<DataObject> listDataToSend = (ArrayList<DataObject>) listDataWaitToSend.clone();
            for (DataObject dataObject : listDataToSend) {
                //sendSync(dataObject);
            }
        }
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

    private JourneyObject getRunningJB(){
        JourneyObject properlyJB = null;
        for(int i = 0; i< currentJbList.size(); i++){
            if(currentJbList.get(i).getTypeTodo() == JourneyObject.TYPE_TODO_RUNNING){
                properlyJB = currentJbList.get(i);
                break;
            }
        }
        return properlyJB;
    }

    private JourneyObject startJbIfPossible(String eventKey, ValueMap eventData, String actionTime){
        JourneyObject properlyJB = null;
        for(int i = 0; i< currentJbList.size(); i++){
            JourneyObject tempJB = currentJbList.get(i);
            if(tempJB.getTypeTodo() == JourneyObject.TYPE_TODO_ACTIVE){
                DataItem rootData = tempJB.getData().get(0);
                if(rootData.getNodeCode().equals(DataItem.NODE_CODE_EVENT)){
                    if(rootData.getEventKey().equals(eventKey)){
                        LogMobio.logD("TestJb", "eventKey equal");
                        EventData ed = rootData.getEventData();
                        ed.setActionTime(actionTime);
                        String cacheStr = new Gson().toJson(ed);
                        try {
                            JSONObject jsonObject = new JSONObject(cacheStr);
                            ValueMap cacheED = toMap(jsonObject);
                            String edStr = new Gson().toJson(cacheED);
                            LogMobio.logD("TestJb", "edStr "+edStr);
                            String eventStr = new Gson().toJson(eventData);
                            LogMobio.logD("TestJb", "eventStr "+eventStr);
                            if(compareTwoJson(edStr, eventStr)){
                                LogMobio.logD("TestJb", "eventData equal");
                                properlyJB = currentJbList.get(i);
                                currentJbList.get(i).setTypeTodo(JourneyObject.TYPE_TODO_RUNNING);
                                pendingNode = rootData.getData().get(0);
                                if(pendingNode != null && pendingNode.getNodeCode().equals(DataItem.NODE_CODE_PUSH_IN_APP)){
                                    NotiResponseObject notiResponseObject = pendingNode.getNotiResponse();
                                    if(SharedPreferencesUtils.getBool(application, SharedPreferencesUtils.KEY_APP_FOREGROUD)){
                                        showGlobalPopup(notiResponseObject);
                                    }
                                    else {
                                        showGlobalNotification(notiResponseObject);
                                    }
                                    pendingNode = pendingNode.getData().get(0);
                                }
                                break;
                            }
                        }
                        catch (Exception e){
                            LogMobio.logD(TAG, "exception "+e);
                        }
                    }
                }
            }
        }
        return properlyJB;
    }

    private boolean compareTwoJson(String first, String second){
        try {
            JSONObject jsonObject = new JSONObject(first);
            JSONObject jsonObject1 = new JSONObject(second);
            Iterator<String> s = jsonObject.keys();
            for (Iterator<String> it = s; it.hasNext(); ) {
                String str = it.next();
                System.out.println("key:" + str + " : value1:" + jsonObject.get(str) + ":value2:" + jsonObject1.get(str));
                //compare value of json1 with json2
                if(!jsonObject.get(str).equals(jsonObject1.get(str))){
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void processLocalJb(String eventKey, ValueMap eventData, String actionTime) {
        if(getRunningJB() != null){
            if(pendingNode != null) {
                if (pendingNode.getNodeCode().equals(DataItem.NODE_CODE_CONDITION)) {
                    if(pendingNode.getEventKey().equals(eventKey)){
                        LogMobio.logD("TestJb", "eventKey equal 1");
                        EventData ed = pendingNode.getEventData();
                        ed.setActionTime(actionTime);
                        String cacheStr = new Gson().toJson(ed);
                        try {
                            JSONObject jsonObject = new JSONObject(cacheStr);
                            ValueMap cacheED = toMap(jsonObject);
                            String edStr = new Gson().toJson(cacheED);
                            LogMobio.logD("TestJb", "edStr1 "+edStr);
                            String eventStr = new Gson().toJson(eventData);
                            LogMobio.logD("TestJb", "eventStr1 "+eventStr);
                            if(compareTwoJson(edStr, eventStr)){
                                LogMobio.logD("TestJb", "eventData1 equal");
                                pendingNode = pendingNode.getData().get(0);
                                if(pendingNode != null && pendingNode.getNodeCode().equals(DataItem.NODE_CODE_PUSH_IN_APP)){
                                    NotiResponseObject notiResponseObject = pendingNode.getNotiResponse();
                                    if(SharedPreferencesUtils.getBool(application, SharedPreferencesUtils.KEY_APP_FOREGROUD)){
                                        showGlobalPopup(notiResponseObject);
                                    }
                                    else {
                                        showGlobalNotification(notiResponseObject);
                                    }
                                    pendingNode = pendingNode.getData().get(0);
                                }
                            }
                        }
                        catch (Exception e){
                            LogMobio.logD(TAG, "exception "+e);
                        }
                    }
                }
            }
            else {
                currentJb.setTypeTodo(JourneyObject.TYPE_TODO_DISACTIVE);
                currentJb = null;
            }
        }
        else {
            currentJb = startJbIfPossible(eventKey, eventData, actionTime);
        }
    }



    private void sendSync(ValueMap dataObject) {
        processLocalJb((String) dataObject.get("event_key"),(ValueMap) dataObject.get("event_data"), (String) ((ValueMap) dataObject.get("event_data")).get("action_time"));

        String url = SharedPreferencesUtils.getString(application, SharedPreferencesUtils.KEY_BASE_URL);
        String endpoint = SharedPreferencesUtils.getString(application, SharedPreferencesUtils.KEY_ENDPOINT);
        try {
            ValueMap bodyMap = new ValueMap().put("data", dataObject);
            LogMobio.logD("Analytics", "send = " + new Gson().toJson(bodyMap));
            Response<Void> response = RetrofitClient.getInstance(url).getMyApi().sendSync(Utils.getHeader(application.getApplicationContext()), bodyMap, endpoint).execute();
            if (response.code() != 200) {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                LogMobio.logD(TAG, "body = " + jObjError.toString());
            } else {
                listDataWaitToSend.remove(dataObject);
            }
            LogMobio.logD(TAG, "code = " + response.code());
        } catch (IOException | JSONException e) {
            LogMobio.logD(TAG, "api dies ");
            e.printStackTrace();
        } catch (Exception e) {
            LogMobio.logD(TAG, "api dies hard");
            e.printStackTrace();
        }
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
                    sendSync(cacheValueMap);
                    //listDataWaitToSend.add(cacheDataObject);
                }
            });
        }
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
                    sendSync(cacheValueMap);
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
            track(Analytics.SDK_Mobile_Test_Open_Update_App, new ValueMap().put("build", currentVersionName)
                    .put("version", String.valueOf(currentVersionCode)));
        }
    }

    public static void setSingletonInstance(Analytics analytics) {
        synchronized (Analytics.class) {
            if (singleton != null) {
                throw new IllegalStateException("Singleton instance already exists.");
            }
            singleton = analytics;
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

        public Builder withEndPoint(String endPoint){
            mEndPoint = endPoint;
            return this;
        }

        public Builder withActivityMap(HashMap<String, ScreenConfigObject> activityMap) {
            mActivityMap = activityMap;
            return this;
        }

        public Analytics build() {
            return new Analytics(this);
        }
    }
}
