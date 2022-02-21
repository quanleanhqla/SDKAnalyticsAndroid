package com.mobio.analytics.client.utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;

public class SharedPreferencesUtils {
    private static final String KEY_SP = "keysp";
    public static final String KEY_FIRST_START_APP = "keyfirststartapp";
    public static final String KEY_VERSION_CODE = "keyversioncode";
    public static final String KEY_VERSION_NAME = "keyversionname";
    public static final String KEY_APP_FOREGROUD = "keyappforegroud";
    public static final String KEY_STATE_LOGIN = "key_state_login";
    public static final String KEY_USER_NAME = "key_username";
    public static final String KEY_PASSWORD = "key_password";
    public static final String KEY_API_TOKEN = "key_api_token";
    public static final String KEY_MERCHANT_ID = "key_merchant_id";
    public static final String KEY_DEVICE_TOKEN = "key_device_token";
    public static final String KEY_BASE_URL = "key_base_url";
    public static final String KEY_ENDPOINT = "key_endpoint";
    public static final String KEY_EVENT = "key_event";
    public static final String KEY_PUSH = "key_push";
    public static final String KEY_PENDING_PUSH = "key_pending_push";
    public static final String KEY_EVENT_QUEUE = "key_event_queue";

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(KEY_SP, Context.MODE_PRIVATE);
    }

    public static void editString(Context context, String key, String value){
        getSharedPreferences(context).edit().putString(key, value).apply();
    }

    public static void editInt(Context context, String key, int value){
        getSharedPreferences(context).edit().putInt(key, value).apply();
    }

    public static void editBool(Context context, String key, boolean value){
        getSharedPreferences(context).edit().putBoolean(key, value).apply();
    }

    public static String getString(Context context, String key){
        return getSharedPreferences(context).getString(key, null);
    }

    public static int getInt(Context context, String key){
        return getSharedPreferences(context).getInt(key, -1);
    }

    public static boolean getBool(Context context, String key){
        return getSharedPreferences(context).getBoolean(key, false);
    }
}
