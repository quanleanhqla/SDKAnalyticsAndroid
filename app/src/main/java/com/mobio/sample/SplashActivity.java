package com.mobio.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.mobio.analytics.client.utility.SharedPreferencesUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_splash);
        Intent i = new Intent(SplashActivity.this, LoginActivity.class);

        Intent finalI = i;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(finalI);
                finish();
            }
        }, 1000);
    }
}