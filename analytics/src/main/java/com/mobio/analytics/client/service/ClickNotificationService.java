package com.mobio.analytics.client.service;

import android.app.IntentService;
import android.content.Intent;

import com.google.gson.Gson;
import com.mobio.analytics.client.utility.LogMobio;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ClickNotificationService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "com.mobio.analytics.client.service.action.FOO";
    public static final String ACTION_BAZ = "com.mobio.analytics.client.service.action.BAZ";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.mobio.analytics.client.service.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.mobio.analytics.client.service.extra.PARAM2";
    public static final String EXTRA_PARAM3 = "com.mobio.analytics.client.service.extra.PARAM3";

    public ClickNotificationService() {
        super("ClickNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final Class param1 = (Class) intent.getSerializableExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                final int param3 = intent.getIntExtra(EXTRA_PARAM3, 0);
                handleActionFoo(param1, param2, param3);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(Class param1, String param2, int param3) {
        // TODO: Handle action Foo
        LogMobio.logD("QuanLA", "click "+param2+"\nid "+param3);
        Intent i = new Intent(this, param1);
        i.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}