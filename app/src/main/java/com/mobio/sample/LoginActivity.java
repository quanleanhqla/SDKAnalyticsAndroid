package com.mobio.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.mobio.analytics.client.MobioSDKClient;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.utility.GpsTracker;
import com.mobio.analytics.client.utility.LogMobio;
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
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
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

                LogMobio.logD("IGA", "Address" + add);
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
            LogMobio.logD("LoginActivity", e.toString());
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
            LogMobio.logD("LoginActivity", "error");
        }
    }

    private void initView() {
        btnLogin = findViewById(R.id.btn_login);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        tvLong = findViewById(R.id.tv_long);
        tvLat = findViewById(R.id.tv_lat);
        tvAddress = findViewById(R.id.tv_address);


//        try {
//            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

                LogMobio.logD("LoginActivity", "userName" + userName);
                LogMobio.logD("LoginActivity", "password" + password);

                if (!TextUtils.isEmpty(userName) &&
                        !TextUtils.isEmpty(password) &&
                        isEmailValid(userName)) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                        Toast.makeText(LoginActivity.this, "Mail or password is null", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid mail", Toast.LENGTH_SHORT).show();
                    }
                }

                MobioSDKClient.getInstance().identify();
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