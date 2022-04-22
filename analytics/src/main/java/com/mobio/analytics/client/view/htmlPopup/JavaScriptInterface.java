package com.mobio.analytics.client.view.htmlPopup;

import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
    private OnActionJavascript onActionJavascript;

    public JavaScriptInterface(OnActionJavascript onActionJavascript) {
        this.onActionJavascript = onActionJavascript;
    }

    @JavascriptInterface
    public void receiveMessage(String data) {
        if (data != null) {
            onActionJavascript.onReceiveMessage(data);
        }
    }

    @JavascriptInterface
    public void dismissMessage() {
        onActionJavascript.onDismissMessage();
    }

    interface OnActionJavascript {
        void onReceiveMessage(String data);
        void onDismissMessage();
    }
}
