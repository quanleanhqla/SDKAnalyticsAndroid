package com.mobio.analytics.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.model.digienty.ValueMap;
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
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                /// TODO: 13/06/2022 handle no network
            } else {
<<<<<<< HEAD
=======
                LogMobio.logD("QuanLA", "Network reciever connect");
>>>>>>> 54b8c3df2c3c49a849d06d7e38d9f17cba2587b8
                ArrayList<Properties> listDataWaitToSend = MobioSDKClient.getInstance().getListFromSharePref(SharedPreferencesUtils.KEY_SEND_QUEUE);
                if (listDataWaitToSend != null && listDataWaitToSend.size() > 0) {
                    for (Properties vm : listDataWaitToSend) {
                        analyticsExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                if(MobioSDKClient.getInstance().sendv2(vm)){
                                    listDataWaitToSend.remove(vm);
                                    MobioSDKClient.getInstance().updateListSharePref(listDataWaitToSend, SharedPreferencesUtils.KEY_SEND_QUEUE);
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}
