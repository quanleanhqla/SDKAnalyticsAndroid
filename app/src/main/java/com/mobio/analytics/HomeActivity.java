package com.mobio.analytics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mobio.analytics.client.utility.LogMobio;

public class HomeActivity extends AppCompatActivity {
    private CombineView cvSendMoney;
    private boolean isShowMoney;
    private ImageView imvShowMoney;
    private TextView tvMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCustomUI();
        setContentView(R.layout.activity_home);
        init();
    }

    public void init(){
        tvMoney = findViewById(R.id.tv_balance);
        imvShowMoney = findViewById(R.id.imv_show_balance);
        cvSendMoney = findViewById(R.id.cv_send_money);
        isShowMoney = true;

        cvSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SendMoneyActivity.class));
            }
        });

        imvShowMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowMoney){
                    imvShowMoney.setImageResource(R.drawable.ic_show_gray);
                    tvMoney.setText("*** VNĐ");
                }
                else {
                    imvShowMoney.setImageResource(R.drawable.ic_hide_gray);
                    tvMoney.setText("30.000.000 VNĐ");
                }
                isShowMoney = !isShowMoney;
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            LogMobio.logD("HomeActivity", "Fetching FCM registration token failed" + task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        LogMobio.logD("HomeActivity", token);

                    }
                });
    }

    private void showCustomUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}