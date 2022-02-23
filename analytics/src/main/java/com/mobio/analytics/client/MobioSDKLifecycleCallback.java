package com.mobio.analytics.client;

import static android.content.Context.ALARM_SERVICE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobio.analytics.R;
import com.mobio.analytics.client.adapters.AdsAdapter;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.models.ScreenConfigObject;
import com.mobio.analytics.client.models.ValueMap;
import com.mobio.analytics.client.service.TerminateService;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;
import com.mobio.analytics.client.view.popup.CustomDialog;
import com.mobio.analytics.client.view.HtmlController;
import com.mobio.analytics.client.view.popup.PermissionDialog;

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
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        if (!alreadyLaunch) {
            alreadyLaunch = true;

            PackageInfo packageInfo = mobioSDKClient.getPackageInfo(application);
            String currentVersionName = packageInfo.versionName;
            int currentVersionCode = packageInfo.versionCode;

            SharedPreferencesUtils.editBool(application.getApplicationContext(), SharedPreferencesUtils.KEY_FIRST_START_APP, true);
            SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_NAME, currentVersionName);
            SharedPreferencesUtils.editInt(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_CODE, currentVersionCode);

            mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Open_First_App, new ValueMap().put("build", String.valueOf(currentVersionCode))
                                                                                      .put("version", currentVersionName));
        }
        mobioSDKClient.trackApplicationLifecycleEvents();
        if (trackDeepLinks) {
            mobioSDKClient.trackDeepLink(activity);
        }
        activity.startService(new Intent(activity, TerminateService.class));
    }

    public void showPopup(ArrayList<NotiResponseObject> notiResponseObjectArrayList){
        if(currentActivity != null){
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialog dialog = new Dialog(currentActivity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.custom_list_ads);

                    ImageView imvClose = (ImageView) dialog.findViewById(R.id.imv_close);
                    RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.rv_ads);

                    AdsAdapter adsAdapter = new AdsAdapter(notiResponseObjectArrayList, new AdsAdapter.OnItemClick() {
                        @Override
                        public void onClick(NotiResponseObject notiResponseObject) {
                            dialog.dismiss();
                            Class des = null;
                            for(int i = 0; i < screenConfigObjectHashMap.values().size(); i++){
                                ScreenConfigObject screenConfigObject = (ScreenConfigObject) screenConfigObjectHashMap.values().toArray()[i];
                                if(screenConfigObject.getTitle().equals(notiResponseObject.getDes_screen())){
                                    des = screenConfigObject.getClassName();
                                    LogMobio.logD("ABCDE", screenConfigObject.getActivityName());
                                    break;
                                }
                            }
                            if(des != null){
                                Intent desIntent = new Intent(currentActivity, des);
                                currentActivity.startActivity(desIntent);
                            }
                        }
                    });
                    recyclerView.setLayoutManager(new LinearLayoutManager(currentActivity));
                    recyclerView.setAdapter(adsAdapter);

                    imvClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            //todo
                            //Analytics.getInstance().track(Analytics.DEMO_EVENT, Analytics.TYPE_CLICK,"Click No on Popup");

                        }
                    });

                    dialog.show();
                }
            });
        }
    }


    private Class findDes(NotiResponseObject notiResponseObject){
        Class des = null;
        for(int i = 0; i < screenConfigObjectHashMap.values().size(); i++){
            ScreenConfigObject screenConfigObject = (ScreenConfigObject) screenConfigObjectHashMap.values().toArray()[i];
            if(screenConfigObject.getTitle().equals(notiResponseObject.getDes_screen())){
                des = screenConfigObject.getClassName();
                break;
            }
        }
        return des;
    }

    public void showPopup(NotiResponseObject notiResponseObject) {
        if(currentActivity != null) {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(notiResponseObject.getType()==NotiResponseObject.TYPE_NATIVE) {
                        new CustomDialog(currentActivity, notiResponseObject, findDes(notiResponseObject)).show();
                    }
                    else {
                        new HtmlController(currentActivity, notiResponseObject, "", findDes(notiResponseObject)).showHtmlView();
                    }

//                    analytics.track(Analytics.SDK_Mobile_Test_Open_Popup_App,
//                            new ValueMap().put("action_time", Utils.getTimeUTC())
//                    .put("push_id", notiResponseObject.getPushId())
//                    .put("device", "Android"));
                    LogMobio.logD("QuanLA", "show popup "+notiResponseObject.getPushId());
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
                mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Open_App, new ValueMap().put("build", String.valueOf(SharedPreferencesUtils.getInt(activity, SharedPreferencesUtils.KEY_VERSION_CODE)))
                        .put("version", SharedPreferencesUtils.getString(activity, SharedPreferencesUtils.KEY_VERSION_NAME)));
            }
            if(!Utils.areNotificationsEnabled(application)){
                if(currentActivity != null) {
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new PermissionDialog(currentActivity).show();
                        }
                    });
                }
            }
            else {
                //todo get devicetoken
            }
        }
        numStarted++;

        if (screenConfigObjectHashMap != null && screenConfigObjectHashMap.size() > 0) {
            ScreenConfigObject screenConfigObject = screenConfigObjectHashMap.get(activity.getClass().getSimpleName());
            if (screenConfigObject != null) {

                if (shouldTrackScreenLifecycleEvents) {
                    mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Screen_Start_In_App, new ValueMap().put("screen_name", screenConfigObject.getTitle())
                            .put("time", Utils.getTimeUTC()));
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
                                            mobioSDKClient.recordScreen(new ValueMap().put("time_visit", countSecond).put("screen_name", screenConfigObject.getTitle()));
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

        LogMobio.logD(TAG, "onresume");

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

                                if (Build.VERSION.SDK_INT < 16) {
                                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                } else {
                                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }


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
        LogMobio.logD(TAG, "onstop");
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
                mobioSDKClient.track(MobioSDKClient.SDK_Mobile_Test_Screen_End_In_App, new ValueMap().put("screen_name", screenConfigObject.getTitle())
                        .put("time", Utils.getTimeUTC()));

//                if (shouldRecordScreenViews) {
//                    analytics.recordScreen(new ValueMap().put("time_visit", countSecond).put("screen_name", screenConfigObject.getTitle()));
//                }

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
}
