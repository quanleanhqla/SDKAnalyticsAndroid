package com.mobio.analytics.client.model.factory;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.gson.Gson;
import com.mobio.analytics.client.activity.TransparentDeeplinkHandleActivity;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.service.ClickNotificationService;
import com.mobio.analytics.client.utility.Utils;

public class PendingIntentFactory {

    public static PendingIntent getPushClickPendingIntent(Context context, Push push, int reqId, Class<?> des){
        Intent intent = new Intent();
        intent.setAction(ClickNotificationService.ACTION_CLICK);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_PUSH, new Gson().toJson(push));
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_DES, des);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_ID, reqId);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            intent.setComponent(new ComponentName(context, TransparentDeeplinkHandleActivity.class));
            return PendingIntent.getActivity(context, reqId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else {
            intent.setComponent(new ComponentName(context, ClickNotificationService.class));
            return PendingIntent.getService(context, reqId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    public static PendingIntent getPushDeletePendingIntent(Context context, Push push, int reqId){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, ClickNotificationService.class));
        intent.setAction(ClickNotificationService.ACTION_CLOSE);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_PUSH, new Gson().toJson(push));

        return PendingIntent.getService(context, reqId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPushActionPendingIntent(Context context, Push push, int reqId, String type){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, ClickNotificationService.class));
        intent.setAction(ClickNotificationService.ACTION_CLICK_INPUT);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_INPUT, type);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_PUSH, new Gson().toJson(push));

        return PendingIntent.getService(context, reqId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPushClickLeftPendingIntent(Context context, Push push, int reqId){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, ClickNotificationService.class));
        intent.setAction(ClickNotificationService.ACTION_CLICK_LEFT);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_PUSH, new Gson().toJson(push));
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_ID, reqId);

        return PendingIntent.getService(context, reqId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPushClickRightPendingIntent(Context context, Push push, int reqId){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, ClickNotificationService.class));
        intent.setAction(ClickNotificationService.ACTION_CLICK_RIGHT);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_PUSH, new Gson().toJson(push));
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_ID, reqId);

        return PendingIntent.getService(context, reqId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPushClickRatePendingIntent(Context context, Push push, int reqId, int position){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, ClickNotificationService.class));
        intent.setAction(ClickNotificationService.ACTION_CLICK_RATE);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_PUSH, new Gson().toJson(push));
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_ID, reqId);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_RATE_POSITION, position);

        return PendingIntent.getService(context, reqId+position, intent, PendingIntent.FLAG_IMMUTABLE |PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getPushRatingSubmitPendingIntent(Context context, Push push, int reqId, int ratePosition) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, ClickNotificationService.class));
        intent.setAction(ClickNotificationService.ACTION_CLICK_SUBMIT_RATE);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_PUSH, new Gson().toJson(push));
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_ID, reqId);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_RATE_POSITION, ratePosition);

        return PendingIntent.getService(context, reqId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
