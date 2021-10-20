package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class CreateDeviceObject extends ProfileBaseObject{

    @SerializedName("source")
    private String source;

    public CreateDeviceObject(String source) {
        super();
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
