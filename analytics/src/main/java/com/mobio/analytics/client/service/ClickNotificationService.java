package com.mobio.analytics.client.service;

import static com.mobio.analytics.client.activity.PopupBuilderActivity.M_KEY_PUSH;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

<<<<<<< HEAD
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.activity.PopupBuilderActivity;
=======
import com.google.gson.Gson;
import com.mobio.analytics.client.activity.PopupBuilderActivity;
import com.mobio.analytics.client.model.digienty.Properties;
>>>>>>> 54b8c3df2c3c49a849d06d7e38d9f17cba2587b8
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.view.notification.RichNotification;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ClickNotificationService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_CLICK = "com.mobio.analytics.client.service.action.CLICK";
    public static final String ACTION_CLOSE = "com.mobio.analytics.client.service.action.CLOSE";
    public static final String ACTION_CLICK_LEFT = "com.mobio.analytics.client.service.action.LEFT";
    public static final String ACTION_CLICK_RIGHT = "com.mobio.analytics.client.service.action.RIGHT";
    public static final String ACTION_CLICK_SUBMIT_RATE = "com.mobio.analytics.client.service.action.SUBMIT_RATE";
    public static final String ACTION_CLICK_RATE = "com.mobio.analytics.client.service.action.RATE";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM_DES = "com.mobio.analytics.client.service.extra.DES";
    public static final String EXTRA_PARAM2 = "com.mobio.analytics.client.service.extra.PARAM2";
<<<<<<< HEAD
    public static final String EXTRA_PARAM_ID = "com.mobio.analytics.client.service.extra.ID";
    public static final String EXTRA_PARAM_PUSH = "com.mobio.analytics.client.service.extra.PUSH";
    public static final String EXTRA_PARAM_RATE_POSITION = "com.mobio.analytics.client.service.extra.RATE_POSITION";
=======
    public static final String EXTRA_PARAM3 = "com.mobio.analytics.client.service.extra.PARAM3";
    public static final String EXTRA_PARAM4 = "com.mobio.analytics.client.service.extra.PARAM4";
>>>>>>> 54b8c3df2c3c49a849d06d7e38d9f17cba2587b8

    public ClickNotificationService() {
        super("ClickNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
<<<<<<< HEAD
            if (ACTION_CLICK.equals(action)) {
                final Class<?> des = (Class<?>) intent.getSerializableExtra(EXTRA_PARAM_DES);
                final int id = intent.getIntExtra(EXTRA_PARAM_ID, 0);
                final String strPush = intent.getStringExtra(EXTRA_PARAM_PUSH);
                handleActionClick(des, id, strPush);
            } else if (ACTION_CLOSE.equals(action)) {
                final String strPush = intent.getStringExtra(EXTRA_PARAM_PUSH);
                handleActionClose(strPush);
            }
            else if (ACTION_CLICK_LEFT.equals(action)){
                final String pushStr = intent.getStringExtra(EXTRA_PARAM_PUSH);
                final int id = intent.getIntExtra(EXTRA_PARAM_ID, 0);
                handleActionClickLeft(pushStr, id);
            }
            else if (ACTION_CLICK_RIGHT.equals(action)){
                final String pushStr = intent.getStringExtra(EXTRA_PARAM_PUSH);
                final int id = intent.getIntExtra(EXTRA_PARAM_ID, 0);
                handleActionClickRight(pushStr, id);
            }
            else if (ACTION_CLICK_RATE.equals(action)){
                final String pushStr = intent.getStringExtra(EXTRA_PARAM_PUSH);
                final int id = intent.getIntExtra(EXTRA_PARAM_ID, 0);
                final int positionRate = intent.getIntExtra(EXTRA_PARAM_RATE_POSITION, 0);
                handleActionClickRate(pushStr, id, positionRate);
=======
            if (ACTION_FOO.equals(action)) {
                final Class<?> param1 = (Class<?>) intent.getSerializableExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                final int param3 = intent.getIntExtra(EXTRA_PARAM3, 0);
                final String param4 = intent.getStringExtra(EXTRA_PARAM4);
                handleActionFoo(param1, param2, param3, param4);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
>>>>>>> 54b8c3df2c3c49a849d06d7e38d9f17cba2587b8
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
<<<<<<< HEAD
    private void handleActionClick(Class<?> des,  int reqId, String pushStr) {

        clearAndCloseNotification(reqId);

        // TODO: Handle action click
        Push push = Push.convertJsonStringtoPush(pushStr);
=======
    private void handleActionFoo(Class<?> param1, String param2, int param3, String param4) {
        LogMobio.logD("QuanLA", "click " + param2 + "\nid " + param3);

        // TODO: Handle action Foo
        Push push = Push.convertJsonStringtoPush(param4);
>>>>>>> 54b8c3df2c3c49a849d06d7e38d9f17cba2587b8
        Push.Alert alert = push.getAlert();
        if(alert == null) return;
        String contentType = alert.getContentType();

        Intent i;
        if(contentType.equals(Push.Alert.TYPE_TEXT)) {
<<<<<<< HEAD
            i = new Intent(this, des);
        }
        else {
            i = new Intent(this, PopupBuilderActivity.class);
            i.putExtra(M_KEY_PUSH, pushStr);
=======
            i = new Intent(this, param1);
        }
        else {
            i = new Intent(this, PopupBuilderActivity.class);
            i.putExtra(M_KEY_PUSH, param4);
>>>>>>> 54b8c3df2c3c49a849d06d7e38d9f17cba2587b8
        }
        i.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void clearAndCloseNotification(int id){
        NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(id);

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(it);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionClose(String strPush) {
        // TODO: Handle action Baz
    }

    private void handleActionClickLeft(String pushStr, int id) {
        Push push = Push.convertJsonStringtoPush(pushStr);
        List<String> listImageUrl = ((List<String>) push.getAlert().get("image_url"));
        assert listImageUrl != null;
        listImageUrl.add(0, listImageUrl.get(listImageUrl.size()-1));
        listImageUrl.remove(listImageUrl.size()-1);
        push.getAlert().putValue("image_url", listImageUrl);

        MobioSDKClient.getInstance().showGlobalNotification(push, id);
    }

    private void handleActionClickRight(String pushStr, int id) {
        Push push = Push.convertJsonStringtoPush(pushStr);
        List<String> listImageUrl = ((List<String>) push.getAlert().get("image_url"));
        assert listImageUrl != null;
        listImageUrl.add(listImageUrl.get(0));
        listImageUrl.remove(0);
        push.getAlert().putValue("image_url", listImageUrl);

        MobioSDKClient.getInstance().showGlobalNotification(push, id);
    }

    private void handleActionClickRate(String pushStr, int id, int positionRate) {
        Push push = Push.convertJsonStringtoPush(pushStr);
        push.getAlert().putValue("position_rate", positionRate);
        MobioSDKClient.getInstance().showGlobalNotification(push, id);
    }
}