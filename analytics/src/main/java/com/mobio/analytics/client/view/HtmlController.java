package com.mobio.analytics.client.view;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import com.google.gson.Gson;
import com.mobio.analytics.R;
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.digienty.Event;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.model.digienty.ValueMap;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlController {
    private final String FILE_URI_SCHEME_PREFIX = "file://";
    private final String FILE_PATH_SEPARATOR = "/";
    private final String HTML_MIME_TYPE = "text/html";
    private final String HTML_ENCODING = "utf-8";
    private final int VIEW_ID = 20001;

    private final String templateHtml = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=0\">\n" +
            "    <style>\n" +
            "                html {\n" +
            "                    height: 100%;\n" +
            "                }\n" +
            "                body {\n" +
            "                    min-height: 100%;\n" +
            "                    position: relative;\n" +
            "                    background: rgba(0,0,0,.2);\n" +
            "                }\n" +
            "                #m_modal {\n" +
            "                    width: 100%;\n" +
            "                    position: absolute;\n" +
            "                    transform: translate(-50%, -50%);\n" +
            "                    top: 50%;\n" +
            "                    left: 50%;\n" +
            "                    right: auto;\n" +
            "                    bottom: auto;\n" +
            "                    text-align: center;\n" +
            "                    border-radius: 10px;\n" +
            "                    background: #fff;\n" +
            "                }\n" +
            "                @media (min-width: 576px) {\n" +
            "                    #m_modal {\n" +
            "                    max-width: 300px;\n" +
            "                    }\n" +
            "                }\n" +
            "\n" +
            "                @media (min-width: 576px) {\n" +
            "                    #m_modal {\n" +
            "                    max-width: 500px;\n" +
            "                    margin: 1.75rem auto;\n" +
            "                    }\n" +
            "                }\n" +
            "\n" +
            "                #m_modal img {\n" +
            "\t                width: -webkit-fill-available !important;\n" +
            "                }\n" +
            "\n" +
            "\n" +
            "    </style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "    <div id=\"m_modal\">\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
    private static final String keyWordSubstr = "<div id=\"m_modal\">";
    private static final int ID_OF_PROGRESSBAR = 336699;

    private Activity activity;
    private Push push;
    private String assetPath;
    private WebView webView;

    public HtmlController(Activity activity, Push push, String assetPath) {
        this.activity = activity;
        this.push = push;
        this.assetPath = assetPath;
    }

    public void showHtmlView() {
        getWindowRoot(activity).addView(createContainer());
    }

    private View createContainer() {
        RelativeLayout containerLayout = new RelativeLayout(activity);
        containerLayout.setId(VIEW_ID);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        containerLayout.setLayoutParams(layoutParams);
        createWebview(containerLayout, assetPath, push);
        return containerLayout;
    }

    private FrameLayout getWindowRoot(Activity activity) {
        return (FrameLayout) activity.getWindow()
                .getDecorView()
                .findViewById(android.R.id.content)
                .getRootView();
    }

    private void createButtonClose(ViewGroup container){
        ImageView imageView = new ImageView(activity);
        imageView.setImageResource(R.drawable.ic_circle_xmark_solid);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissMessage();
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.setMargins(20, 100, 20, 20);
        container.addView(imageView, params);
    }

    private void showLoading(ViewGroup container){
        ProgressBar progressBar = new ProgressBar(activity);
        progressBar.setId(ID_OF_PROGRESSBAR);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#A6ACAF"), android.graphics.PorterDuff.Mode.MULTIPLY);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        container.addView(progressBar, params);
    }

    private void hideLoading(ViewGroup container){
        container.removeView(container.findViewById(ID_OF_PROGRESSBAR));
    }

    private void createWebview(ViewGroup container, String assetPath, Push push) {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint({"SetJavaScriptEnabled", "ResourceType"})
            @Override
            public void run() {
                webView = new WebView(activity);
                webView.setId(ViewCompat.generateViewId());
                webView.setFocusableInTouchMode(true);
                webView.setBackgroundColor(Color.parseColor("#80000000"));
//                webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                webView.setVisibility(View.GONE);

                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setUseWideViewPort(true);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setDisplayZoomControls(false);
                webSettings.setDomStorageEnabled(true);

                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.setAcceptThirdPartyCookies(webView, true);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    webSettings.setMediaPlaybackRequiresUserGesture(false);
                }
                webSettings.setAllowFileAccess(true);

                webView.addJavascriptInterface(new JS_INTERFACE(activity), "sdk");
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        LogMobio.logD("QuanLA", "onPageStarted");
                        view.loadUrl("javascript:(function() {" +
                                "window.parent.addEventListener ('message', function(event) {" +
                                " sdk.receiveMessage(JSON.stringify(event.data));});" +
                                "})();");
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        LogMobio.logD("QuanLA", "onPageFinished");
                        Utils.hideKeyboard(activity);
                        webView.setVisibility(View.VISIBLE);
                        webView.requestFocus();
                    }
                });

                Push.Data data = push.getData();
                Push.Alert alert = push.getAlert();
                if (alert == null) return;

                String content_type = alert.getContentType();
                if (content_type.equals(Push.Alert.TYPE_POPUP)) {
                    if(data == null) return;
                    String popupUrl = data.getPopupUrl();
                    if (popupUrl != null) webView.loadUrl(popupUrl);
                } else if (content_type.equals(Push.Alert.TYPE_HTML)) {
                    webView.loadDataWithBaseURL(assetPath,
                            genDynamicHtml(alert.getBodyHTML()),
                            //alert.getBodyHTML(),
                            HTML_MIME_TYPE,
                            HTML_ENCODING, null);
                }

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                webView.setLayoutParams(layoutParams);
                webView.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_BACK:
                                    dismissMessage();
                                    return true;
                            }
                        }
                        return false;
                    }
                });
                webView.setDownloadListener(new DownloadListener() {
                    @Override
                    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                        download(url, userAgent, contentDisposition, mimetype, contentLength);
                    }
                });

                webView.setVisibility(View.GONE);
                container.addView(webView);
                if (content_type.equals(Push.Alert.TYPE_HTML)){
                    createButtonClose(container);
                }
            }
        });
    }

    private void download(String url, String userAgent, String contentDisposition, String mimetype, long contentLength){
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        request.setMimeType(mimetype);
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription("Downloading File...");
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                        url, contentDisposition, mimetype));
        DownloadManager dm = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(request);

        LogMobio.logD("QuanLA", "downloading");
    }

    private void dismissMessage() {
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                FrameLayout root = getWindowRoot(activity);
                if (root != null) {
                    root.removeView(root.findViewById(20001));
                }
            }
        });
    }

    public class JS_INTERFACE {
        private Activity activity;

        /**
         * Instantiate the interface and set the context
         */
        public JS_INTERFACE(Activity a) {
            activity = a;
        }

        @SuppressLint("ResourceType")
        @JavascriptInterface
        public void trackClick() {
            //todo
        }

        @JavascriptInterface
        public void receiveMessage(String data) {
            if (data != null) {
                LogMobio.logD("QuanLA", "data " + data);
                processReceivedMessage(data);
            }
        }

        @JavascriptInterface
        public void identifyUser(String name, String email) {
            //todo
        }

        @SuppressLint("ResourceType")
        @JavascriptInterface
        public void dismissMessage() {
            HtmlController.this.dismissMessage();
        }

        @SuppressLint("ResourceType")
        @JavascriptInterface
        public void getContentWidth(String value) {
            if (value != null) {
                LogMobio.logD("HtmlController", "Result from javascript:" + Integer.parseInt(value));
            }
        }

        @JavascriptInterface
        public String getDataFromNative() {
            return "Demo data native";
        }

        // Show a toast from the web page
        @JavascriptInterface
        public void showToast() {
            LogMobio.logD("QuanLA", "abc");
            Toast.makeText(activity, "toast", Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public int getAndroidVersion() {
            return android.os.Build.VERSION.SDK_INT;
        }

        @SuppressLint("ResourceType")
        @JavascriptInterface
        public void navigateToHome() {
            //todo
        }

        @JavascriptInterface
        public void showAndroidVersion(String versionName) {
            Toast.makeText(activity, versionName, Toast.LENGTH_SHORT).show();
        }
    }

    private void processReceivedMessage(String data) {
        Properties dataVM = Properties.convertJsonStringtoProperties(data);
        String message = (String) dataVM.get("message");
        if (message != null) {
            if (message.equals("MO_CLOSE_BUTTON_CLICK")) {
                dismissMessage();
                return;
            }

            if (message.equals("MO_BUTTON_CLICK")) {
                processDynamicEvents(dataVM);
                return;
            }
            if (message.equals("MO_POPUP_LOADED")) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            webView.evaluateJavascript("handleReplacePersonalization('" + getProfileInfoToWebview(push) + "');", null);
                            webView.evaluateJavascript("showPopup('cc');", null);
                        } else {
                            webView.loadUrl("javascript:handleReplacePersonalization('" + getProfileInfoToWebview(push) + "');");
                            webView.loadUrl("javascript:showPopup('cc');");
                        }
                    }
                });
            }
        }
    }

    private String getProfileInfoToWebview(Push push) {
        Push.Data data = push.getData();
        if (data != null) {
            Properties profileInfo = data.getValueMap("profile_info", Properties.class);
            if (profileInfo != null) {
                return new Gson().toJson(profileInfo);
            }
        }
        return "";
    }

    private void processDynamicEvents(Properties valueMap) {
        long actionTime = 0;
        List<Properties> listEvents = valueMap.getList("events", Properties.class);
        ArrayList<Event> listEvent = new ArrayList<>();
        if (listEvents != null && listEvents.size() > 0) {
            for (int i = 0; i < listEvents.size(); i++) {
                Properties tempEvent = listEvents.get(i);

                if (tempEvent == null) continue;

                String eventKey = (String) tempEvent.get("eventKey");
                Properties eventData = (Properties) tempEvent.get("eventData");
                boolean includedReport = tempEvent.getBoolean("includedReport", false);

                if (eventData != null) {
                    List<Properties> fields = eventData.getList("fields", Properties.class);
                    if (fields != null && fields.size() > 0) {
                        actionTime = (long) fields.get(0).getLong("value", 0);
                    }
                }

                ArrayList<Event.Dynamic> listDynamic = new ArrayList<>();
                listDynamic.add(new Event.Dynamic().putEventKey(eventKey).putEventData(
                        new Properties().putValue("action_time", actionTime)).putValue("includedReport", includedReport));
                Event eventDynamic = new Event().putSource("popup_builder")
                        .putType("dynamic").putActionTime(123456789)
                        .putDynamic(listDynamic);
                listEvent.add(eventDynamic);
            }
            MobioSDKClient.getInstance().track(listEvent);
        }
    }

    public String genDynamicHtml(String receiveHtml){
        Pattern word = Pattern.compile(keyWordSubstr);
        Matcher match = word.matcher(templateHtml);
        String html=templateHtml;
        int endPos = 0;
        while (match.find()) {
            endPos = match.end();
            LogMobio.logD("Found love at index ",html.substring(0, endPos)+ receiveHtml +html.substring(endPos));
        }
        return html.substring(0, endPos)+ receiveHtml +html.substring(endPos);
    }

}
