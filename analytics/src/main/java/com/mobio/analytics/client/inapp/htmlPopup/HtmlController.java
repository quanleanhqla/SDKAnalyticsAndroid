package com.mobio.analytics.client.inapp.htmlPopup;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.core.view.ViewCompat;

import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.utility.Utils;

public class HtmlController{
    private final int VIEW_ID = 20001;
    private static final int ID_OF_PROGRESSBAR = 336699;
    public static final String M_KEY_PUSH = "m_key_push";
    public static final String POSITION_CENTER = "cc";

    private Activity activity;
    private Push push;
    private String assetPath;
    private WebView webView;
    private String position;

    public HtmlController(Activity activity, Push push, String assetPath) {
        this.activity = activity;
        this.push = push;
        this.assetPath = assetPath;
        if (push.getData() != null) {
            this.position = push.getData().getString("position");
            if(position == null) position = POSITION_CENTER;
        }
    }

    public static void showHtmlPopup(Activity activity, Push push, String assetPath) {
        new HtmlController(activity, push, assetPath).showHtmlView();
    }

    private void showHtmlView() {
        ViewGroup root = getWindowRoot(activity);
        if (root.findViewById(VIEW_ID) == null) {
            getWindowRoot(activity).addView(createContainer());
        }
    }

    private View createContainer() {
        RelativeLayout containerLayout = new RelativeLayout(activity);
        containerLayout.setId(VIEW_ID);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if(Utils.hasNavBar(activity.getWindowManager()) && Utils.isNavAtBottom(activity)) {
            layoutParams.setMargins(0, Utils.getHeightOfStatusBar(activity), 0, Utils.getHeightOfNavigationBar(activity));
        }
        else {
            layoutParams.setMargins(0, Utils.getHeightOfStatusBar(activity), 0, 0);
        }
        containerLayout.setLayoutParams(layoutParams);
        containerLayout.setBackgroundColor(Color.TRANSPARENT);
        if(!position.equals(POSITION_CENTER)) containerLayout.setClickable(false);
        createWebview(containerLayout);
        new WebviewController(activity, containerLayout, push).createWebview(assetPath, webView);
        return containerLayout;
    }

    private FrameLayout getWindowRoot(Activity activity) {
        return (FrameLayout) activity.getWindow()
                .getDecorView()
                .findViewById(android.R.id.content)
                .getRootView();
    }

    private void showLoading(ViewGroup container) {
        ProgressBar progressBar = new ProgressBar(activity);
        progressBar.setId(ID_OF_PROGRESSBAR);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#A6ACAF"), android.graphics.PorterDuff.Mode.MULTIPLY);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        container.addView(progressBar, params);
    }

    private void hideLoading(ViewGroup container) {
        container.removeView(container.findViewById(ID_OF_PROGRESSBAR));
    }

    private void createWebview(ViewGroup container){
        webView = new WebView(activity);
        webView.setId(ViewCompat.generateViewId());
        RelativeLayout.LayoutParams layoutParams;
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        webView.setLayoutParams(layoutParams);

        container.addView(webView);
    }
}
