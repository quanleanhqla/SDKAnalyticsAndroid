package com.mobio.analytics.client.service;

import static com.mobio.analytics.client.activity.PopupBuilderActivity.M_KEY_PUSH;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.activity.PopupBuilderActivity;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.inapp.notification.RichNotification;

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
    public static final String EXTRA_PARAM_ID = "com.mobio.analytics.client.service.extra.ID";
    public static final String EXTRA_PARAM_PUSH = "com.mobio.analytics.client.service.extra.PUSH";
    public static final String EXTRA_PARAM_RATE_POSITION = "com.mobio.analytics.client.service.extra.RATE_POSITION";

    public ClickNotificationService() {
        super("ClickNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
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
            }
        }
    }

    private void handleActionClick(Class<?> des,  int reqId, String pushStr) {

        clearAndCloseNotification(reqId);

        // TODO: Handle action click
        Push push = Push.convertJsonStringtoPush(pushStr);
        Push.Alert alert = push.getAlert();
        if(alert == null) return;
        String contentType = alert.getContentType();

        Intent i;
        if(contentType.equals(Push.Alert.TYPE_TEXT)) {
            i = new Intent(this, des);
        }
        else {
            i = new Intent(this, PopupBuilderActivity.class);
            i.putExtra(M_KEY_PUSH, pushStr);
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

    private void handleActionClose(String strPush) {
        // TODO: Handle action Close
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