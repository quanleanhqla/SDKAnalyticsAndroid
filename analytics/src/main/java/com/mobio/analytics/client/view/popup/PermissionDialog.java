package com.mobio.analytics.client.view.popup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class PermissionDialog extends BaseDialog{

    public PermissionDialog(Activity activity) {
        this.activity = activity;
    }

    @Override
    String getDetail() {
        return "Quý khách vui lòng cấp quyền thông báo";
    }

    @Override
    String getTitle() {
        return "Cấp quyền thông báo!";
    }

    @Override
    String getAction() {
        return "Đồng ý";
    }

    @Override
    void close() {

    }

    @Override
    void cancel() {

    }

    @Override
    void action() {
        addPermissionNoti();
    }

    @Override
    void doDismiss() {

    }

    private void addPermissionNoti() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", activity.getPackageName());
            intent.putExtra("app_uid", activity.getApplicationInfo().uid);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
        }
        activity.startActivity(intent);
    }
}
