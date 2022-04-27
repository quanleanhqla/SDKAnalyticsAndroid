package com.mobio.analytics.client;

import static android.content.Context.ALARM_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.model.old.ScreenConfigObject;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.service.TerminateService;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;
import com.mobio.analytics.client.view.popup.CustomDialog;
import com.mobio.analytics.client.view.htmlPopup.HtmlController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobioSDKLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = MobioSDKLifecycleCallback.class.getName();
    private MobioSDKClient mobioSDKClient;
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
    private Activity currentActivity;
    private AlarmManager alarmManager;

    public MobioSDKLifecycleCallback(MobioSDKClient mobioSDKClient, boolean shouldTrackApplicationLifecycleEvents, boolean shouldTrackScreenLifecycleEvents,
                                     boolean trackDeepLinks, boolean shouldRecordScreenViews,
                                     boolean shouldTrackScrollEvent, Application application, HashMap<String, ScreenConfigObject> screenConfigObjectHashMap) {
        this.mobioSDKClient = mobioSDKClient;
        this.shouldTrackApplicationLifecycleEvents = shouldTrackApplicationLifecycleEvents;
        this.shouldTrackScreenLifecycleEvents = shouldTrackScreenLifecycleEvents;
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

        alarmManager = (AlarmManager) application.getSystemService(ALARM_SERVICE);

        LogMobio.logD("QuanLA","12");
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        LogMobio.logD("QuanLA","11 "+alreadyLaunch);
        if (!alreadyLaunch) {
            alreadyLaunch = true;
            doFirstOpen();
        }
        mobioSDKClient.trackApplicationLifecycleEvents();
        if (trackDeepLinks) {
            mobioSDKClient.trackDeepLink(activity);
        }
        activity.startService(new Intent(activity, TerminateService.class));
    }

    private void doFirstOpen(){
        PackageInfo packageInfo = mobioSDKClient.getPackageInfo(application);
        String currentVersionName = packageInfo.versionName;
        int currentVersionCode = packageInfo.versionCode;

        SharedPreferencesUtils.editBool(application.getApplicationContext(), SharedPreferencesUtils.KEY_FIRST_START_APP, true);
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_NAME, currentVersionName);
        SharedPreferencesUtils.editInt(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_CODE, currentVersionCode);
        LogMobio.logD("QuanLA", "4");
        mobioSDKClient.identify();
        mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Open_First_App, new Properties().putValue("build", String.valueOf(currentVersionCode))
                .putValue("version", currentVersionName));
        LogMobio.logD("QuanLA", "4");
    }

    private Class<?> findDes(Push push){
        Class<?> des = null;
        for(int i = 0; i < screenConfigObjectHashMap.values().size(); i++){
            ScreenConfigObject screenConfigObject = (ScreenConfigObject) screenConfigObjectHashMap.values().toArray()[i];
            if(screenConfigObject.getTitle().equals(push.getDesScreen())){
                des = screenConfigObject.getClassName();
                break;
            }
        }
        return des;
    }

    public void showPopup(Push push) {
        if(currentActivity != null) {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Push.Alert alert = push.getAlert();
                    if(alert == null) return;

                    String contentType = alert.getContentType();
                    if(contentType == null) return;
                    if(contentType.equals(Push.Alert.TYPE_POPUP) || contentType.equals(Push.Alert.TYPE_HTML)) {
                        new HtmlController(currentActivity, push, "", false).showHtmlView();
                    }
                    else {
                        new CustomDialog(currentActivity, push, findDes(push)).show();
                    }
                }
            });
        }
    }

    public String getNameOfActivity(Activity activity) {
        String name = null;
        PackageManager packageManager = activity.getPackageManager();
        try {
            ActivityInfo info = packageManager.getActivityInfo(activity.getComponentName(), 0);
            CharSequence activityLabel = info.name;
            name = activityLabel.toString();
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("Activity Not Found: " + e.toString());
        } catch (Exception e) {
            LogMobio.logE(TAG, "Unable to track screen view for " + activity.toString());
        }
        return name;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
        if (numStarted == 0) {
            LogMobio.logD(TAG, "app went to foreground");
            SharedPreferencesUtils.editBool(activity, SharedPreferencesUtils.KEY_APP_FOREGROUD, true);
            if (shouldTrackApplicationLifecycleEvents) {
                mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Open_App, new Properties().putValue("build", String.valueOf(SharedPreferencesUtils.getInt(activity, SharedPreferencesUtils.KEY_VERSION_CODE)))
                        .putValue("version", SharedPreferencesUtils.getString(activity, SharedPreferencesUtils.KEY_VERSION_NAME)));
            }

            mobioSDKClient.trackNotificationOnOff(currentActivity);
            requestAppPermissions();
        }
        numStarted++;

        if (screenConfigObjectHashMap != null && screenConfigObjectHashMap.size() > 0) {
            ScreenConfigObject screenConfigObject = screenConfigObjectHashMap.get(activity.getClass().getSimpleName());
            if (screenConfigObject != null) {

                if (shouldTrackScreenLifecycleEvents) {
                    mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Screen_Start_In_App, new Properties().putValue("screen_name", screenConfigObject.getTitle())
                            .putValue("time", Utils.getTimeUTC()));
                }

                if (shouldRecordScreenViews) {
                    if (lifeCycleHandler != null) {
                        lifeCycleHandler.removeCallbacksAndMessages(null);
                        countSecond = 0;
                        lifeCycleHandler.postDelayed(new Runnable() {
                            public void run() {
                                countSecond++;
                                mapScreenAndCountTime.put(getNameOfActivity(activity), countSecond);
                                lifeCycleHandler.postDelayed(this, delay);

                                if (screenConfigObject.getVisitTime().length > 0) {
                                    for (int i = 0; i < screenConfigObject.getVisitTime().length; i++) {
                                        if (screenConfigObject.getVisitTime()[i] == countSecond) {
                                            mobioSDKClient.recordScreen(new Properties().putValue("time_visit", countSecond)
                                                    .putValue("screen_name", screenConfigObject.getTitle()));
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
        if (shouldTrackScrollEvent) {
            trackScrollEvent(activity);
        }
    }

    public void trackScrollEvent(Activity activity) {
        for (View view : getAllViewCanScrollOrEdittext(activity.getWindow().getDecorView())) {
                if (view instanceof ScrollView) {
                    int[] scrollRange = {0};
                    final ViewTreeObserver vto = view.getViewTreeObserver();
                    if (vto.isAlive()) {
                        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                int viewHeight = ((ScrollView) view).getChildAt(0).getMeasuredHeight();
                                int scrollviewHeight = view.getMeasuredHeight();
                                // handle viewWidth here...

                                LogMobio.logD("AnalyticsLifecycleCallback", "height " + viewHeight + "\n scrollviewHeight " + scrollviewHeight);

                                scrollRange[0] = viewHeight - scrollviewHeight;

                                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);


                            }
                        });
                    }

                    int[] percentScroll = {0};
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                            @Override
                            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                                percentScroll[0] = (int) (((float) i1 / scrollRange[0]) * 100);
                                LogMobio.logD("AnalyticsLifecycleCallback", "percent " + percentScroll[0] + "%");
                            }
                        });
                    }
                } else if (view instanceof EditText) {
                    ((EditText) view).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            LogMobio.logD("Saving", view.toString() + " out text " + charSequence.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }

        }
    }


    List<View> getAllViewCanScrollOrEdittext(View v) {
        ArrayList<View> viewCanScroll = new ArrayList<>();
        ViewGroup viewgroup = (ViewGroup) v;
        for (int i = 0; i < viewgroup.getChildCount(); i++) {
            View v1 = viewgroup.getChildAt(i);
            if (v1 instanceof ViewGroup) viewCanScroll.addAll(getAllViewCanScrollOrEdittext(v1));
            LogMobio.logD("SavingActivity", v1.toString() + " " + (v1 instanceof EditText));
            if (v1 instanceof ListView
                    || v1 instanceof ScrollView
                    || v1 instanceof NestedScrollView
                    || v1 instanceof RecyclerView
                    || v1 instanceof WebView || v1 instanceof EditText) {
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
        numStarted--;

        if (numStarted == 0) {
            currentActivity = null;
            LogMobio.logD(TAG, "app went to background");
            SharedPreferencesUtils.editBool(activity, SharedPreferencesUtils.KEY_APP_FOREGROUD, false);
            if (lifeCycleHandler != null) {
                lifeCycleHandler.removeCallbacksAndMessages(null);
            }
        }

        if (shouldTrackScreenLifecycleEvents && screenConfigObjectHashMap != null && screenConfigObjectHashMap.size() > 0) {
            ScreenConfigObject screenConfigObject = screenConfigObjectHashMap.get(activity.getClass().getSimpleName());
            if (screenConfigObject != null) {
                mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Screen_End_In_App, new Properties().putValue("screen_name", screenConfigObject.getTitle())
                        .putValue("time", Utils.getTimeUTC()));
            }
        }

        //todo duplicate if
        if(numStarted == 0){
            mobioSDKClient.processPendingJson();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (Utils.hasWritePermissions(currentActivity) ) {
            return;
        }

        ActivityCompat.requestPermissions(currentActivity,
                new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, 999); // your request code
    }

    
}
