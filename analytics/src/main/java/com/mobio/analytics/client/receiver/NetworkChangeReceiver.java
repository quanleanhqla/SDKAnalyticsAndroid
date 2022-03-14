package com.mobio.analytics.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.models.ValueMap;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.NetworkUtil;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        ExecutorService analyticsExecutor = Executors.newSingleThreadExecutor();
        int status = NetworkUtil.getConnectivityStatusString(context);
        LogMobio.logD("QuanLA", "Network reciever");
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                LogMobio.logD("QuanLA", "Network reciever not connect");
            } else {
                LogMobio.logD("QuanLA", "Network reciever connect");
                ArrayList<ValueMap> listDataWaitToSend = MobioSDKClient.getInstance().getListFromSharePref(SharedPreferencesUtils.KEY_EVENT_QUEUE);
                if (listDataWaitToSend != null && listDataWaitToSend.size() > 0) {
                    for (ValueMap vm : listDataWaitToSend) {
                        analyticsExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                if(MobioSDKClient.getInstance().sendSync(vm)){
                                    listDataWaitToSend.remove(vm);
                                    MobioSDKClient.getInstance().updateListSharePref(listDataWaitToSend, SharedPreferencesUtils.KEY_EVENT_QUEUE);
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}
