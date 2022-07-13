package com.mobio.analytics.client.inapp.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.mobio.analytics.R;
import com.mobio.analytics.client.activity.TransparentDeeplinkHandleActivity;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.model.old.ScreenConfigObject;
import com.mobio.analytics.client.receiver.NotificationDismissedReceiver;
import com.mobio.analytics.client.service.ClickNotificationService;

import java.util.HashMap;

public class GlobalNotification {
    private int id;
    private Push push;
    private HashMap<String, ScreenConfigObject> configActivityMap;
    private Context context;
    private Class classDes;
    private NotificationManager notificationManager;

    public GlobalNotification(int id, Push push, HashMap<String, ScreenConfigObject> configActivityMap, Context context) {
        this.id = id;
        this.push = push;
        this.configActivityMap = configActivityMap;
        this.context = context;
        this.classDes = findDes();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void showGlobalNotification(int id, Push push, HashMap<String, ScreenConfigObject> configActivityMap, Context context){
        new GlobalNotification(id, push, configActivityMap, context).show();
    }

    private Intent createIntent(){
        Intent intent = new Intent();
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setComponent(new ComponentName(context, ClickNotificationService.class));
        intent.setAction(ClickNotificationService.ACTION_CLICK);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_DES, classDes);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_ID, id);
        intent.putExtra(ClickNotificationService.EXTRA_PARAM_PUSH, new Gson().toJson(push));
        return intent;
    }

    private Class<?> findDes(){
        Class<?> classDes = null;
        Class<?> classInitial = null;
        for (int i = 0; i < configActivityMap.values().size(); i++) {
            ScreenConfigObject screenConfigObject = (ScreenConfigObject) configActivityMap.values().toArray()[i];
            if (screenConfigObject.getTitle().equals(push.getAlert().getDesScreen())) {
                classDes = screenConfigObject.getClassName();
                break;
            }
            if (screenConfigObject.isInitialScreen()) {
                classInitial = screenConfigObject.getClassName();
            }
        }
        if (classDes == null) {
            classDes = classInitial;
        }
        return classDes;
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId, boolean isDelete) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("type_delete", isDelete);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, PendingIntent.FLAG_IMMUTABLE);
        return pendingIntent;
    }

    private void show(){
        Intent intent = createIntent();
        String CHANNEL_ID = "Channel Analytics";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.icon))
                .setContentTitle(push.getAlert().getTitle())
                .setContentText(push.getAlert().getBody())
                .setDeleteIntent(createOnDismissedIntent(context, id, false))
                //.setContentIntent(classDes != null ? PendingIntent.getActivity(application.getApplicationContext(), REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE) : null)
                .setContentIntent(PendingIntent.getService(context, id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT))
                .setGroup("Analytics")
                .setAutoCancel(true);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R){
            intent.setComponent(new ComponentName(context, TransparentDeeplinkHandleActivity.class));
            notificationBuilder.setContentIntent(classDes != null ? PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_IMMUTABLE) : null);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(id, notification); // 0 is the request code, it should be unique id

//        track(SDK_Mobile_Test_Open_Notification_App,
//                new ValueMap().put("action_time", Utils.getTimeUTC())
//        .put("push_id", notiResponseObject.getPushId())
//        .put("device", "Android"));

    }

}
