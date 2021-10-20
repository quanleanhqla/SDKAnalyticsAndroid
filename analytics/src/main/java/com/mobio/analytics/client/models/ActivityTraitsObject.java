package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class ActivityTraitsObject extends TraitsObject{

    @SerializedName("detail")
    private String detail;

    public ActivityTraitsObject(String detail) {
        super();
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "ActivityTraitsObject{" +
                "detail='" + detail + '\'' +
                '}';
    }
}
