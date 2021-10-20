package com.mobio.analytics;

import static com.mobio.analytics.SendMoneyInActivity.ACCOUNT_NAME_TO;
import static com.mobio.analytics.SendMoneyInActivity.ACCOUNT_TO;
import static com.mobio.analytics.SendMoneyInActivity.CONTENT_TO;
import static com.mobio.analytics.SendMoneyInActivity.MONEY_TO;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ConfirmTransferActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imvBack;
    private Button btnConfirm;
    private String accountTo;
    private String accountName;
    private String money;
    private String contentTo;
    private ComboText ctAccountTo;
    private ComboText ctAccountNameTo;
    private ComboText ctMoney;
    private ComboText ctContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_confirm_transfer);
        init();
        getIntentData();
    }

    public void init(){
        imvBack = findViewById(R.id.imv_back);
        btnConfirm = findViewById(R.id.btn_confirm);
        ctAccountTo = findViewById(R.id.ct_to);
        ctAccountNameTo = findViewById(R.id.ct_to_name);
        ctMoney = findViewById(R.id.ct_money);
        ctContent = findViewById(R.id.ct_content);

        imvBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    public void getIntentData(){
        accountTo = getIntent().getStringExtra(ACCOUNT_TO);
        accountName = getIntent().getStringExtra(ACCOUNT_NAME_TO);
        money = getIntent().getStringExtra(MONEY_TO);
        contentTo = getIntent().getStringExtra(CONTENT_TO);

        ctAccountTo.setTextContent(accountTo);
        ctAccountNameTo.setTextContent(accountName);
        ctMoney.setTextContent(money+" VND");
        ctContent.setTextContent(contentTo);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imv_back:
                finish();
                break;
            case R.id.btn_confirm:
                processConfirm();
                break;
        }
    }

    public void processConfirm(){
        Intent intent = new Intent(ConfirmTransferActivity.this, TransferSuccessActivity.class);
        intent.putExtra(ACCOUNT_TO, accountTo);
        intent.putExtra(ACCOUNT_NAME_TO, accountName);
        intent.putExtra(MONEY_TO, money);
        intent.putExtra(CONTENT_TO, contentTo);

        startActivity(intent);
    }
}