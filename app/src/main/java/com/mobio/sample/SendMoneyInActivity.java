package com.mobio.sample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobio.analytics.client.receiver.NotificationDismissedReceiver;
import com.mobio.analytics.client.utility.LogMobio;

import java.util.StringTokenizer;

public class SendMoneyInActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String ACCOUNT_TO = "account_to";
    public static final String ACCOUNT_NAME_TO = "account_name_to";
    public static final String MONEY_TO = "money_to";
    public static final String CONTENT_TO = "content_to";

    private static final long DISCONNECT_TIMEOUT = 10000;

    private Button btnContinue;
    private ImageView imvBack;
    private EditText edtAccount;
    private EditText edtMoney;
    private EditText edtContent;
    private TextView tvName;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_send_money_in);
        init();
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    public void init(){
        btnContinue = findViewById(R.id.btn_continue);
        imvBack = findViewById(R.id.imv_back);
        edtAccount = findViewById(R.id.et_account);
        edtMoney = findViewById(R.id.et_amount_money);
        edtContent = findViewById(R.id.et_content);
        tvName = findViewById(R.id.tv_name);

        handler = new Handler();

        imvBack.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

        edtAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length() > 0){
                    tvName.setVisibility(View.VISIBLE);
                }
                else {
                    tvName.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try
                {
                    edtMoney.removeTextChangedListener(this);
                    String value = edtMoney.getText().toString();

                    if (value != null && !value.equals(""))
                    {

                        if(value.startsWith(".")){
                            edtMoney.setText("0.");
                        }
                        if(value.startsWith("0") && !value.startsWith("0.")){
                            edtMoney.setText("");

                        }


                        String str = edtMoney.getText().toString().replaceAll(",", "");
                        if (!value.equals(""))
                            edtMoney.setText(getDecimalFormattedString(str));
                        edtMoney.setSelection(edtMoney.getText().toString().length());
                    }
                    edtMoney.addTextChangedListener(this);
                    return;
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    edtMoney.addTextChangedListener(this);
                }
            }
        });
    }

    public void resetDisconnectTimer(){
        handler.removeCallbacks(timeOutRunable);
        handler.postDelayed(timeOutRunable, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        handler.removeCallbacks(timeOutRunable);
    }

    public static String getDecimalFormattedString(String value)
    {
        StringTokenizer lst = new StringTokenizer(value, ".");
        String str1 = value;
        String str2 = "";
        if (lst.countTokens() > 1)
        {
            str1 = lst.nextToken();
            str2 = lst.nextToken();
        }
        String str3 = "";
        int i = 0;
        int j = -1 + str1.length();
        if (str1.charAt( -1 + str1.length()) == '.')
        {
            j--;
            str3 = ".";
        }
        for (int k = j;; k--)
        {
            if (k < 0)
            {
                if (str2.length() > 0)
                    str3 = str3 + "." + str2;
                return str3;
            }
            if (i == 3)
            {
                str3 = "," + str3;
                i = 0;
            }
            str3 = str1.charAt(k) + str3;
            i++;
        }

    }

    private Runnable timeOutRunable = new Runnable() {
        @Override
        public void run() {
            stopDisconnectTimer();
            if(com.mobio.analytics.client.utility.SharedPreferencesUtils.getBool(SendMoneyInActivity.this,
                    com.mobio.analytics.client.utility.SharedPreferencesUtils.KEY_APP_FOREGROUD)){
                showPopup();
            }
            else {
                int reqCode = 1;
                Intent intent = new Intent(getApplicationContext(), SendMoneyInActivity.class);
                showNotification(SendMoneyInActivity.this, "SDKMobio", "Send money to bank now", intent, reqCode);
            }
        }
    };

    public void showPopup(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_popup);

        Button btnAction = (Button) dialog.findViewById(R.id.btn_action);
        ImageView imvClose = (ImageView) dialog.findViewById(R.id.imv_close);

        imvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //Analytics.getInstance().track(Analytics.DEMO_EVENT, Analytics.TYPE_CLICK,"Click No on Popup");
            }
        });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //Analytics.getInstance().track(Analytics.DEMO_EVENT, Analytics.TYPE_CLICK,"Click Yes on Popup");
                    resetDisconnectTimer();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imv_back:
                finish();
                break;
            case R.id.btn_continue:
                processContinue();
                break;
        }
    }

    public void processContinue(){
        String accountTo = edtAccount.getText().toString();
        String accountName = tvName.getText().toString();
        String money = edtMoney.getText().toString();
        String contentTo = edtContent.getText().toString();

        if(tvName.getVisibility() == View.VISIBLE){
            if(!TextUtils.isEmpty(accountTo) && !TextUtils.isEmpty(accountName)
            && !TextUtils.isEmpty(money) && !TextUtils.isEmpty(contentTo)){
                Intent intent = new Intent(SendMoneyInActivity.this, ConfirmTransferActivity.class);
                intent.putExtra(ACCOUNT_TO, accountTo);
                intent.putExtra(ACCOUNT_NAME_TO, accountName);
                intent.putExtra(MONEY_TO, money);
                intent.putExtra(CONTENT_TO, contentTo);

                startActivity(intent);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Analytics.getInstance().track(Analytics.DEMO_EVENT, Analytics.TYPE_CLICK,"Click Open from noti");
        resetDisconnectTimer();
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        String CHANNEL_ID = "channel_name";// The id of the channel.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDeleteIntent(createOnDismissedIntent(this, reqCode))
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null){
            if(timeOutRunable != null) {
                handler.removeCallbacks(timeOutRunable);
            }
        }
    }
}