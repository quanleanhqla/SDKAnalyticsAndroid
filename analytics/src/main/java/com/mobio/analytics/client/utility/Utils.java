package com.mobio.analytics.client.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.mobio.analytics.BuildConfig;
import com.mobio.analytics.client.models.AppObject;
import com.mobio.analytics.client.models.ContextObject;
import com.mobio.analytics.client.models.CreateDeviceObject;
import com.mobio.analytics.client.models.DeviceObject;
import com.mobio.analytics.client.models.OsObject;
import com.mobio.analytics.client.models.ProfileBaseObject;
import com.mobio.analytics.client.models.ProfileInfoObject;
import com.mobio.analytics.client.models.PropertiesObject;
import com.mobio.analytics.client.models.TraitsObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Utils {
    /** Returns the referrer who started the Activity. */
    public static Uri getReferrer(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return activity.getReferrer();
        }
        return getReferrerCompatible(activity);
    }

    /** Returns the referrer on devices running SDK versions lower than 22. */
    private static Uri getReferrerCompatible(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Intent intent = activity.getIntent();
            Uri referrerUri = intent.getParcelableExtra(Intent.EXTRA_REFERRER);
            if (referrerUri != null) {
                return referrerUri;
            }
            // Intent.EXTRA_REFERRER_NAME
            String referrer = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
            if (referrer != null) {
                // Try parsing the referrer URL; if it's invalid, return null
                try {
                    return Uri.parse(referrer);
                } catch (android.net.ParseException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static DeviceObject getDeviceObject(){
        LogMobio.logD("Utils", "Name "+Build.DEVICE+
                "\nType "+Build.BRAND+
                "\nDisplay "+Build.DISPLAY+
                "\nId "+Build.ID+
                "\nManufacturer "+Build.MANUFACTURER+
                "\nModel "+Build.MODEL);
        return new DeviceObject.Builder()
                .withName(Build.DEVICE)
                .withManufacturer(Build.MANUFACTURER)
                .withId(Build.ID)
                .withType(Build.BRAND)
                .withModel(Build.MODEL)
                .withDeviceId(Build.ID)
                .build();
    }

    public static AppObject getAppObject(Application application){
        String namespace = application.getPackageName();
        String name = "";
        String versionName = "";
        int versionCode=0;
        PackageManager packageManager = application.getPackageManager();
        try {
            name =(String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(namespace, 0));
            versionName = packageManager.getPackageInfo(namespace, 0).versionName;
            versionCode = packageManager.getPackageInfo(namespace, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        LogMobio.logD("Utils", "Namespace "+ namespace +
                "\nBuild "+versionCode+
                "\nVersion "+versionName);

        return new AppObject(namespace, name,
                String.valueOf(versionCode), versionName);
    }

    public static String getTimeZone(){
        LogMobio.logD("Utils", "Timezone "+ TimeZone.getDefault().getDisplayName());
        return TimeZone.getDefault().getDisplayName();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static TraitsObject getTraitsObject(){
        return new TraitsObject();
    }

    public static String getTimeUTC(){
        return Instant.now().toString();
    }

    public static OsObject getOsObject(){
        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                LogMobio.logD("Utils", "Name "+ fieldName+
                        "\nVersion "+Build.VERSION.RELEASE);
                return new OsObject(fieldName, String.valueOf(Build.VERSION.RELEASE));
            }
        }
        return new OsObject(String.valueOf(Build.VERSION.SDK_INT), String.valueOf(Build.VERSION.RELEASE));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ContextObject getContextObject(Application application){
        return new ContextObject.Builder().withApp(getAppObject(application))
                .withDevice(getDeviceObject())
                .withOs(getOsObject())
                .withTimezone(getTimeZone())
                .withTraits(getTraitsObject())
                .build();
    }

    public static ProfileBaseObject getProfileInfoObject(){
        return (ProfileBaseObject) new ProfileInfoObject.Builder()
                .build();
    }

    public static ProfileBaseObject getProfileCreateDeviceObject(Application application){
        String email = SharedPreferencesUtils.getString(application.getApplicationContext(), SharedPreferencesUtils.KEY_USER_NAME);
        if(email==null || TextUtils.isEmpty(email)){
            return (ProfileBaseObject) new CreateDeviceObject(application.getApplicationContext(), "APP");
        }
        else {
            return (ProfileBaseObject) new ProfileInfoObject.Builder()
                    .withEmail(email)
                    .withContext(application.getApplicationContext())
                    .build();
        }
    }

    public static PropertiesObject getProperties(){
        return new PropertiesObject(BuildConfig.VERSION_NAME, String.valueOf(BuildConfig.VERSION_CODE));
    }

    public static Map<String, String> getHeader(Context context){
        //String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjdmYzBhMzNjLWJhZjUtMTFlNy1hN2MyLTAyNDJhYzE4MDAwMyIsInVzZXJuYW1lIjoiYWRtaW5AcGluZ2NvbXNob3AiLCJmdWxsbmFtZSI6Ik5ndXlcdTFlYzVuIFZcdTAxMDNuIEEiLCJwaG9uZV9udW1iZXIiOiIrODQzMjM0NTY3ODkiLCJlbWFpbCI6InRoYWl0dEBtb2Jpby52biIsIm1lcmNoYW50X2lkIjoiMWI5OWJkY2YtZDU4Mi00ZjQ5LTk3MTUtMWI2MWRmZmYzOTI0IiwiaXNfYWRtaW4iOjEsImlzX21vYmlvIjoyLCJhdmF0YXIiOiJodHRwczovL3QxLm1vYmlvLnZuL3N0YXRpYy8xYjk5YmRjZi1kNTgyLTRmNDktOTcxNS0xYjYxZGZmZjM5MjQvZWMwYTEwZWUtMjg3NC00NGUzLTgwMzQtZmE4OWYyODczZGMyLmJpbiIsImlhdCI6MTYzNDYxMTkyMC45ODQxNzg1LCJpc19zdWJfYnJhbmQiOmZhbHNlLCJ1c2VfY2FsbGNlbnRlciI6MywibWVyY2hhbnRfbmFtZSI6IlBpbmdjb21TaG9wIiwibWVyY2hhbnRfYXZhdGFyIjoiaHR0cHM6Ly90MS5tb2Jpby52bi9zdGF0aWMvMWI5OWJkY2YtZDU4Mi00ZjQ5LTk3MTUtMWI2MWRmZmYzOTI0LzFlNDhhYmM3LTUyNzctNGYxYy1hZjU5LTA3ZThlZDQwMmU0Ny5qcGciLCJtZXJjaGFudF90eXBlIjoxLCJ4cG9pbnRfc3RhdHVzIjozLCJyb2xlX2dyb3VwIjoib3duZXIiLCJtZXJjaGFudF9jb2RlIjoiUElOR0NPTVNIT1AiLCJ0eXBlIjpbXSwiZXhwIjoxNjM0Njk4MzIxLjA2NDk0N30.eACtwpF7GPCE4O2V9n8SzA0FPToUwngbe1g92lDzm2Y";
        String token = SharedPreferencesUtils.getString(context, SharedPreferencesUtils.KEY_API_TOKEN);
        //String merchantID = "1b99bdcf-d582-4f49-9715-1b61dfff3924";
        String merchantID = SharedPreferencesUtils.getString(context, SharedPreferencesUtils.KEY_MERCHANT_ID);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        header.put("X-Merchant-Id", merchantID);
        header.put("User-Agent", "analytics-android "+BuildConfig.VERSION_NAME);
        return header;
    }


}
