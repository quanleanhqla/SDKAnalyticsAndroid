package com.mobio.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.mobio.analytics.client.Analytics;
import com.mobio.analytics.client.models.IdentifyObject;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showCustomUI();
        setContentView(R.layout.activity_login);
        initView();
        addListener();
    }

    public void initView(){
        btnLogin = findViewById(R.id.btn_login);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
    }

    private void showCustomUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void addListener(){

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                Log.d("LoginActivity","userName" + userName);
                Log.d("LoginActivity","password" + password);

                if(!TextUtils.isEmpty(userName) &&
                        !TextUtils.isEmpty(password) &&
                        isEmailValid(userName)){
                    SharedPreferencesUtils.editString(LoginActivity.this, SharedPreferencesUtils.KEY_USER_NAME, userName);
                    SharedPreferencesUtils.editString(LoginActivity.this, SharedPreferencesUtils.KEY_PASSWORD, password);
                    SharedPreferencesUtils.editBool(LoginActivity.this, SharedPreferencesUtils.KEY_STATE_LOGIN, true);
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Analytics.getInstance().identify(new IdentifyObject.Builder()
                        .withEmail(userName).build());
                    }
                    //Analytics.with(LoginActivity.this).track("Login success");
                }
                else {
                    if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)){
                        Toast.makeText(LoginActivity.this, "Mail or password is null", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Invalid mail", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}