package com.mobio.analytics.client.activity;

import static com.mobio.analytics.client.activity.PopupBuilderActivity.M_KEY_PUSH;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.service.ClickNotificationService;
import com.mobio.analytics.client.utility.LogMobio;

public class TransparentDeeplinkHandleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if(i != null) {
            final String action = i.getAction();
            if (ClickNotificationService.ACTION_FOO.equals(action)) {
                final Class<?> param1 = (Class<?>) i.getSerializableExtra(ClickNotificationService.EXTRA_PARAM1);
                final String param2 = i.getStringExtra(ClickNotificationService.EXTRA_PARAM2);
                final int param3 = i.getIntExtra(ClickNotificationService.EXTRA_PARAM3, 0);
                final String param4 = i.getStringExtra(ClickNotificationService.EXTRA_PARAM4);
                handleActionFoo(param1, param2, param3, param4);
            }
        }
    }

    private void handleActionFoo(Class<?> param1, String param2, int param3, String param4) {
        // TODO: Handle action Foo
        LogMobio.logD("QuanLA", "click " + param2 + "\nid " + param3);

        // TODO: Handle action Foo
        Push push = Push.convertJsonStringtoPush(param4);
        Push.Alert alert = push.getAlert();
        if(alert == null) return;
        String contentType = alert.getContentType();

        Intent i;
        if(contentType.equals(Push.Alert.TYPE_TEXT)) {
            i = new Intent(this, param1);
        }
        else {
            i = new Intent(this, PopupBuilderActivity.class);
            i.putExtra(M_KEY_PUSH, param4);
        }
        i.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}

