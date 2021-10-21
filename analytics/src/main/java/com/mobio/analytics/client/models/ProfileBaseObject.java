package com.mobio.analytics.client.models;

import android.content.Context;
import android.os.Build;

import com.google.gson.annotations.SerializedName;
import com.mobio.analytics.client.utility.SharedPreferencesUtils;
import com.mobio.analytics.client.utility.Utils;

public class ProfileBaseObject {
    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("push_id")
    private PushObject pushId;

    public ProfileBaseObject(Context context) {
        this.customerId = Build.ID;
        this.pushId = new PushObject(SharedPreferencesUtils.getString(context, SharedPreferencesUtils.KEY_DEVICE_TOKEN)
                , true, Utils.getTimeUTC(), "VI");
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public PushObject getPushId() {
        return pushId;
    }

    public void setPushId(PushObject pushId) {
        this.pushId = pushId;
    }
}
