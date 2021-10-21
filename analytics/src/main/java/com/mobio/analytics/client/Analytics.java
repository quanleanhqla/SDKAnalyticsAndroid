package com.mobio.analytics.client;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.mobio.analytics.client.models.ActivityTraitsObject;
import com.mobio.analytics.client.models.AppStateTraitsObject;
import com.mobio.analytics.client.models.ClickTraitsObject;
import com.mobio.analytics.client.models.DataObject;
import com.mobio.analytics.client.models.EventTraitsObject;
import com.mobio.analytics.client.models.IdentifyObject;
import com.mobio.analytics.client.models.ProfileBaseObject;
import com.mobio.analytics.client.models.ProfileInfoObject;
import com.mobio.analytics.client.models.PushObject;
import com.mobio.analytics.client.models.ScreenRecordTraitsObject;
import com.mobio.analytics.client.models.SendSyncObject;
import com.mobio.analytics.client.models.TrackObject;
import com.mobio.analytics.client.models.TraitsObject;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;
import com.mobio.analytics.network.RetrofitClient;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;


import retrofit2.Response;

public class Analytics {
    private static final String TAG = Analytics.class.getName();
    public static final String DEMO_EVENT = "android_sdk_demo_event";
    public static final int TYPE_LOGIN_SUCCESS = 1;
    public static final int TYPE_TRANSFER_SUCCESS = 2;
    public static final int TYPE_TRANSFER = 3;
    public static final int TYPE_APP_LIFECYCLE = 7;
    public static final int TYPE_SCREEN_LIFECYCLE = 8;
    public static final int TYPE_CLICK = 9;

    @SuppressLint("StaticFieldLeak")
    static volatile Analytics singleton = null;
    private Application application;
    private AnalyticsLifecycleCallback analyticsLifecycleCallback;
    private boolean shouldTrackLifecycle;
    private boolean shouldTrackDeepLink;
    private boolean shouldRecordScreen;
    private boolean shouldTrackScroll;
    private DataObject cacheDataObject;
    private String apiToken;
    private String merchantId;
    private int intervalSecond;
    private String deviceToken;
    private ArrayList<DataObject> listDataWaitToSend;

    private final ExecutorService analyticsExecutor;
    private final ScheduledExecutorService sendSyncScheduler;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Analytics getInstance() {
        synchronized (Analytics.class) {
            if (singleton == null) {
                singleton = new Builder().build();
            }
        }
        return singleton;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Analytics(Builder builder){
        application = builder.mApplication;
        shouldTrackLifecycle = builder.mShouldTrackLifecycle;
        shouldTrackDeepLink = builder.mShouldTrackDeepLink;
        shouldRecordScreen = builder.mShouldRecordScreen;
        intervalSecond = builder.mIntervalSecond;
        shouldTrackScroll = builder.mShouldTrackScroll;
        apiToken = builder.mApiToken;
        merchantId = builder.mMerchantId;
        deviceToken = builder.mDeviceToken;

        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_MERCHANT_ID, merchantId);
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_API_TOKEN, apiToken);

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

        analyticsLifecycleCallback = new AnalyticsLifecycleCallback(this, shouldTrackLifecycle,
                    shouldTrackDeepLink, shouldRecordScreen, shouldTrackScroll, application);

        application.registerActivityLifecycleCallbacks(analyticsLifecycleCallback);

        cacheDataObject = new DataObject.Builder()
                .withContext(Utils.getContextObject())
                .withProfileInfoObject(Utils.getProfileCreateDeviceObject(application))
                .withAnonymousId(Build.ID)
                .withProperties(Utils.getProperties())
                .build();
    }

    public void setDeviceToken(String deviceToken){
        this.deviceToken = deviceToken;
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_DEVICE_TOKEN, deviceToken);
        this.cacheDataObject.setProfileInfo(Utils.getProfileCreateDeviceObject(application));
    }

    public void track(String eventKey, int eventType, String detail){
        analyticsExecutor.submit(new Runnable() {
            @Override
            public void run() {
                cacheDataObject.setType("track");
                cacheDataObject.setEventKey(eventKey);
                if(eventKey.equals(DEMO_EVENT)){
                    cacheDataObject.getContext().setTraits((TraitsObject) new EventTraitsObject.Builder()
                            .withEventType(eventType)
                    .withDetail(detail).build());
                }
                sendSync(cacheDataObject);
                LogMobio.logD(TAG, cacheDataObject.toString());
            }
        });
    }

    public void autoSendSync(){
        LogMobio.logD("Analytics","Scheduler");
        if(listDataWaitToSend.size() > 0) {
            ArrayList<DataObject> listDataToSend = (ArrayList<DataObject>) listDataWaitToSend.clone();
            for (DataObject dataObject : listDataToSend) {
                sendSync(dataObject);
            }
        }
    }

    public void sendSync(DataObject dataObject){
//        Future<?> future = analyticsExecutor.submit(new Runnable() {
//            @Override
//            public void run() {
                SendSyncObject sendSyncObject = new SendSyncObject(dataObject);
                try {
                    LogMobio.logD("Analytics","1");
                    Response<Void> response = RetrofitClient.getInstance().getMyApi().sendSync(Utils.getHeader(application.getApplicationContext()), sendSyncObject).execute();
                    LogMobio.logD("Analytics","2");
                    if(response.code() != 200){
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        LogMobio.logD(TAG, "body = "+jObjError.toString());
                    }
                    else {
                        listDataWaitToSend.remove(dataObject);
                    }
                    LogMobio.logD(TAG, "code = "+response.code());
                } catch (IOException | JSONException e) {
                    LogMobio.logD(TAG, "api dies ");
                    e.printStackTrace();
                } catch (Exception e){
                    LogMobio.logD(TAG, "api dies hard");
                    e.printStackTrace();
                }
//            }
//        });
    }

    public void identify(IdentifyObject identifyObject){
        if(cacheDataObject != null){
            Future<?> future = analyticsExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    cacheDataObject.setType("identify");
                    cacheDataObject.setProfileInfo((ProfileBaseObject) new ProfileInfoObject.Builder()
                            .withContext(application.getApplicationContext())
                            .withCif(identifyObject.getCif())
                    .withEmail(identifyObject.getEmail())
                    .withPhoneNumber(identifyObject.getPhoneNumber())
                    .build());
                    sendSync(cacheDataObject);
                    //listDataWaitToSend.add(cacheDataObject);
                    LogMobio.logD("Analytics identify", cacheDataObject.toString());
                }
            });
        }
    }

    public void screen(String name){

    }

    void recordScreenViews(String name, int recordTime) {
        if(cacheDataObject != null){
            Future<?> future = analyticsExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    cacheDataObject.setType("screen");
                    cacheDataObject.setEventKey("sdk_mobio_android_record_screen");
                    cacheDataObject.getContext().setTraits((TraitsObject) new ScreenRecordTraitsObject(name, recordTime));
                    //listDataWaitToSend.add(cacheDataObject);
                    sendSync(cacheDataObject);
                    LogMobio.logD("Analytics recordScreenViews", cacheDataObject.toString());
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

        LogMobio.logD(TAG, "currentVersionName "+currentVersionName);
        LogMobio.logD(TAG, "currentVersionCode "+currentVersionCode);

        // Get the previous recorded version.
        String previousVersionName = SharedPreferencesUtils.getString(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_NAME);
        int previousVersionCode = SharedPreferencesUtils.getInt(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_CODE);

        // Check and track Application Updated.
        if(currentVersionCode != previousVersionCode){
            SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_NAME, currentVersionName);
            SharedPreferencesUtils.editInt(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_CODE, currentVersionCode);
            track(DEMO_EVENT, TYPE_APP_LIFECYCLE,"Application updated");
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
        private boolean mShouldTrackLifecycle = false;
        private boolean mShouldTrackDeepLink = false;
        private boolean mShouldRecordScreen = false;
        private boolean mShouldTrackScroll = false;
        private String mApiToken;
        private String mMerchantId;
        private int mIntervalSecond = 30;
        private String mDeviceToken;


        public Builder(){}

        public Builder withApplication(Application application){
            mApplication = application;
            return this;
        }

        public Builder shouldTrackLifecycle(boolean shouldTrackLifecycle){
            mShouldTrackLifecycle = shouldTrackLifecycle;
            return this;
        }

        public Builder shouldTrackDeepLink(boolean shouldTrackDeepLink){
            mShouldTrackDeepLink = shouldTrackDeepLink;
            return this;
        }

        public Builder shouldRecordScreen(boolean shouldRecordScreen){
            mShouldRecordScreen = shouldRecordScreen;
            return this;
        }

        public Builder withIntervalSecond(int second){
            mIntervalSecond = second;
            return this;
        }

        public Builder shouldTrackScroll(boolean shouldTrackScroll){
            mShouldTrackScroll = shouldTrackScroll;
            return this;
        }

        public Builder withMerchantId(String merchantId){
            mMerchantId = merchantId;
            return this;
        }

        public Builder withApiToken(String apiToken){
            mApiToken = apiToken;
            return this;
        }

        public Builder withDeviceToken(String deviceToken){
            mDeviceToken = deviceToken;
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public Analytics build(){
            return new Analytics(this);
        }
    }
}
