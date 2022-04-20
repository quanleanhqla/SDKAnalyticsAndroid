package com.mobio.analytics.client.model.old;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

public class CreateDeviceObject extends ProfileBaseObject{

    @SerializedName("source")
    private String source;

    public CreateDeviceObject(Context context, String source) {
        super(context);
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
