package com.mobio.analytics.client.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import com.mobio.analytics.R;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.inapp.htmlPopup.WebviewController;


public class PopupBuilderActivity extends AppCompatActivity {
    public static final String M_KEY_PUSH = "m_key_push";

    private WebView webView;
    private RelativeLayout rlRoot;
    private Push push;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_builder);

        webView = findViewById(R.id.web);
        rlRoot = findViewById(R.id.rl_root);

        push = getDataPush();
        if (push != null) {
            new WebviewController(this, rlRoot, push).createWebview("", webView);
        }
//        initDialogHtml();
    }

//    private void initDialogHtml(){
//        dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.popup_webview);
//
//        webView = dialog.findViewById(R.id.wv_popup);
//        rlRoot = dialog.findViewById(R.id.rl_root);
//
//        push = getDataPush();
//        if (push != null) {
//            new WebviewController(this, rlRoot, push).createWebview("", webView);
//        }
//
//        dialog.show();
//    }

    public Push getDataPush() {
        if (getIntent() != null) {
            String pushStr = getIntent().getStringExtra(M_KEY_PUSH);

            return Push.convertJsonStringtoPush(pushStr);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        if(dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}