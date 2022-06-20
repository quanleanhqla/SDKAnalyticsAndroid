package com.mobio.sample;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobio.analytics.client.utility.SharedPreferencesUtils;

public class HomeActivity extends AppCompatActivity {
    private CombineView cvSendMoney;
    private CombineView cvAccount;
    private boolean isShowMoney;
    private ImageView imvShowMoney;
    private TextView tvMoney;
    private TextView tvDid;
    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCustomUI();
        setContentView(R.layout.activity_home);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("MissingPermission")
    public void init(){
        tvMoney = findViewById(R.id.tv_balance);
        imvShowMoney = findViewById(R.id.imv_show_balance);
        cvSendMoney = findViewById(R.id.cv_send_money);
        cvAccount = findViewById(R.id.cv_account);
        tvDid = findViewById(R.id.tv_did);
        tvName = findViewById(R.id.tv_name);
        isShowMoney = true;

        cvSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SendMoneyActivity.class));
            }
        });

        cvAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Analytics.getInstance().track(Analytics.DEMO_EVENT, Analytics.TYPE_CLICK,"Click Yes on Popup");
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

        tvDid.setText(SharedPreferencesUtils.getString(this, SharedPreferencesUtils.M_KEY_D_ID));
        tvName.setText(SharedPreferencesUtils.getString(this, SharedPreferencesUtils.M_KEY_USER_NAME));
    }

    private void showCustomUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}