package com.mobio.analytics.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.utility.NetworkUtil;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        int status = NetworkUtil.getConnectivityStatusString(context);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                /// TODO: 13/06/2022 handle no network
            } else {
                if(SharedPreferencesUtils.getBool(context, SharedPreferencesUtils.M_KEY_ALLOW_CALL_API)) {
                    MobioSDKClient.getInstance().handleAutoResendWhenReconnect();
                }
            }
        }
    }
}
