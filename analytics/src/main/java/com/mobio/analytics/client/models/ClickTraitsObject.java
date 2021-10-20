package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class ClickTraitsObject extends TraitsObject{
    @SerializedName("detail")
    private String detail;

    public ClickTraitsObject(String detail) {
        super();
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
