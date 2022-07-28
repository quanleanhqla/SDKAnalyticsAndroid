package com.mobio.analytics.client.inapp.nativePopup;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobio.analytics.R;
import com.mobio.analytics.client.utility.DownloadImageTask;
import com.mobio.analytics.client.utility.LogMobio;

public abstract class BaseDialog {
    protected Activity activity;
    protected Dialog dialog;

    public void show() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_popup);

        Button btnAction = (Button) dialog.findViewById(R.id.btn_action);
        ImageView imvClose = (ImageView) dialog.findViewById(R.id.imv_close);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tvDetail = (TextView) dialog.findViewById(R.id.tv_detail);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView imvContent = (ImageView) dialog.findViewById(R.id.imv_content);

        String urlImage = getImage();

        if (urlImage != null) {
            LogMobio.logD("QuanLA", "url");
            imvContent.setVisibility(View.VISIBLE);
            new DownloadImageTask(imvContent)
                    .execute(urlImage);
        } else imvContent.setVisibility(View.GONE);

        tvTitle.setText(getTitle());
        tvDetail.setText(getDetail());
        btnAction.setText(getAction());

        imvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                close();
                //todo call api update device token

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                cancel();
                //todo call api update device token
            }
        });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                action();
            }
        });


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                doDismiss();
            }
        });

        dialog.show();
    }

    abstract String getDetail();

    abstract String getTitle();

    abstract String getAction();

    abstract String getImage();

    abstract void close();

    abstract void cancel();

    abstract void action();

    abstract void doDismiss();
}
