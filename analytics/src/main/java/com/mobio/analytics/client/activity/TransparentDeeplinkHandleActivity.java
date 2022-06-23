package com.mobio.analytics.client.activity;

import static com.mobio.analytics.client.activity.PopupBuilderActivity.M_KEY_PUSH;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
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
            if (ClickNotificationService.ACTION_CLICK.equals(action)) {
                final Class<?> des = (Class<?>) i.getSerializableExtra(ClickNotificationService.EXTRA_PARAM_DES);
                final int reqId = i.getIntExtra(ClickNotificationService.EXTRA_PARAM_ID, 0);
                final String pushStr = i.getStringExtra(ClickNotificationService.EXTRA_PARAM_PUSH);
                handleActionClick(des, reqId, pushStr);
            }
        }
    }

    private void handleActionClick(Class<?> des, int reqId, String pushStr) {
        clearAndCloseNotification(reqId);

        Push push = Push.convertJsonStringtoPush(pushStr);
        Push.Alert alert = push.getAlert();
        if(alert == null) return;
        String contentType = alert.getContentType();

        Intent i;
        if(contentType.equals(Push.Alert.TYPE_TEXT)) {
            i = new Intent(this, des);
        }
        else {
            i = new Intent(this, PopupBuilderActivity.class);
            i.putExtra(M_KEY_PUSH, pushStr);
        }
        i.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void clearAndCloseNotification(int id){
        NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(id);

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(it);
    }
}

