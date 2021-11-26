package com.mobio.analytics.client.utility;

import static org.json.JSONObject.wrap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

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
import com.mobio.analytics.client.models.ViewDimension;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    public static ViewDimension getScreenDimension(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return new ViewDimension(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public static boolean areNotificationsEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (!manager.areNotificationsEnabled()) {
                return false;
            }
            List<NotificationChannel> channels = manager.getNotificationChannels();
            for (NotificationChannel channel : channels) {
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    return false;
                }
            }
            return true;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
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

    @SuppressLint("HardwareIds")
    public static String getIMEIDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    deviceId = mTelephony.getImei();
                }else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        LogMobio.logD("deviceId", deviceId);
        return deviceId;
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

    public static TraitsObject getTraitsObject(){
        return new TraitsObject();
    }

    public static String getTimeUTC(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return Instant.now().toString();
//        }
//        else {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            return sdf.format(new Date());
//        }
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

    public static String getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses != null && addresses.size() > 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                address = add;
                LogMobio.logD("IGA", "Address" + add);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogMobio.logD("LoginActivity", e.toString());
        }
        return address;
    }

    public static ContextObject getContextObject(Application application){
        return new ContextObject.Builder().withApp(getAppObject(application))
                .withDevice(getDeviceObject())
                .withOs(getOsObject())
                .withTimezone(getTimeZone())
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
