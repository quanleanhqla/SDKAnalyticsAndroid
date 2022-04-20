package com.mobio.analytics.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
        }
        else {
            if(intent.getAction().equals("ACTION_LAUNCH_NOTI")) {
                String strPendingPush = SharedPreferencesUtils.getString(context, SharedPreferencesUtils.KEY_PENDING_PUSH);
                Properties tempP = Properties.convertJsonStringtoProperties(strPendingPush);
                List<Properties> listPendingNoti = tempP.getList("key_pending_push", Properties.class);
                if(listPendingNoti == null || listPendingNoti.size() == 0) return;
                tempP = listPendingNoti.get(0);
                Properties notiPp = tempP.getValueMap("noti_response", Properties.class);
                if(notiPp == null) return;
                MobioSDKClient.getInstance().showPushInApp(notiPp);

                listPendingNoti.remove(0);
                Properties pendingPush = new Properties().putValue("key_pending_push", listPendingNoti);
                String jsonEvent = new Gson().toJson(pendingPush, new TypeToken<Properties>() {
                }.getType());
                SharedPreferencesUtils.editString(context, SharedPreferencesUtils.KEY_PENDING_PUSH, jsonEvent);

            }
        }
    }
}
