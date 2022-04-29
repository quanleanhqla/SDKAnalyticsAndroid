package com.mobio.analytics.client.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mobio.analytics.R;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.view.htmlPopup.HtmlController;

public class PopupBuilderActivity extends AppCompatActivity {
    public static final String M_KEY_PUSH = "m_key_push";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_builder);

        if(getDataPush() != null){
            new HtmlController(this, getDataPush(), "", true).showHtmlView();
        }
    }

    public Push getDataPush(){
        if(getIntent() != null){
            String pushStr = getIntent().getStringExtra(M_KEY_PUSH);

            return Push.convertJsonStringtoPush(pushStr);
        }
        return null;
    }
}