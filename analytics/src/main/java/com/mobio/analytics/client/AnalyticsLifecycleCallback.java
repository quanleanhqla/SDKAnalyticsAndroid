package com.mobio.analytics.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobio.analytics.R;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.models.ScreenConfigObject;
import com.mobio.analytics.client.models.ValueMap;
import com.mobio.analytics.client.service.TerminateService;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyticsLifecycleCallback implements Application.ActivityLifecycleCallbacks {
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
    private Activity currentActivity;
    private Dialog dialog;
    private final String FILE_URI_SCHEME_PREFIX = "file://";
    private final String FILE_PATH_SEPARATOR = "/";
    private final String HTML_MIME_TYPE = "text/html";
    private final String HTML_ENCODING = "utf-8";
    private FrameLayout root;

    public AnalyticsLifecycleCallback(Analytics analytics, boolean shouldTrackApplicationLifecycleEvents, boolean shouldTrackScreenLifecycleEvents,
                                      boolean trackDeepLinks, boolean shouldRecordScreenViews,
                                      boolean shouldTrackScrollEvent, Application application, HashMap<String, ScreenConfigObject> screenConfigObjectHashMap) {
        this.analytics = analytics;
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
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        if (!alreadyLaunch) {
            alreadyLaunch = true;

            PackageInfo packageInfo = analytics.getPackageInfo(application);
            String currentVersionName = packageInfo.versionName;
            int currentVersionCode = packageInfo.versionCode;

            SharedPreferencesUtils.editBool(application.getApplicationContext(), SharedPreferencesUtils.KEY_FIRST_START_APP, true);
            SharedPreferencesUtils.editString(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_NAME, currentVersionName);
            SharedPreferencesUtils.editInt(application.getApplicationContext(), SharedPreferencesUtils.KEY_VERSION_CODE, currentVersionCode);

            analytics.track(Analytics.SDK_Mobile_Test_Open_First_App, new ValueMap().put("build", String.valueOf(currentVersionCode))
                                                                                      .put("version", currentVersionName));
        }
        analytics.trackApplicationLifecycleEvents();
        if (trackDeepLinks) {
            trackDeepLink(activity);
        }
        activity.startService(new Intent(activity, TerminateService.class));
    }

    private void addPermissionNoti(){
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, application.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", application.getPackageName());
            intent.putExtra("app_uid", application.getApplicationInfo().uid);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + application.getPackageName()));
        }
        currentActivity.startActivity(intent);
    }

    private boolean isHtml(String content){
        // adapted from post by Phil Haack and modified to match better
        String tagStart=
                "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)\\>";
        String tagEnd=
                "\\</\\w+\\>";
        String tagSelfClosing=
                "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)/\\>";
        String htmlEntity=
                "&[a-zA-Z][a-zA-Z0-9]+;";
        Pattern htmlPattern=Pattern.compile(
                "("+tagStart+".*"+tagEnd+")|("+tagSelfClosing+")|("+htmlEntity+")",
                Pattern.DOTALL
        );

        boolean ret = false;
        if(content != null){
            ret = htmlPattern.matcher(content).find();
        }
        return ret;
    }

    public void showHtmlPopup(NotiResponseObject notiResponseObject, Class des){
        root = getWindowRoot(currentActivity);
        root.addView(createPrimaryContainer(currentActivity,
                "", notiResponseObject, des));
    }

    private FrameLayout getWindowRoot(Activity activity) {
        return (FrameLayout) activity.getWindow()
                .getDecorView()
                .findViewById(android.R.id.content)
                .getRootView();
    }

    private void createWebview(ViewGroup container,String assetPath,  NotiResponseObject notiResponseObject, Class des){
        currentActivity.runOnUiThread(new Runnable() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void run() {
                WebView webView = new WebView(currentActivity);
                webView.setId(ViewCompat.generateViewId());
                webView.setFocusableInTouchMode(true);
                webView.requestFocus();

                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setUseWideViewPort(true);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setDisplayZoomControls(false);
                webSettings.setDomStorageEnabled(true);
                webSettings.setAllowFileAccess(true);

                webView.addJavascriptInterface(new JS_INTERFACE(currentActivity, des), "sdk");
                if(notiResponseObject.getType() == NotiResponseObject.TYPE_HTML_URL) {
                    webView.loadUrl(notiResponseObject.getContent());
                }
                else if(notiResponseObject.getType() == NotiResponseObject.TYPE_HTML) {
                webView.loadDataWithBaseURL(assetPath,
                        notiResponseObject.getData(),
                        HTML_MIME_TYPE,
                        HTML_ENCODING, null);
                }

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                webView.setLayoutParams(layoutParams);
                container.addView(webView);
            }
        });
    }

    @SuppressLint("ResourceType")
    private View createPrimaryContainer(Activity activity, String assetPath, NotiResponseObject notiResponseObject, Class des){
        RelativeLayout containerLayout = new RelativeLayout(activity);
        containerLayout.setId(20001);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        containerLayout.setLayoutParams(layoutParams);
        createWebview(containerLayout, assetPath, notiResponseObject, des);
        return containerLayout;
    }

    public void showPopup(NotiResponseObject notiResponseObject){
        if(currentActivity != null) {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Class des = null;
                    for(int i = 0; i < screenConfigObjectHashMap.values().size(); i++){
                        ScreenConfigObject screenConfigObject = (ScreenConfigObject) screenConfigObjectHashMap.values().toArray()[i];
                        if(screenConfigObject.getTitle().equals(notiResponseObject.getDes_screen())){
                            des = screenConfigObject.getClassName();
                            LogMobio.logD("ABC", screenConfigObject.getActivityName());
                            break;
                        }
                    }

                    if(dialog == null) {
                        dialog = new Dialog(currentActivity);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCancelable(false);
                    }
                    if(notiResponseObject.getType()==NotiResponseObject.TYPE_NATIVE) {
                        dialog.setContentView(com.mobio.analytics.R.layout.custom_popup);

                        Button btnAction = (Button) dialog.findViewById(com.mobio.analytics.R.id.btn_action);
                        ImageView imvClose = (ImageView) dialog.findViewById(com.mobio.analytics.R.id.imv_close);
                        TextView tvTitle = (TextView) dialog.findViewById(com.mobio.analytics.R.id.tv_title);
                        TextView tvDetail = (TextView) dialog.findViewById(com.mobio.analytics.R.id.tv_detail);
                        Button btnCancel = (Button) dialog.findViewById(com.mobio.analytics.R.id.btn_cancel);

                        tvTitle.setText(notiResponseObject.getTitle());
                        tvDetail.setText(notiResponseObject.getContent());

                        imvClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                //todo
                                //Analytics.getInstance().track(Analytics.DEMO_EVENT, Analytics.TYPE_CLICK,"Click No on Popup");

                            }
                        });

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        Class finalDes = des;
                        btnAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                //todo
                                if (finalDes != null && !currentActivity.getClass().getSimpleName().equals("LoginActivity")) {
                                    Intent desIntent = new Intent(currentActivity, finalDes);
                                    currentActivity.startActivity(desIntent);
                                }
                            }
                        });
                        dialog.show();
                    }
                    else {

                        //dialog.setContentView(R.layout.custom_html_popup);

//                        WebView webView = new WebView(currentActivity);
//                        webView.addJavascriptInterface(new JS_INTERFACE(currentActivity, des), "sdk");
//                        webView.setWebViewClient(new WebViewClient());
//
//                        WebSettings webSettings = webView.getSettings();
//                        webSettings.setJavaScriptEnabled(true);
//                        webSettings.setUseWideViewPort(true);
//                        webSettings.setLoadWithOverviewMode(true);
//                        webSettings.setDisplayZoomControls(false);
//                        webSettings.setDomStorageEnabled(true);
//                        webSettings.setAllowFileAccess(true);
//
//                        if(notiResponseObject.getType() == NotiResponseObject.TYPE_HTML_URL) {
//                            webView.loadUrl(notiResponseObject.getData());
//                        }
//                        else if(notiResponseObject.getType() == NotiResponseObject.TYPE_HTML){
//                            webView.loadDataWithBaseURL("",
//                                    notiResponseObject.getData(),
//                                    HTML_MIME_TYPE,
//                                    HTML_ENCODING, null);
//                        }
//                        webView.requestFocus();
//
//                        dialog.setContentView(webView);

                        showHtmlPopup(notiResponseObject, des);
                    }
                }
            });
        }
    }

    public class JS_INTERFACE {

        Context mContext;
        Class dest;

        /**
         * Instantiate the interface and set the context
         */
        JS_INTERFACE(Context c, Class des) {
            dest = des;
            mContext = c;
        }

        @SuppressLint("ResourceType")
        @JavascriptInterface
        public void trackClick() {
            if (dest != null && !currentActivity.getClass().getSimpleName().equals("LoginActivity")) {
                Intent desIntent = new Intent(currentActivity, dest);
                currentActivity.startActivity(desIntent);
            }
            if(root != null){
                root.removeView(root.findViewById(20001));
            }
        }

        @SuppressLint("ResourceType")
        @JavascriptInterface
        public void dismissMessage() {
            if(root != null){
                root.removeView(root.findViewById(20001));
            }
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
                analytics.track(Analytics.SDK_Mobile_Test_Open_App, new ValueMap().put("build", String.valueOf(SharedPreferencesUtils.getInt(activity, SharedPreferencesUtils.KEY_VERSION_CODE)))
                        .put("version", SharedPreferencesUtils.getString(activity, SharedPreferencesUtils.KEY_VERSION_NAME)));
            }
            if(!Utils.areNotificationsEnabled(application)){
                if(currentActivity != null) {
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Dialog dialog = new Dialog(currentActivity);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCancelable(false);
                            dialog.setContentView(com.mobio.analytics.R.layout.custom_popup);

                            Button btnAction = (Button) dialog.findViewById(com.mobio.analytics.R.id.btn_action);
                            ImageView imvClose = (ImageView) dialog.findViewById(com.mobio.analytics.R.id.imv_close);
                            TextView tvTitle = (TextView) dialog.findViewById(com.mobio.analytics.R.id.tv_title);
                            TextView tvDetail = (TextView) dialog.findViewById(com.mobio.analytics.R.id.tv_detail);
                            Button btnCancel = (Button) dialog.findViewById(com.mobio.analytics.R.id.btn_cancel);

                            tvTitle.setText("Cấp quyền thông báo!");
                            tvDetail.setText("Quý khách vui lòng cấp quyền thông báo");

                            imvClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    //todo call api update device token

                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    //todo call api update device token
                                }
                            });

                            btnAction.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    addPermissionNoti();
                                }
                            });

                            dialog.show();
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
                    analytics.track(Analytics.SDK_Mobile_Test_Screen_Start_In_App, new ValueMap().put("screen_name", screenConfigObject.getTitle())
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
                                            analytics.recordScreen(new ValueMap().put("time_visit", countSecond).put("screen_name", screenConfigObject.getTitle()));
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                    view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                            percentScroll[0] = (int) (((float) i1 / scrollRange[0]) * 100);
                            LogMobio.logD("AnalyticsLifecycleCallback", "percent " + percentScroll[0] + "%");
                        }
                    });
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

            if (shouldTrackApplicationLifecycleEvents) {
//                analytics.track("", new ValueMap().put("version", String.valueOf(SharedPreferencesUtils.getInt(activity, SharedPreferencesUtils.KEY_VERSION_CODE)))
//                        .put("build", SharedPreferencesUtils.getString(activity, SharedPreferencesUtils.KEY_VERSION_NAME)));
            }
        }

        if (shouldTrackScreenLifecycleEvents && screenConfigObjectHashMap != null && screenConfigObjectHashMap.size() > 0) {
            ScreenConfigObject screenConfigObject = screenConfigObjectHashMap.get(activity.getClass().getSimpleName());
            if (screenConfigObject != null) {
                analytics.track(Analytics.SDK_Mobile_Test_Screen_End_In_App, new ValueMap().put("screen_name", screenConfigObject.getTitle())
                        .put("time", Utils.getTimeUTC()));

                if (shouldRecordScreenViews) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        analytics.recordScreen(new ValueMap().put("time_visit", countSecond).put("screen_name", screenConfigObject.getTitle()));
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
                    LogMobio.logD(TAG, "parameter: " + parameter + " value: " + value);
                }
            }
        } catch (Exception e) {
            LogMobio.logE(TAG, e.toString());
        }

        //analytics.track("Deep Link Opened");
    }
}
