package com.mobio.analytics.client.inapp.nativePopup;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.utility.DownloadImageTask;
import com.mobio.analytics.client.utility.Utils;


public class InAppNativeFragment extends Fragment {
    public static final String M_KEY_PUSH = "mkeypush";

    Push push;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setUpImage(String urlImage, ImageView imageView){
        if(urlImage != null && !urlImage.isEmpty()) {
            new DownloadImageTask(imageView).execute(urlImage);
        }
    }

    void setUpButton(Button button, String text){
        button.setText(text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    void setUpText(TextView textView, String text){
        textView.setText(text);
    }

    void dismiss(){
        if (!Utils.isActivityDead(getActivity())) {
            final FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction;
            if (fragmentManager != null) {
                transaction = fragmentManager.beginTransaction();
                try {
                    transaction.remove(this).commit();
                } catch (IllegalStateException e) {
                    fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss();
                }
            }

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        String strPush = bundle.getString(M_KEY_PUSH);
        push = Push.convertJsonStringtoPush(strPush);
    }
}