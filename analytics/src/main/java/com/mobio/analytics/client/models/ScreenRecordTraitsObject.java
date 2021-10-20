package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class ScreenRecordTraitsObject extends TraitsObject{
    @SerializedName("screen_name")
    private String nameScreen;

    @SerializedName("record_time")
    private int recordTime;

    public ScreenRecordTraitsObject(String nameScreen, int recordTime) {
        super();
        this.nameScreen = nameScreen;
        this.recordTime = recordTime;
    }

    public String getNameScreen() {
        return nameScreen;
    }

    public void setNameScreen(String nameScreen) {
        this.nameScreen = nameScreen;
    }

    public int getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(int recordTime) {
        this.recordTime = recordTime;
    }
}
