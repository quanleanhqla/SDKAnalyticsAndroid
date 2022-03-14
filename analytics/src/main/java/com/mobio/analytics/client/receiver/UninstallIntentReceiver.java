package com.mobio.analytics.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mobio.analytics.client.utility.LogMobio;

public class UninstallIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogMobio.logD("QuanLA","uninstall "+context.getPackageName());
        String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");

        if(packageNames!=null){
            for(String packageName: packageNames){
                if(packageName!=null && packageName.equals(context.getPackageName())){
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
                    LogMobio.logD("QuanLA", "uninstall");

                }
            }
        }
    }
}
