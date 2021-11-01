package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class Body {
    @SerializedName("body")
    private SendSyncObject body;

    public Body(SendSyncObject body) {
        this.body = body;
    }
}
