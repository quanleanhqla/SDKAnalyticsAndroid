package com.mobio.sample;

import static com.mobio.sample.SendMoneyInActivity.ACCOUNT_NAME_TO;
import static com.mobio.sample.SendMoneyInActivity.ACCOUNT_TO;
import static com.mobio.sample.SendMoneyInActivity.CONTENT_TO;
import static com.mobio.sample.SendMoneyInActivity.MONEY_TO;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransferSuccessActivity extends AppCompatActivity {

    private TextView tvMoney;
    private ComboText ctAccountTo;
    private ComboText ctAccountNameTo;
    private ComboText ctContent;
    private ComboText ctTime;
    private Button btnDone;

    private String accountTo;
    private String accountName;
    private String money;
    private String contentTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_transfer_success);
        init();
        getIntentData();
    }

    public void init(){
        btnDone = findViewById(R.id.btn_done);
        tvMoney = findViewById(R.id.tv_money);
        ctAccountTo = findViewById(R.id.ct_to);
        ctAccountNameTo = findViewById(R.id.ct_to_name);
        ctContent = findViewById(R.id.ct_content);
        ctTime = findViewById(R.id.ct_time);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransferSuccessActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    public void getIntentData(){
        accountTo = getIntent().getStringExtra(ACCOUNT_TO);
        accountName = getIntent().getStringExtra(ACCOUNT_NAME_TO);
        money = getIntent().getStringExtra(MONEY_TO);
        contentTo = getIntent().getStringExtra(CONTENT_TO);

        ctAccountTo.setTextContent(accountTo);
        ctAccountNameTo.setTextContent(accountName);
        tvMoney.setText(String.valueOf(money+" VND"));
        ctContent.setTextContent(contentTo);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        ctTime.setTextContent(str);
    }
}