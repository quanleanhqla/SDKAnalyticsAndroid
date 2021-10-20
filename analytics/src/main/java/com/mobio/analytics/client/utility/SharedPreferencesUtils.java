package com.mobio.analytics.client.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private static final String KEY_SP = "keysp";
    public static final String KEY_FIRST_START_APP = "keyfirststartapp";
    public static final String KEY_VERSION_CODE = "keyversioncode";
    public static final String KEY_VERSION_NAME = "keyversionname";
    public static final String KEY_APP_FOREGROUD = "keyappforegroud";
    public static final String KEY_STATE_LOGIN = "key_state_login";
    public static final String KEY_USER_NAME = "key_username";
    public static final String KEY_PASSWORD = "key_password";

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
