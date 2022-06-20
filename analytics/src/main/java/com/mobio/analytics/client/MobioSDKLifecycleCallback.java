package com.mobio.analytics.client;

import static android.content.Context.ALARM_SERVICE;

import static com.mobio.analytics.client.activity.PopupBuilderActivity.M_KEY_PUSH;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mobio.analytics.client.activity.PopupBuilderActivity;
import com.mobio.analytics.client.model.factory.ModelFactory;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.model.old.ScreenConfigObject;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.service.TerminateService;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;
import com.mobio.analytics.client.view.htmlPopup.HtmlController;
import com.mobio.analytics.client.view.popup.CustomDialog;

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
    private Handler lifeCycleFragmentHandler;
    final int delay = 1000;
    private int countSecond;
    private int countSecondFragment;
    private HashMap<String, ScreenConfigObject> activityConfigObjectHashMap;
    private HashMap<String, ScreenConfigObject> fragmentConfigObjectHashMap;
    private Activity currentActivity;
    private AlarmManager alarmManager;
    private FragmentManager.FragmentLifecycleCallbacks onFragmentLifecycleCallbacks;

    public MobioSDKLifecycleCallback(MobioSDKClient mobioSDKClient, boolean shouldTrackApplicationLifecycleEvents, boolean shouldTrackScreenLifecycleEvents,
                                     boolean trackDeepLinks, boolean shouldRecordScreenViews,
                                     boolean shouldTrackScrollEvent, Application application, HashMap<String, ScreenConfigObject> activityConfigObjectHashMap, HashMap<String, ScreenConfigObject> fragmentConfigObjectHashMap) {
        this.mobioSDKClient = mobioSDKClient;
        this.shouldTrackApplicationLifecycleEvents = shouldTrackApplicationLifecycleEvents;
        this.shouldTrackScreenLifecycleEvents = shouldTrackScreenLifecycleEvents;
        this.trackDeepLinks = trackDeepLinks;
        this.shouldRecordScreenViews = shouldRecordScreenViews;
        this.numStarted = 0;
        this.application = application;
        this.shouldTrackScrollEvent = shouldTrackScrollEvent;
        this.alreadyLaunch = SharedPreferencesUtils.getBool(application.getApplicationContext(), SharedPreferencesUtils.M_KEY_FIRST_START_APP);
        this.countSecond = 0;
        this.lifeCycleHandler = new Handler();
        this.activityConfigObjectHashMap = activityConfigObjectHashMap;
        this.lifeCycleFragmentHandler = new Handler();
        this.fragmentConfigObjectHashMap = fragmentConfigObjectHashMap;

        alarmManager = (AlarmManager) application.getSystemService(ALARM_SERVICE);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
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

    private void doFirstOpen() {
        PackageInfo packageInfo = mobioSDKClient.getPackageInfo(application);
        String currentVersionName = packageInfo.versionName;
        int currentVersionCode = packageInfo.versionCode;

        SharedPreferencesUtils.editBool(application.getApplicationContext(), SharedPreferencesUtils.M_KEY_FIRST_START_APP, true);
        SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.M_KEY_VERSION_NAME, currentVersionName);
        SharedPreferencesUtils.editInt(application.getApplicationContext(), SharedPreferencesUtils.M_KEY_VERSION_CODE, currentVersionCode);
        mobioSDKClient.identify();
        mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Open_First_App, new Properties().putValue("build", String.valueOf(currentVersionCode))
                .putValue("version", currentVersionName));
    }

    private Class<?> findDes(Push push) {
        Class<?> des = null;
        for (int i = 0; i < activityConfigObjectHashMap.values().size(); i++) {
            ScreenConfigObject screenConfigObject = (ScreenConfigObject) activityConfigObjectHashMap.values().toArray()[i];
            if (screenConfigObject.getTitle().equals(push.getAlert().getDesScreen())) {
                des = screenConfigObject.getClassName();
                break;
            }
        }
        return des;
    }

    public void showPopup(Push push) {
        if (currentActivity != null) {
            currentActivity.runOnUiThread(() -> {
                Push.Alert alert = push.getAlert();
                if (alert == null) return;

                String contentType = alert.getContentType();
                if (contentType == null) return;
                if (contentType.equals(Push.Alert.TYPE_POPUP) || contentType.equals(Push.Alert.TYPE_HTML)) {
                    if (push.getData() != null
                            && push.getData().getString("position") != null
                            && !push.getData().getString("position").equals(HtmlController.POSITION_CENTER)) {
                        HtmlController.showHtmlPopup(currentActivity, push, "", false);
                    } else {
                        startPopupActivity(currentActivity, push);
                    }
                } else {
                    CustomDialog.showCustomDialog(currentActivity, push, findDes(push));
                }
            });
        }
    }

    private void startPopupActivity(Activity currentActivity, Push push) {
        Intent i = new Intent(currentActivity, PopupBuilderActivity.class);
        i.putExtra(M_KEY_PUSH, new Gson().toJson(push));
        currentActivity.startActivity(i);
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
            e.printStackTrace();
        }
        return name;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (isActivityValid(activity)) {
            currentActivity = activity;
        }
        if (numStarted == 0) {
            mobioSDKClient.identify();
            SharedPreferencesUtils.editBool(activity, SharedPreferencesUtils.M_KEY_APP_FOREGROUD, true);
            if (shouldTrackApplicationLifecycleEvents) {
                mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Open_App, new Properties().putValue("build", String.valueOf(SharedPreferencesUtils.getInt(activity, SharedPreferencesUtils.M_KEY_VERSION_CODE)))
                        .putValue("version", SharedPreferencesUtils.getString(activity, SharedPreferencesUtils.M_KEY_VERSION_NAME)));
            }

            mobioSDKClient.trackNotificationOnOff(activity);
            requestAppPermissions(activity);
        }
        numStarted++;

        if (activityConfigObjectHashMap != null && activityConfigObjectHashMap.size() > 0) {
            ScreenConfigObject screenConfigObject = activityConfigObjectHashMap.get(activity.getClass().getSimpleName());
            if (screenConfigObject != null) {

                if (shouldTrackScreenLifecycleEvents) {
                    long actionTime = System.currentTimeMillis();
                    mobioSDKClient.track(ModelFactory.createBaseList(
                            ModelFactory.createBase("screen", new Properties().putValue("screen_name", screenConfigObject.getTitle())),
                            "view", actionTime, "digienty"), actionTime);
                }

                if (shouldRecordScreenViews && screenConfigObject.getVisitTime().length > 0) {
                    countTimeScreen(screenConfigObject, lifeCycleHandler);
                }
            }
        }

        onFragmentLifecycleCallbacks = getOnFragmentLifecycleCallbacks();
        unregisterFragmentCallbacks(activity);
        registerFragmentCallbacks(activity);
    }

    private void countTimeScreen(ScreenConfigObject screenConfigObject, Handler timeHandler){
        if (timeHandler != null) {
            timeHandler.removeCallbacksAndMessages(null);
            final int[] countSecond = {0};
            timeHandler.postDelayed(new Runnable() {
                public void run() {
                    countSecond[0]++;
                    timeHandler.postDelayed(this, delay);

                    for (int i = 0; i < screenConfigObject.getVisitTime().length; i++) {
                        if (screenConfigObject.getVisitTime()[i] == countSecond[0]) {
                            long action_time = System.currentTimeMillis();
                            mobioSDKClient.track(ModelFactory.createBaseList(
                                    ModelFactory.createBase("screen", new Properties().putValue("time_visit", countSecond[0])
                                            .putValue("screen_name", screenConfigObject.getTitle())),
                                    "time_visit", action_time, "digienty"), action_time);
                        }
                    }
                    if(countSecond[0] == screenConfigObject.getVisitTime()[screenConfigObject.getVisitTime().length-1]){
                        timeHandler.removeCallbacksAndMessages(null);
                    }
                }
            }, delay);
        }
    }

    private void registerFragmentCallbacks(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            fragmentManager.registerFragmentLifecycleCallbacks(onFragmentLifecycleCallbacks, true);
        }
    }

    private void unregisterFragmentCallbacks(Activity activity) {
        if (activity instanceof AppCompatActivity) {
            FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            fragmentManager.unregisterFragmentLifecycleCallbacks(onFragmentLifecycleCallbacks);
        }
    }

    private FragmentManager.FragmentLifecycleCallbacks getOnFragmentLifecycleCallbacks() {
        return new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentResumed(FragmentManager fm, Fragment f) {
                super.onFragmentResumed(fm, f);
                if (fragmentConfigObjectHashMap != null && fragmentConfigObjectHashMap.size() > 0) {
                    ScreenConfigObject screenConfigObject = fragmentConfigObjectHashMap.get(f.getClass().getSimpleName());
                    if (screenConfigObject != null) {
                        if (shouldTrackScreenLifecycleEvents) {
                            long actionTime = System.currentTimeMillis();
                            mobioSDKClient.track(ModelFactory.createBaseList(
                                    ModelFactory.createBase("screen", new Properties().putValue("screen_name", screenConfigObject.getTitle())),
                                    "view", actionTime, "digienty"), actionTime);
                        }
                        if (shouldRecordScreenViews && screenConfigObject.getVisitTime().length > 0) {
                            countTimeScreen(screenConfigObject, lifeCycleFragmentHandler);
                        }
                    }
                }
            }

            @Override
            public void onFragmentPaused(FragmentManager fm, Fragment f) {
                super.onFragmentPaused(fm, f);
                if (shouldTrackScreenLifecycleEvents && fragmentConfigObjectHashMap != null && fragmentConfigObjectHashMap.size() > 0) {
                    ScreenConfigObject screenConfigObject = fragmentConfigObjectHashMap.get(f.getClass().getSimpleName());
                    if (screenConfigObject != null) {
                        mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Screen_End_In_App, new Properties().putValue("screen_name", screenConfigObject.getTitle())
                                .putValue("time", Utils.getTimeUTC()));
                    }
                }
                if (lifeCycleFragmentHandler != null) {
                    lifeCycleFragmentHandler.removeCallbacksAndMessages(null);
                }
            }
        };
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (shouldTrackScrollEvent) {
            trackScrollEvent(activity);
        }
    }

    private boolean isActivityValid(Activity activity) {
        for (int i = 0; i < activityConfigObjectHashMap.values().size(); i++) {
            ScreenConfigObject screenConfigObject = (ScreenConfigObject) activityConfigObjectHashMap.values().toArray()[i];
            if (screenConfigObject.getClassName().getSimpleName().equals(activity.getClass().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    private void trackScrollEvent(Activity activity) {
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


                            scrollRange[0] = viewHeight - scrollviewHeight;

                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);


                        }
                    });
                }

                int[] percentScroll = {0};
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewTreeObserver observer = view.getViewTreeObserver();
                        observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                            @Override
                            public void onScrollChanged() {
                                int scrollX = view.getScrollX();
                                int scrollY = view.getScrollY();
                                percentScroll[0] = (int) (((float) scrollY / scrollRange[0]) * 100);
                                if (percentScroll[0] % 5 == 0) {
                                    if (activityConfigObjectHashMap != null && activityConfigObjectHashMap.size() > 0) {
                                        ScreenConfigObject screenConfigObject = activityConfigObjectHashMap.get(activity.getClass().getSimpleName());
                                        if (screenConfigObject == null) return;
                                        long action_time = System.currentTimeMillis();
                                        mobioSDKClient.track(ModelFactory.createBaseList(
                                                ModelFactory.createBase("screen", new Properties().putValue("percentage_scroll", percentScroll[0])
                                                        .putValue("screen_name", screenConfigObject.getTitle())
                                                        .putValue("direction", "vertical").putValue("unit", "percent")),
                                                "scroll", action_time, "digienty"), action_time);
                                    }
                                }
                            }
                        });
                    }
                });
            }

        }
    }


    private List<View> getAllViewCanScrollOrEdittext(View v) {
        ArrayList<View> viewCanScroll = new ArrayList<>();
        ViewGroup viewgroup = (ViewGroup) v;
        for (int i = 0; i < viewgroup.getChildCount(); i++) {
            View v1 = viewgroup.getChildAt(i);
            if (v1 instanceof ViewGroup) viewCanScroll.addAll(getAllViewCanScrollOrEdittext(v1));
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
            SharedPreferencesUtils.editBool(activity, SharedPreferencesUtils.M_KEY_APP_FOREGROUD, false);
            if (lifeCycleHandler != null) {
                lifeCycleHandler.removeCallbacksAndMessages(null);
            }
        }

        if (shouldTrackScreenLifecycleEvents && activityConfigObjectHashMap != null && activityConfigObjectHashMap.size() > 0) {
            ScreenConfigObject screenConfigObject = activityConfigObjectHashMap.get(activity.getClass().getSimpleName());
            if (screenConfigObject != null) {
                mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Screen_End_In_App, new Properties().putValue("screen_name", screenConfigObject.getTitle())
                        .putValue("time", Utils.getTimeUTC()));
            }
        }

        //todo duplicate if
        if (numStarted == 0) {
            mobioSDKClient.processPendingJson();
        }

        unregisterFragmentCallbacks(activity);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    private void requestAppPermissions(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (Utils.hasWritePermissions(activity)) {
            return;
        }

        ActivityCompat.requestPermissions(activity,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, 999); // your request code
    }


}
