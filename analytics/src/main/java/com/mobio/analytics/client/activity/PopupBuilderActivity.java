package com.mobio.analytics.client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobio.analytics.R;
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.factory.ModelFactory;
import com.mobio.analytics.client.model.digienty.Event;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.Utils;
import com.mobio.analytics.client.view.htmlPopup.JavaScriptInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PopupBuilderActivity extends AppCompatActivity {
    private final String HTML_MIME_TYPE = "text/html";
    private final String HTML_ENCODING = "utf-8";
    private static final int ID_OF_URL_BAR = 112233;
    private static final int ID_OF_URL_TEXTVIEW = 332211;
    private static final int ID_OF_URL_LINE = 222211;
    public static final String M_KEY_PUSH = "m_key_push";
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
    private static final String POSITION_CENTER = "cc";
    private static final String POSITION_TOP = "tc";
    private static final String POSITION_BOTTOM = "bc";


    private WebView webView;
    private ProgressBar progressBar;
    private RelativeLayout rlRoot;
    private Push push;
    private String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_builder);

        webView = findViewById(R.id.web);
        progressBar = findViewById(R.id.progress);
        rlRoot = findViewById(R.id.rl_root);

        push = getDataPush();
        if (push != null) {
            if (push.getData() != null) {
                this.position = push.getData().getString("position");
                if(position == null) position = POSITION_CENTER;
            }
            createWebview("", push);
        }

//        if (getDataPush() != null) {
//            HtmlController.showHtmlPopup(this, getDataPush(), "", true);
//        }
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
        rlRoot.setBackgroundColor(Color.TRANSPARENT);
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
            }

            @Override
            public void onDismissMessage() {
                dismissMessage();
            }

        }), "sdk");


        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = Uri.parse(url);
                return handleUri(uri);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                return handleUri(uri);
            }

            private boolean handleUri(final Uri uri) {
                if (uri.toString().startsWith("http://") || uri.toString().startsWith("https://")) {
                    webView.post(new Runnable() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void run() {
                            if(findViewById(ID_OF_URL_BAR) == null) {
                                RelativeLayout actionBar = createActionBar(rlRoot, uri.toString());
                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT);
                                layoutParams.addRule(RelativeLayout.BELOW, actionBar.getId());
                                webView.setLayoutParams(layoutParams);
                                createButtonClose(actionBar);
                            }
                            else {
                                if(findViewById(ID_OF_URL_TEXTVIEW) instanceof TextView){
                                    ((TextView) findViewById(ID_OF_URL_TEXTVIEW)).setText(uri.toString());
                                }
                            }
                        }
                    });
                    return false;
                } else {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    dismissMessage();
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                view.loadUrl("javascript:(function() {" +
                        "window.parent.addEventListener ('message', function(event) {" +
                        " sdk.receiveMessage(JSON.stringify(event.data));});" +
                        "})();");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
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
            createButtonClose(rlRoot);
        }
    }

    private void createButtonClose(ViewGroup container) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissMessage();
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        params.setMargins(20, 20, 20, 20);
        container.addView(imageView, params);
    }

    @SuppressLint("ResourceType")
    private RelativeLayout createActionBar(ViewGroup container, String url) {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setId(ID_OF_URL_BAR);
        relativeLayout.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Utils.getHeightOfScreen(this) / 14);
        rlParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        relativeLayout.setLayoutParams(rlParams);

        TextView tvUrl = new TextView(this);
        tvUrl.setId(ID_OF_URL_TEXTVIEW);
        tvUrl.setTextColor(Color.BLACK);
        tvUrl.setText(url);
        tvUrl.setSingleLine(true);
        tvUrl.setEllipsize(TextUtils.TruncateAt.END);
        tvUrl.setTypeface(Typeface.DEFAULT_BOLD);
        tvUrl.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(
                400, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        tvUrl.setLayoutParams(tvParams);
        relativeLayout.addView(tvUrl);

        View view = new View(this);
        view.setId(ID_OF_URL_LINE);
        view.setBackgroundColor(Color.BLACK);
        RelativeLayout.LayoutParams vParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 3);
        vParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        view.setLayoutParams(vParams);
        relativeLayout.addView(view);

        container.addView(relativeLayout);
        return relativeLayout;
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
                            webView.evaluateJavascript("showPopup({popup_position:'cc'});", null);
                        } else {
                            webView.loadUrl("javascript:handleReplacePersonalization('" + getProfileInfoToWebview(push) + "');");
                            webView.loadUrl("javascript:showPopup({popup_position:'cc'});");
                        }

                        long actionTime = System.currentTimeMillis();
                        MobioSDKClient.getInstance().track(ModelFactory.createBaseListForPopup(push, "popup", "open", actionTime), actionTime);

                        Properties size = dataVM.getValueMap("size", Properties.class);
                        if (size != null) {

                            webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(position.equals(POSITION_CENTER)) return;

                                    int widthMobile = size.getInt("widthMobile", 0);
                                    int heightMobile = size.getInt("heightMobile", 0);
                                    RelativeLayout.LayoutParams layoutParams;

                                    if (heightMobile < Utils.dpFromPx(PopupBuilderActivity.this, Utils.getHeightOfScreen(PopupBuilderActivity.this))) {

                                        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                (int) Utils.pxFromDp(PopupBuilderActivity.this, heightMobile));
                                        if (position.equals(POSITION_TOP)) {
                                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                                        } else if (position.equals(POSITION_BOTTOM)) {
                                            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                                        }
                                        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                        webView.setLayoutParams(layoutParams);
                                    }
                                }
                            });
                        }
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
        }
        return html.substring(0, endPos) + receiveHtml + html.substring(endPos);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}