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
import com.mobio.analytics.client.models.IdentifyObject;
import com.mobio.analytics.client.models.ProfileBaseObject;
import com.mobio.analytics.client.models.ProfileInfoObject;
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
    @SuppressLint("StaticFieldLeak")
    static volatile Analytics singleton = null;
    private Application application;
    private AnalyticsLifecycleCallback analyticsLifecycleCallback;
    private boolean shouldTrackLifecycle;
    private boolean shouldTrackDeepLink;
    private boolean shouldRecordScreen;
    private boolean shouldTrackScroll;
    private DataObject cacheDataObject;
    private int intervalSecond;
    private ArrayList<DataObject> listDataWaitToSend;

    private final ExecutorService analyticsExecutor;
    private final ScheduledExecutorService sendSyncScheduler;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Analytics with(Context context) {
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

    public void track(String eventKey){
        if(cacheDataObject != null){
            LogMobio.logD("Analytics","Track");
            Future<?> future = analyticsExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    TrackObject trackObject = new TrackObject.Builder().withEventKey(eventKey).build();
                    cacheDataObject.setType("track");
                    if(eventKey.contains("Application")){
                        cacheDataObject.setEventKey("sdk_android_mobio_app_state");
                        cacheDataObject.getContext().setTraits((TraitsObject) new AppStateTraitsObject(eventKey));
                    }
                    else if(eventKey.contains("Activity")){
                        cacheDataObject.setEventKey("sdk_mobio_android_screen_track");
                        cacheDataObject.getContext().setTraits((TraitsObject) new ActivityTraitsObject(eventKey));
                    }
                    else if(eventKey.contains("thong bao")){
                        cacheDataObject.setEventKey("sdk_mobio_android_notification_event");
                        cacheDataObject.getContext().setTraits((TraitsObject) new ClickTraitsObject("thong bao"));
                    }
                    sendSync(cacheDataObject);
                    //listDataWaitToSend.add(cacheDataObject);
                    LogMobio.logD("Analytics track", cacheDataObject.toString());
                }
            });
        }
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
                    Response<Void> response = RetrofitClient.getInstance().getMyApi().sendSync(Utils.getHeader(), sendSyncObject).execute();
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
            track("Application updated");
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
        private int mIntervalSecond = 30;


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

        @RequiresApi(api = Build.VERSION_CODES.O)
        public Analytics build(){
            return new Analytics(this);
        }
    }
}
