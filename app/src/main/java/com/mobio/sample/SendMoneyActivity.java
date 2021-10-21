package com.mobio.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SendMoneyActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout llIn;
    private ImageView imvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_send_money);
        init();
    }

    public void init(){
        llIn = findViewById(R.id.ll_in);
        imvBack = findViewById(R.id.imv_back);

        imvBack.setOnClickListener(this);

        llIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imv_back:
                finish();
                break;
            case R.id.ll_in:
                startActivity(new Intent(SendMoneyActivity.this, SendMoneyInActivity.class));
                break;
        }
    }
}