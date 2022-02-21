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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.mobio.analytics.client.models.ValueMap;
import com.mobio.analytics.client.models.ViewDimension;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class Utils {
    /** Returns the referrer who started the Activity. */
    public static Uri getReferrer(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return activity.getReferrer();
        }
        return getReferrerCompatible(activity);
    }

    public static boolean isHtml(String content){
        // adapted from post by Phil Haack and modified to match better
        String tagStart=
                "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)\\>";
        String tagEnd=
                "\\</\\w+\\>";
        String tagSelfClosing=
                "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)/\\>";
        String htmlEntity=
                "&[a-zA-Z][a-zA-Z0-9]+;";
        Pattern htmlPattern=Pattern.compile(
                "("+tagStart+".*"+tagEnd+")|("+tagSelfClosing+")|("+htmlEntity+")",
                Pattern.DOTALL
        );

        boolean ret = false;
        if(content != null){
            ret = htmlPattern.matcher(content).find();
        }
        return ret;
    }

    public static final String HTML_RAW = "<!doctype html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <title>Lunar -  Free Bootstrap Modal and Popups  </title>\n" +
            "    <!-- Required meta tags -->\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=0\">\n" +
            "    <!-- Bootstrap CSS -->\n" +
            "    <link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/bootstrap/css/bootstrap.min.css\">\n" +
            "    <!-- Lunar CSS -->\n" +
            "    <link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/css/lunar.css\">\n" +
            "    <!--<link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/css/demo.css\">-->\n" +
            "    <!-- Fonts -->\n" +
            "    <link rel=\"stylesheet\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/css/animate.min.css\">\n" +
            "    <link href=\"https://fonts.googleapis.com/css?family=Work+Sans:600\" rel=\"stylesheet\">\n" +
            "    <link href=\"https://fonts.googleapis.com/css?family=Overpass:300,400,600,700,800,900\" rel=\"stylesheet\">\n" +
            "    <link rel=\"icon\" type=\"image/x-icon\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/img/lunar.png\"/>\n" +
            "    <link rel=\"icon\" href=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/img/lunar.png\" type=\"image/png\" sizes=\"16x16\">\n" +
            "</head>\n" +
            "<style>\n" +
            "        .modal.fade.modal-bottom-right .modal-dialog {\n" +
            "            width: 100%;\n" +
            "            position: absolute;\n" +
            "            transform: translate(-50%, -50%);\n" +
            "            top: 50%;\n" +
            "            left: 48%;\n" +
            "            right: auto;\n" +
            "            bottom: auto;\n" +
            "        }\n" +
            "    </style>\n" +
            "<body class=\"modal-open\">\n" +
            "    <!-- Modal -->\n" +
            "    <div class=\"modal fade modal-bottom-right show\" id=\"demoModal\"  tabindex=\"-1\" role=\"dialog\"\n" +
            "         aria-labelledby=\"demoModal\" aria-hidden=\"true\" style=\"display: block;\">\n" +
            "\n" +
            "        <div class=\"modal-dialog  modal-sm\" role=\"document\">\n" +
            "\n" +
            "            <div class=\"modal-content\">\n" +
            "                <button type=\"button\" class=\"close size-sm light\" data-dismiss=\"modal\"\n" +
            "                        aria-label=\"Close\" onclick=\"sdk.dismissMessage();\">\n" +
            "                    <span aria-hidden=\"true\">&times;</span>\n" +
            "                </button>\n" +
            "                <div class=\"modal-body bg-rhino px-sm-3 py-sm-3\" >\n" +
            "                    <div class=\"text-center pb-2\"><img src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/img/megaphone.png\" alt=\"\"></div>\n" +
            "                    <h3 class=\"text-white text-center \">CTKM</h3>\n" +
            "                    <p class=\"text-white-50\">CTKM Thanh toán điện thoại Viettel được tặng 10% chỉ có tại Mobio Bank trong hôm nay!</p>\n" +
            "                    <div class=\"pt-2 text-center\">\n" +
            "                        <a class=\"btn btn-cstm-light \" data-dismiss=\"modal\" aria-label=\"Close\" onclick=\"sdk.trackClick(); \">Đồng ý</a>\n" +
            "                    </div>\n" +
            "\n" +
            "                </div>\n" +
            "\n" +
            "\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "    <!-- Modal Ends -->\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "<!--end content here-->\n" +
            "<div id=\"image\"></div>\n" +
            "<script src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/js/jquery.min.js\"></script>\n" +
            "<script src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/js/popper.min.js\"></script>\n" +
            "<script src=\"https://campaign-assets-01.moengage.com/inbound/inapp/html_inapp/campaigns/DemoAccount-E-commerce/163606189309_omg365/163723101375_70xwq4/assets/bootstrap/js/bootstrap.min.js\"></script>\n" +
            "<div class=\"modal-backdrop show\"></div>\n" +
            "</body>\n" +
            "</html>";

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

    private static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static boolean compareTwoJson(String first, String second) {
        try {
            JSONObject jsonObject = new JSONObject(first);
            JSONObject jsonObject1 = new JSONObject(second);
            Iterator<String> s = jsonObject.keys();
            for (Iterator<String> it = s; it.hasNext(); ) {
                String str = it.next();
                System.out.println("key:" + str + " : value1:" + jsonObject.get(str) + ":value2:" + jsonObject1.get(str));
                //compare value of json1 with json2
                if (!jsonObject.get(str).equals(jsonObject1.get(str))) {
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ValueMap toMap(JSONObject object) throws JSONException {
        ValueMap map = new ValueMap();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
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

    public static long getTimeInterval(long max, long min, int size){
        long diff = max - min;
        return diff / size + min;
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

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
