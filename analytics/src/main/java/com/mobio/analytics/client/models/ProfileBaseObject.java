package com.mobio.analytics.client.models;

import android.os.Build;

import com.google.gson.annotations.SerializedName;

public class ProfileBaseObject {
    @SerializedName("customer_id")
    private String customerId;

    public ProfileBaseObject() {
        this.customerId = Build.ID;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
