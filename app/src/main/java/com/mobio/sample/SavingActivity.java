package com.mobio.sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.mobio.analytics.client.receiver.NotificationDismissedReceiver;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;

public class SavingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSave;
    private Button btnLogout;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving);
        initView();
    }

    public void initView(){
        btnLogout = findViewById(R.id.btn_logout);
        btnSave = findViewById(R.id.btn_save);
        handler = new Handler();

        btnSave.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

        handler.postDelayed(timeOutRunable, 10000);
    }

    private Runnable timeOutRunable = new Runnable() {
        @Override
        public void run() {
            if(com.mobio.analytics.client.utility.SharedPreferencesUtils.getBool(SavingActivity.this,
                    com.mobio.analytics.client.utility.SharedPreferencesUtils.KEY_APP_FOREGROUD)){
            }
            else {
                int reqCode = 1;
                Intent intent = new Intent(getApplicationContext(), SavingActivity.class);
                showNotification(SavingActivity.this, "SDKMobio", "Send money to bank now", intent, reqCode);
            }
        }
    };



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogMobio.logD("SavingActivity", "abc");
        //Analytics.getInstance().track(Analytics.DEMO_EVENT, Analytics.TYPE_CLICK,"Click Open from noti");
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDeleteIntent(createOnDismissedIntent(this, reqCode))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    public void save(){
        if(handler != null){
            if(timeOutRunable != null) {
                handler.removeCallbacks(timeOutRunable);
            }
        }

    }

    public void logout(){
        if(handler != null){
            if(timeOutRunable != null) {
                handler.removeCallbacks(timeOutRunable);
            }
        }
        SharedPreferencesUtils.editString(this, SharedPreferencesUtils.KEY_USER_NAME, null);
        SharedPreferencesUtils.editString(this, SharedPreferencesUtils.KEY_PASSWORD, null);
        SharedPreferencesUtils.editBool(this, SharedPreferencesUtils.KEY_STATE_LOGIN, false);
        startActivity(new Intent(SavingActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null){
            if(timeOutRunable != null) {
                handler.removeCallbacks(timeOutRunable);
            }
        }
    }
}