package com.mobio.analytics.client.view.popup;

import android.app.Activity;
import android.content.Intent;

import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.model.old.NotiResponseObject;
import com.mobio.analytics.client.utility.LogMobio;

import java.util.Objects;

public class CustomDialog extends BaseDialog{
    private Push push;
    private Class<?> des;

    public CustomDialog(Activity activity, Push push, Class<?> des) {
        this.activity = activity;
        this.push = push;
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

        LogMobio.logD("QuanLA", "close popup "+ push.getAlert().getPopupId());
    }

    @Override
    String getDetail() {
        return push.getAlert().getContentType().equals(Push.Alert.TYPE_TEXT)
                ? push.getAlert().getBody()
                : push.getAlert().getBodyHTML() ;
    }

    @Override
    String getTitle() {
        return push.getAlert().getTitle();
    }

    @Override
    String getAction() {
        return "Đồng ý";
    }
}