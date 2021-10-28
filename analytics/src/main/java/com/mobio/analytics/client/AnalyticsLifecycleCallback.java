package com.mobio.analytics.client;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobio.analytics.client.models.ScreenTraitsObject;
import com.mobio.analytics.client.models.EventTraitsObject;
import com.mobio.analytics.client.models.ScreenConfigObject;
import com.mobio.analytics.client.service.TerminateService;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AnalyticsLifecycleCallback implements Application.ActivityLifecycleCallbacks{
    private static final String TAG = AnalyticsLifecycleCallback.class.getName();
    private Analytics analytics;
    private boolean shouldTrackApplicationLifecycleEvents;
    private boolean shouldTrackScreenLifecycleEvents;
    private boolean trackDeepLinks;
    private boolean shouldRecordScreenViews;
    private boolean shouldTrackScrollEvent;
    private boolean alreadyLaunch;
    private int numStarted;
    private Application application;
    private Handler lifeCycleHandler;
    final int delay = 1000;
    private int countSecond;
    private HashMap<String, Integer> mapScreenAndCountTime;
    private HashMap<String, ScreenConfigObject> screenConfigObjectHashMap;

    public AnalyticsLifecycleCallback(Analytics analytics, boolean shouldTrackApplicationLifecycleEvents, boolean shouldTrackScreenLifecycleEvents,
                                      boolean trackDeepLinks, boolean shouldRecordScreenViews,
                                      boolean shouldTrackScrollEvent ,Application application, HashMap<String, ScreenConfigObject> screenConfigObjectHashMap) {
        this.analytics = analytics;
        this.shouldTrackApplicationLifecycleEvents = shouldTrackApplicationLifecycleEvents;
        this.shouldTrackScreenLifecycleEvents  = shouldTrackScreenLifecycleEvents;
        this.trackDeepLinks = trackDeepLinks;
        this.shouldRecordScreenViews = shouldRecordScreenViews;
        this.numStarted = 0;
        this.application = application;
        this.shouldTrackScrollEvent = shouldTrackScrollEvent;
        this.alreadyLaunch = SharedPreferencesUtils.getBool(application.getApplicationContext(), SharedPreferencesUtils.KEY_FIRST_START_APP);
        this.countSecond = 0;
        this.lifeCycleHandler = new Handler();
        this.mapScreenAndCountTime = new HashMap<>();
        this.screenConfigObjectHashMap = screenConfigObjectHashMap;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        if(!alreadyLaunch){
            alreadyLaunch = true;

            PackageInfo packageInfo = analytics.getPackageInfo(application);
            String currentVersionName = packageInfo.versionName;
            int currentVersionCode = packageInfo.versionCode;

            SharedPreferencesUtils.editBool(application.getApplicationContext(), SharedPreferencesUtils.KEY_FIRST_START_APP, true);
            SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_NAME, currentVersionName);
            SharedPreferencesUtils.editInt(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_CODE, currentVersionCode);

            analytics.track(Analytics.DEMO_EVENT, Analytics.TYPE_APP_LIFECYCLE,"Application first installed");
        }
        analytics.trackApplicationLifecycleEvents();
        if (trackDeepLinks) {
            trackDeepLink(activity);
        }

        activity.startService(new Intent(activity, TerminateService.class));
    }

    public String getNameOfActivity(Activity activity){
        String name = null;
        PackageManager packageManager = activity.getPackageManager();
        try {
            ActivityInfo info = packageManager.getActivityInfo(activity.getComponentName(), 0);
            CharSequence activityLabel = info.name;
            name = activityLabel.toString();
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("Activity Not Found: " + e.toString());
        } catch (Exception e) {
            LogMobio.logE(TAG, "Unable to track screen view for "+ activity.toString());
        }
        return name;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if(numStarted == 0){
            LogMobio.logD(TAG,"app went to foreground");
            SharedPreferencesUtils.editBool(activity, SharedPreferencesUtils.KEY_APP_FOREGROUD, true);
            if(shouldTrackApplicationLifecycleEvents){
                analytics.track(Analytics.DEMO_EVENT, Analytics.TYPE_APP_LIFECYCLE,"Application started");
            }
        }
        numStarted++;

        if(screenConfigObjectHashMap != null && screenConfigObjectHashMap.size() > 0){
            ScreenConfigObject screenConfigObject = screenConfigObjectHashMap.get(activity.getClass().getSimpleName());
            if(screenConfigObject != null) {
                ScreenTraitsObject screenTraitsObject = new ScreenTraitsObject(screenConfigObject.getTitle(),
                        screenConfigObject.getActivityName(), 0);

                if(shouldTrackScreenLifecycleEvents) {
                    EventTraitsObject eventTraitsObject = new EventTraitsObject.Builder()
                            .withEventType(Analytics.TYPE_SCREEN_LIFECYCLE)
                            .withDetail(screenConfigObject.getTitle() + " start").build();
                    analytics.track(Analytics.DEMO_EVENT, eventTraitsObject);
                }

                if (shouldRecordScreenViews) {
                    if(lifeCycleHandler != null) {
                        lifeCycleHandler.removeCallbacksAndMessages(null);
                        countSecond = 0;
                        lifeCycleHandler.postDelayed(new Runnable() {
                            public void run() {
                                countSecond++;
                                mapScreenAndCountTime.put(getNameOfActivity(activity), countSecond);
                                lifeCycleHandler.postDelayed(this, delay);

                                if(screenConfigObject.getVisitTime().length > 0){
                                    for(int i = 0 ; i < screenConfigObject.getVisitTime().length; i++){
                                        if(screenConfigObject.getVisitTime()[i] == countSecond){
                                            screenTraitsObject.setRecordTime(countSecond);
                                            analytics.recordScreen(screenTraitsObject);
                                        }
                                    }
                                }
                            }
                        }, delay);
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if(shouldTrackScrollEvent) {
            trackScrollEvent(activity);
        }

        LogMobio.logD(TAG, "onresume");

    }

    public void trackScrollEvent(Activity activity){
        for(View view : getAllViewCanScrollOrEdittext(activity.getWindow().getDecorView())){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(view instanceof ScrollView) {
                    int[] scrollRange = {0};
                    final ViewTreeObserver vto = view.getViewTreeObserver();
                    if (vto.isAlive()) {
                        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                int viewHeight = ((ScrollView) view).getChildAt(0).getMeasuredHeight();
                                int scrollviewHeight = view.getMeasuredHeight();
                                // handle viewWidth here...

                                LogMobio.logD("AnalyticsLifecycleCallback", "height " + viewHeight+"\n scrollviewHeight "+scrollviewHeight);

                                scrollRange[0] = viewHeight - scrollviewHeight;

                                if (Build.VERSION.SDK_INT < 16) {
                                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                } else {
                                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                            }
                        });
                    }

                    int[] percentScroll = {0};
                    view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                            percentScroll[0] = (int) (((float)i1/scrollRange[0]) * 100);
                            LogMobio.logD("AnalyticsLifecycleCallback", "percent " + percentScroll[0] +"%");
                        }
                    });
                }
                else if(view instanceof EditText){
                    ((EditText) view).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            LogMobio.logD("Saving", view.toString()+ " out text " + charSequence.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }
            }
        }
    }



    List<View> getAllViewCanScrollOrEdittext(View v){
        ArrayList<View> viewCanScroll = new ArrayList<>();
        ViewGroup viewgroup=(ViewGroup)v;
        for (int i=0;i<viewgroup.getChildCount();i++) {
            View v1=viewgroup.getChildAt(i);
            if (v1 instanceof ViewGroup) viewCanScroll.addAll(getAllViewCanScrollOrEdittext(v1));
            LogMobio.logD("SavingActivity",v1.toString() +" "+(v1 instanceof EditText));
            if(v1 instanceof ListView
                    || v1 instanceof ScrollView
                    || v1 instanceof NestedScrollView
                    || v1 instanceof RecyclerView
                    || v1 instanceof WebView || v1 instanceof EditText){
                viewCanScroll.add(v1);
            }
        }

        return viewCanScroll;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        LogMobio.logD(TAG, "onstop");
        numStarted--;
        if (numStarted == 0) {
            LogMobio.logD(TAG, "app went to background");
            SharedPreferencesUtils.editBool(activity, SharedPreferencesUtils.KEY_APP_FOREGROUD, false);
            if(lifeCycleHandler!=null) {
                lifeCycleHandler.removeCallbacksAndMessages(null);
            }

            if(shouldTrackApplicationLifecycleEvents){
                analytics.track(Analytics.DEMO_EVENT, Analytics.TYPE_APP_LIFECYCLE,"Application backgrouded");
            }
        }

        if(shouldTrackScreenLifecycleEvents && screenConfigObjectHashMap != null && screenConfigObjectHashMap.size() > 0){
            ScreenConfigObject screenConfigObject = screenConfigObjectHashMap.get(activity.getClass().getSimpleName());
            if(screenConfigObject != null) {

                EventTraitsObject eventTraitsObject = new EventTraitsObject.Builder()
                        .withEventType(Analytics.TYPE_SCREEN_LIFECYCLE)
                        .withDetail(screenConfigObject.getTitle() + " stop").build();
                analytics.track(Analytics.DEMO_EVENT, eventTraitsObject);

                if(shouldRecordScreenViews){
                    String name = getNameOfActivity(activity);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        ScreenTraitsObject screenTraitsObject = new ScreenTraitsObject(screenConfigObject.getTitle(),
                                screenConfigObject.getActivityName(), mapScreenAndCountTime.getOrDefault(name, 0));
                        analytics.recordScreen(screenTraitsObject);
                    }
                }

            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        //analytics.track("Activity "+getNameOfActivity(activity)+" destroyed");
    }

    private void trackDeepLink(Activity activity) {
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
                    LogMobio.logD(TAG, "parameter: "+parameter+" value: "+value);
                }
            }
        } catch (Exception e) {
            LogMobio.logE(TAG, e.toString());
        }

        //analytics.track("Deep Link Opened");
    }
}
