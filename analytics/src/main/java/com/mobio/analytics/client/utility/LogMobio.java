package com.mobio.analytics.client.utility;

import android.util.Log;

public class LogMobio {
    private final static boolean shouldLog = true;

    public static void logV(String tag, String content){
        if(shouldLog) {
            Log.v(tag, content);
        }
    }

    public static void logD(String tag, String content){
        if(shouldLog) {
            Log.d(tag, content);
        }
    }

    public static void logE(String tag, String content){
        if(shouldLog) {
            Log.e(tag, content);
        }
    }

    public static void logI(String tag, String content){
        if(shouldLog) {
            Log.i(tag, content);
        }
    }

    public static void logW(String tag, String content){
        if(shouldLog) {
            Log.w(tag, content);
        }
    }

    public static void logWTF(String tag, String content){
        if(shouldLog) {
            Log.wtf(tag, content);
        }
    }

}
