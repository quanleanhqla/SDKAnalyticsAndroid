package com.mobio.analytics.client.inapp;

import static com.mobio.analytics.client.activity.PopupBuilderActivity.M_KEY_PUSH;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.mobio.analytics.client.activity.PopupBuilderActivity;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.inapp.htmlPopup.HtmlController;
import com.mobio.analytics.client.inapp.nativePopup.CustomDialog;
import com.mobio.analytics.client.inapp.nativePopup.InAppNativeFooterFragment;
import com.mobio.analytics.client.inapp.nativePopup.InAppNativeFragment;
import com.mobio.analytics.client.inapp.nativePopup.InAppNativeHeaderFragment;
import com.mobio.analytics.client.utility.LogMobio;

public class InAppController {

    private static Push currentlyDisplayingInApp = null;

    public static final String MInAppTypePopupBuilder = "popup";
    public static final String MInAppTypeHtml = "html";
    public static final String MInAppTypeAlert = "text";

    private Push push;
    private Class<?> des;
    private String assetPath;

    public InAppController(Push push, Class<?> des, String assetPath) {
        this.push = push;
        this.des = des;
        this.assetPath = assetPath;
    }

    public static void showInApp(Activity activity, Push push, String assetPath, Class<?> des){
        InAppNativeFragment inAppFragment = null;
        String type = push.getAlert().getContentType();
        switch (type) {
            case MInAppTypeAlert:
                if(getPopupPosition(push).equals("cc")) {
                    CustomDialog.showCustomDialog(activity, push, des);
                }
                else if(getPopupPosition(push).equals("tc")){
                    inAppFragment = new InAppNativeHeaderFragment();
                }
                else if(getPopupPosition(push).equals("bc")){
                    inAppFragment = new InAppNativeFooterFragment();
                }
                break;
            case MInAppTypeHtml:
                startPopupActivity(activity, push);
                break;
            case MInAppTypePopupBuilder:
                if(getPopupPosition(push).equals("cc")){
                    startPopupActivity(activity, push);
                }
                else {
                    HtmlController.showHtmlPopup(activity, push, assetPath);
                }
                break;
        }

        if(inAppFragment != null){
            FragmentTransaction fragmentTransaction =((FragmentActivity) activity)
                    .getSupportFragmentManager()
                    .beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString(InAppNativeFragment.M_KEY_PUSH, new Gson().toJson(push));
            inAppFragment.setArguments(bundle);
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            fragmentTransaction.add(android.R.id.content, inAppFragment);
            fragmentTransaction.commit();
        }
    }

    private static void startPopupActivity(Activity currentActivity, Push push) {
        Intent i = new Intent(currentActivity, PopupBuilderActivity.class);
        i.putExtra(M_KEY_PUSH, new Gson().toJson(push));
        currentActivity.startActivity(i);
    }

    private static String getPopupPosition(Push push){
        String positionPopup = push.getAlert().getString("position");
        if(positionPopup == null || positionPopup.isEmpty()) return "cc";
        return positionPopup;
    }
}
