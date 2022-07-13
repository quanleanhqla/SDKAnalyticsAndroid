package com.mobio.analytics.client.inapp.nativePopup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.digienty.Notification;

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
    String getImage() {
        return null;
    }

    @Override
    void close() {
        denyPermission();
    }

    @Override
    void cancel() {
        denyPermission();
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

    private void denyPermission(){
        String permisson = MobioSDKClient.getInstance().getCurrentNotiPermissionInValue();
        if(permisson == null || !permisson.equals(Notification.KEY_DENIED)) {
            MobioSDKClient.getInstance().updateNotificationPermission(Notification.KEY_DENIED);
        }
    }
}
