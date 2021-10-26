package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class ScreenTraitsObject extends TraitsObject{

    @SerializedName("title")
    private String title;

    @SerializedName("name")
    private String activityName;

    @SerializedName("time")
    private int recordTime;


    public ScreenTraitsObject(String title, String activityName, int recordTime) {
        super();
        this.title = title;
        this.activityName = activityName;
        this.recordTime = recordTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(int recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "ScreenTraitsObject{" +
                "title='" + title + '\'' +
                ", activityName='" + activityName + '\'' +
                ", recordTime=" + recordTime +
                '}';
    }
}
