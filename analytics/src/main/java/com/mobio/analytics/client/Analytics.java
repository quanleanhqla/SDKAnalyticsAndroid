package com.mobio.analytics.client;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mobio.analytics.client.models.AppObject;
import com.mobio.analytics.client.models.DataObject;
import com.mobio.analytics.client.models.DeviceObject;
import com.mobio.analytics.client.models.OsObject;
import com.mobio.analytics.client.models.PropertiesObject;
import com.mobio.analytics.client.models.TraitsObject;
import com.mobio.analytics.client.models.ValueMap;
import com.mobio.analytics.client.models.IdentifyObject;
import com.mobio.analytics.client.models.ProfileBaseObject;
import com.mobio.analytics.client.models.ProfileInfoObject;
import com.mobio.analytics.client.models.ScreenConfigObject;
import com.mobio.analytics.client.models.ScreenTraitsObject;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;
import com.mobio.analytics.network.RetrofitClient;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

        listDataWaitToSend = new ArrayList<>();

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
                                .put("name", "Android Mobio Bank")
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
        ValueMap pushIdVM = cacheValueMap.get("profile_info").get("push_id");
        if (pushIdVM != null) {
            pushIdVM.put("push_id", deviceToken);
            cacheValueMap.get("profile_info").put("push_id", pushIdVM);
        }
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

    public void showGlobalPopup(String title, String content, Context source, Class des, String nameButton){
        if(analyticsLifecycleCallback != null){
            analyticsLifecycleCallback.showPopup(title, content, source, des, nameButton);
        }
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

    public void sendSync(ValueMap dataObject) {
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
                    profileParam.put("name", "Android Mobio Bank");

                    cacheValueMap.put("type", "identify");
                    cacheValueMap.put("event_key", SDK_Mobile_Test_Identify_App);
                    ValueMap profileVM = (ValueMap) cacheValueMap.get("profile_info").remove("source").clone();
                    cacheValueMap.put("profile_info", profile);


                    cacheValueMap.put("event_data", profileParam);
                    cacheValueMap.get("profile_info").put("customer_id", Build.ID);
                    cacheValueMap.get("profile_info").put("source", "APP");
                    cacheValueMap.get("profile_info").put("device_id", Build.ID);
                    cacheValueMap.get("profile_info").put("push_id", profileVM.get("push_id"));
                    cacheValueMap.get("profile_info").put("name", "Android Mobio Bank");
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
