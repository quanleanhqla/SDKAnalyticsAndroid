package com.mobio.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.utility.GpsTracker;
import com.mobio.analytics.client.utility.LogMobio;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1;
    private Button btnLogin;
    private EditText etUsername;
    private EditText etPassword;
    private GpsTracker gpsTracker;

    private TextView tvLong;
    private TextView tvLat;
    private TextView tvAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCustomUI();
        setContentView(R.layout.activity_login);
        initView();
        addListener();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogMobio.logD("QuanLA", "orientation "+newConfig.orientation);
    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getPostalCode();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + obj.getSubThoroughfare();

                String finalAdd = add;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvAddress.setText(finalAdd);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getLocation() {
        gpsTracker = new GpsTracker(LoginActivity.this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvLat.setText(new StringBuilder().append(latitude).append(" ").append(Utils.getIMEIDeviceId(LoginActivity.this)).toString());
                    tvLong.setText(String.valueOf(longitude));
                }
            });
            getAddress(latitude, longitude);

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private void initView() {
        btnLogin = findViewById(R.id.btn_login);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvLong = findViewById(R.id.tv_long);
        tvLat = findViewById(R.id.tv_lat);
        tvAddress = findViewById(R.id.tv_address);
    }

    private void showCustomUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void addListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = etUsername.getText().toString();
                String password = etPassword.getText().toString();

//                showPopup(etPassword);

                if (!TextUtils.isEmpty(userName) &&
                        !TextUtils.isEmpty(password) &&
                        isEmailValid(userName)) {
                    SharedPreferencesUtils.editString(LoginActivity.this, SharedPreferencesUtils.M_KEY_USER_NAME, userName);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                        Toast.makeText(LoginActivity.this, "Mail or password is null", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid mail", Toast.LENGTH_SHORT).show();
                    }
                }

//                MobioSDKClient.getInstance().identify();
            }
        });
    }

    public void showPopup(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_webview, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        WebView webView = popupView.findViewById(R.id.wv_popup);
        webView.loadUrl("https://mobio.io/");

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}