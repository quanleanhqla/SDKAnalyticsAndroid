package com.mobio.analytics.client.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import com.mobio.analytics.R;
import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.models.ValueMap;
import com.mobio.analytics.client.utility.LogMobio;

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

    private Activity activity;
    private NotiResponseObject notiResponseObject;
    private String assetPath;
    private Class des;

    public HtmlController(Activity activity, NotiResponseObject notiResponseObject, String assetPath, Class des) {
        this.activity = activity;
        this.notiResponseObject = notiResponseObject;
        this.assetPath = assetPath;
        this.des = des;
    }

    public void showHtmlView(){
        getWindowRoot(activity).addView(createContainer());
    }

    private View createContainer(){
        RelativeLayout containerLayout = new RelativeLayout(activity);
        containerLayout.setId(VIEW_ID);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        containerLayout.setLayoutParams(layoutParams);
        createWebview(containerLayout, assetPath, notiResponseObject, des);
        return containerLayout;
    }

    private FrameLayout getWindowRoot(Activity activity) {
        return (FrameLayout) activity.getWindow()
                .getDecorView()
                .findViewById(android.R.id.content)
                .getRootView();
    }

    private void createWebview(ViewGroup container, String assetPath, NotiResponseObject notiResponseObject, Class des){
        activity.runOnUiThread(new Runnable() {
            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void run() {
                WebView webView = new WebView(activity);
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

                webView.addJavascriptInterface(new JS_INTERFACE(activity, des), "sdk");
                webView.setWebViewClient (new WebViewClient() {
                    @Override
                    public void onPageFinished (WebView view, String url) {
                        webView.loadUrl ("javascript: window.sdk.getContentWidth (document.getElementsByTagName ('html') [0] .scrollWidth);");
                    }
                });
                if(notiResponseObject.getType() == NotiResponseObject.TYPE_HTML_URL) {
                    webView.loadUrl(notiResponseObject.getContent());
                }
                else if(notiResponseObject.getType() == NotiResponseObject.TYPE_HTML) {
                    webView.loadDataWithBaseURL(assetPath,
                            genDynamicHtml(notiResponseObject.getData()),
                            //notiResponseObject.getData(),
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

    public class JS_INTERFACE {
        private Activity activity;
        private Class dest;
        private ViewGroup root;

        /**
         * Instantiate the interface and set the context
         */
        public JS_INTERFACE(Activity a, Class des) {
            dest = des;
            activity = a;
            root = getWindowRoot(activity);
        }

        @SuppressLint("ResourceType")
        @JavascriptInterface
        public void trackClick() {
            if (dest != null) {
                Intent desIntent = new Intent(activity, dest);
                activity.startActivity(desIntent);
            }
        }

        @JavascriptInterface
        public void identifyUser(String name, String email){
            Analytics.getInstance().identify(new ValueMap().put("name", name).put("email", email));
        }

        @SuppressLint("ResourceType")
        @JavascriptInterface
        public void dismissMessage() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(root != null){
                        root.removeView(root.findViewById(20001));
                    }
                }
            });
        }

        @SuppressLint("ResourceType")
        @JavascriptInterface
        public void getContentWidth (String value) {
            if (value != null) {
                LogMobio.logD ("HtmlController", "Result from javascript:" + Integer.parseInt (value));
            }
        }

        @JavascriptInterface
        public String getDataFromNative(){
            return "Demo data native";
        }

        // Show a toast from the web page
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public int getAndroidVersion() {
            return android.os.Build.VERSION.SDK_INT;
        }

        @SuppressLint("ResourceType")
        @JavascriptInterface
        public void navigateToHome() {
            if(activity.getClass().getSimpleName().equals("LoginActivity")){
                if (dest != null) {
                    Intent desIntent = new Intent(activity, dest);
                    activity.startActivity(desIntent);
                    activity.finish();
                }
            }
        }

        @JavascriptInterface
        public void showAndroidVersion(String versionName) {
            Toast.makeText(activity, versionName, Toast.LENGTH_SHORT).show();
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
