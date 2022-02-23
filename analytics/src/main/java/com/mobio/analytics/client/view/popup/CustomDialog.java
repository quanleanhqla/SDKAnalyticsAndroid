package com.mobio.analytics.client.view.popup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobio.analytics.R;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.models.ScreenConfigObject;
import com.mobio.analytics.client.utility.LogMobio;

import java.util.HashMap;

public class CustomDialog extends BaseDialog{
    private NotiResponseObject notiResponseObject;
    private Class des;

    public CustomDialog(Activity activity, NotiResponseObject notiResponseObject, Class des) {
        this.activity = activity;
        this.notiResponseObject = notiResponseObject;
        this.des = des;
    }

    @Override
    void close() {

    }

    @Override
    void cancel() {

    }

    @Override
    void action() {
        if (des != null && !activity.getClass().getSimpleName().equals("LoginActivity")) {
            Intent desIntent = new Intent(activity, des);
            activity.startActivity(desIntent);
        }
    }

    @Override
    void doDismiss() {
        LogMobio.logD("QuanLA", "close popup "+notiResponseObject.getPushId());
    }

    @Override
    String getDetail() {
        return notiResponseObject.getContent();
    }

    @Override
    String getTitle() {
        return notiResponseObject.getTitle();
    }

    @Override
    String getAction() {
        return "Đồng ý";
    }
}
