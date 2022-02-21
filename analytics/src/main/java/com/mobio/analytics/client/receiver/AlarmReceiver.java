package com.mobio.analytics.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.models.ValueMap;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

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
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(strPendingPush);
                    ValueMap vm = Utils.toMap(jsonObject);
                    if (vm.get("key_pending_push") != null) {
                        List<ValueMap> listPendingNoti = (List<ValueMap>) vm.get("key_pending_push");
                        if(listPendingNoti == null || listPendingNoti.size() == 0) return;
                        vm = listPendingNoti.get(0);
                        ValueMap notiVm = (ValueMap) vm.get("noti_response");
                        if(notiVm == null) return;
                        String notiStr = new Gson().toJson(notiVm);
                        String pushId = (String) vm.get("node_id");
                        NotiResponseObject notiResponseObject = new Gson().fromJson(notiStr, NotiResponseObject.class);
                        notiResponseObject.setPushId(pushId);
                        if (SharedPreferencesUtils.getBool(context, SharedPreferencesUtils.KEY_APP_FOREGROUD)) {
                            Analytics.getInstance().showGlobalPopup(notiResponseObject);
                        } else {
                            Analytics.getInstance().showGlobalNotification(notiResponseObject, (int) (Math.random() * 10000));
                        }
                        listPendingNoti.remove(0);
                        ValueMap pendingPush = new ValueMap().put("key_pending_push", listPendingNoti);
                        String jsonEvent = new Gson().toJson(pendingPush, new TypeToken<ValueMap>() {
                        }.getType());
                        SharedPreferencesUtils.editString(context, SharedPreferencesUtils.KEY_PENDING_PUSH, jsonEvent);
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
