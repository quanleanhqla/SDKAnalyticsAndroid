package com.mobio.analytics.client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.mobio.analytics.R;
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.ModelFactory;
import com.mobio.analytics.client.model.digienty.Event;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.Utils;
import com.mobio.analytics.client.view.htmlPopup.HtmlController;
import com.mobio.analytics.client.view.htmlPopup.JavaScriptInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PopupBuilderActivity extends AppCompatActivity {
    private final String HTML_MIME_TYPE = "text/html";
    private final String HTML_ENCODING = "utf-8";
    public static final String M_KEY_PUSH = "m_key_push";
    private WebView webView;
    private ProgressBar progressBar;
    private RelativeLayout rlRoot;
    private Push push;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_builder);

        webView = findViewById(R.id.web);
        progressBar = findViewById(R.id.progress);
        rlRoot = findViewById(R.id.rl_root);

        push = getDataPush();
        if (push != null) {
            createWebview("", push);
        }
    }

    public Push getDataPush() {
        if (getIntent() != null) {
            String pushStr = getIntent().getStringExtra(M_KEY_PUSH);

            return Push.convertJsonStringtoPush(pushStr);
        }
        return null;
    }

    private void createWebview(String assetPath, Push push) {
        webView.setFocusableInTouchMode(true);
        webView.setVerticalScrollBarEnabled(true);
        rlRoot.setBackgroundColor(Color.parseColor("#80000000"));
        webView.setBackgroundColor(Color.TRANSPARENT);

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

        webView.addJavascriptInterface(new JavaScriptInterface(new JavaScriptInterface.OnActionJavascript() {
            @Override
            public void onReceiveMessage(String data) {
                processReceivedMessage(data);
                LogMobio.logD("QuanLA", "data " + data);
            }

            @Override
            public void onDismissMessage() {
                dismissMessage();
            }

            @Override
            public void onReceiveHeight(String height) {
                LogMobio.logD("QuanLA", "height " + height);
            }
        }), "sdk");


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                LogMobio.logD("QuanLA", "onPageStarted");
                view.loadUrl("javascript:(function() {" +
                        "window.parent.addEventListener ('message', function(event) {" +
                        " sdk.receiveMessage(JSON.stringify(event.data));});" +
                        "})();");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                LogMobio.logD("QuanLA", "onPageFinished");
                Utils.hideKeyboard(PopupBuilderActivity.this);
                webView.requestFocus();
            }
        });

        Push.Data data = push.getData();
        Push.Alert alert = push.getAlert();
        if (alert == null){
            dismissMessage();
            return;
        }

        String content_type = alert.getContentType();
        if (content_type.equals(Push.Alert.TYPE_POPUP)) {
            String popupUrl = data.getPopupUrl();
            if (popupUrl != null){
                webView.loadUrl(popupUrl);
            }
        } else if (content_type.equals(Push.Alert.TYPE_HTML)) {
            webView.loadDataWithBaseURL(assetPath,
                    genDynamicHtml(alert.getBodyHTML()),
                    //alert.getBodyHTML(),
                    HTML_MIME_TYPE,
                    HTML_ENCODING, null);
        }
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
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
                if (Utils.hasWritePermissions(PopupBuilderActivity.this)) {
                    download(url, userAgent, contentDisposition, mimetype, contentLength);
                }
            }
        });
        if (content_type.equals(Push.Alert.TYPE_HTML)) {
            createButtonClose();
        }
    }

    private void createButtonClose() {
        ImageView imageView = new ImageView(this);
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
        rlRoot.addView(imageView, params);
    }


    private void download(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
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
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(request);

        LogMobio.logD("QuanLA", "downloading");
    }

    private void dismissMessage() {
        finish();
    }

    private void processReceivedMessage(String data) {
        Properties dataVM = Properties.convertJsonStringtoProperties(data);
        String message = dataVM.getString("message");
        if (message != null) {
            if (message.equals("MO_CLOSE_BUTTON_CLICK")) {
                if (dataVM.getString("popupId") != null && dataVM.getInt("page", 2)==1) {
                    long actionTime = System.currentTimeMillis();
                    MobioSDKClient.getInstance().track(ModelFactory.createBaseListForPopup(push, "popup", "close", actionTime), actionTime);
                }
                dismissMessage();
                return;
            }

            if (message.equals("MO_BUTTON_CLICK")) {
                processClickButton(dataVM);
                return;
            }

            if (message.equals("MO_SUBMIT_BUTTON_CLICK")) {
                processSubmitForm(dataVM);
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

                        long actionTime = System.currentTimeMillis();
                        MobioSDKClient.getInstance().track(ModelFactory.createBaseListForPopup(push, "popup", "open", actionTime), actionTime);
                    }
                });
            }
        }
    }

    private void processSubmitForm(Properties dataVM) {
        if (dataVM == null) return;

        long actionTime = System.currentTimeMillis();

        Properties formData = dataVM.getValueMap("formData", Properties.class);
        String id = formData.getString("id");
        Properties field = formData.getValueMap("fields", Properties.class);

        Properties tags = dataVM.getValueMap("tags", Properties.class);
        String buttonId = dataVM.getString("buttonId");
        boolean hasSecondPage = dataVM.getBoolean("hasSecondPage", false);
        int includedReport = dataVM.getInt("includedReport", 0);

        Properties value = createValueForBase("submit", buttonId, field, tags);
        Event.Base base = ModelFactory.createBase("button", value);
        Event event = new Event().putBase(base).putSource("popup_builder")
                .putType("submit")
                .putIncludedReport(includedReport)
                .putActionTime(actionTime);

        ArrayList<Event> events = new ArrayList<>();
        events.add(event);

        MobioSDKClient.getInstance().track(events, actionTime);

        if (!hasSecondPage) {
            dismissMessage();
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

    private void processClickButton(Properties valueMap) {
        long actionTime = 0;
        List<Properties> listEvents = valueMap.getList("events", Properties.class);
        Properties tags = valueMap.getValueMap("tags", Properties.class);
        int includedReport = valueMap.getInt("includedReport", 0);
        boolean hasSecondPage = valueMap.getBoolean("hasSecondPage", false);
        String id = valueMap.getString("id");
        String name = valueMap.getString("name");

        long action_time = System.currentTimeMillis();

        ArrayList<Event> listEvent = new ArrayList<>();
        if (listEvents != null && listEvents.size() > 0) {
            for (int i = 0; i < listEvents.size(); i++) {
                Properties tempEvent = listEvents.get(i);

                if (tempEvent == null) continue;

                String eventKey = tempEvent.getString("eventKey");
                Properties eventData = tempEvent.getValueMap("eventData", Properties.class);

                if (eventData != null) {
                    List<Properties> fields = eventData.getList("fields", Properties.class);
                    if (fields != null && fields.size() > 0) {
                        actionTime = fields.get(0).getLong("value", 0);
                    }
                }

                ArrayList<Event.Dynamic> listDynamic = new ArrayList<>();
                listDynamic.add(new Event.Dynamic().putEventKey(eventKey).putEventData(
                        new Properties().putValue("action_time", actionTime)));

                Event eventDynamic = new Event().putSource("popup_builder")
                        .putType("dynamic")
                        .putActionTime(action_time)
                        .putDynamic(listDynamic);
                listEvent.add(eventDynamic);
            }
        }

        if (includedReport == 1) {
            Properties value = createValueForBase("click", id, null, tags);
            Event.Base base = ModelFactory.createBase("button", value);
            Event event = new Event().putBase(base).putSource("popup_builder")
                    .putType("click")
                    .putActionTime(System.currentTimeMillis())
                    .putIncludedReport(includedReport);
            listEvent.add(event);
        }
        MobioSDKClient.getInstance().track(listEvent, action_time);
        if (!hasSecondPage) dismissMessage();
    }

    private Properties createValueForBase(String type, String id, Properties field, Properties tags) {
        Properties value = new Properties()
                .putValue("button", new Properties().putValue("type", type).putValue("id", id))
                .putValue("tags", tags)
                .putValue("journey", ModelFactory.getJourney(push));
        if (field != null) value.putValue("input_fields", field);

        return value;
    }

    public String genDynamicHtml(String receiveHtml) {
        Pattern word = Pattern.compile(keyWordSubstr);
        Matcher match = word.matcher(templateHtml);
        String html = templateHtml;
        int endPos = 0;
        while (match.find()) {
            endPos = match.end();
            LogMobio.logD("Found love at index ", html.substring(0, endPos) + receiveHtml + html.substring(endPos));
        }
        return html.substring(0, endPos) + receiveHtml + html.substring(endPos);
    }
}