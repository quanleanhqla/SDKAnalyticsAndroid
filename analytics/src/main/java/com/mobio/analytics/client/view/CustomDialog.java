package com.mobio.analytics.client.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobio.analytics.R;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.models.ScreenConfigObject;
import com.mobio.analytics.client.utility.LogMobio;

import java.util.HashMap;

public class CustomDialog {
    private Activity activity;
    private NotiResponseObject notiResponseObject;
    private Class des;

    public CustomDialog(Activity activity, NotiResponseObject notiResponseObject, Class des) {
        this.activity = activity;
        this.notiResponseObject = notiResponseObject;
        this.des = des;
    }

    public void showDialog(){
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(com.mobio.analytics.R.layout.custom_popup);

        Button btnAction = (Button) dialog.findViewById(com.mobio.analytics.R.id.btn_action);
        ImageView imvClose = (ImageView) dialog.findViewById(com.mobio.analytics.R.id.imv_close);
        TextView tvTitle = (TextView) dialog.findViewById(com.mobio.analytics.R.id.tv_title);
        TextView tvDetail = (TextView) dialog.findViewById(com.mobio.analytics.R.id.tv_detail);
        Button btnCancel = (Button) dialog.findViewById(com.mobio.analytics.R.id.btn_cancel);

        tvTitle.setText(notiResponseObject.getTitle());
        tvDetail.setText(notiResponseObject.getContent());

        imvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //todo

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LogMobio.logD("QuanLA", "close popup "+notiResponseObject.getPushId());
            }
        });

        Class finalDes = des;
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //todo
                if (finalDes != null && !activity.getClass().getSimpleName().equals("LoginActivity")) {
                    Intent desIntent = new Intent(activity, finalDes);
                    activity.startActivity(desIntent);
                }
            }
        });
        dialog.show();
    }
}
