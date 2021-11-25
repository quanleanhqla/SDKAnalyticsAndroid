package com.mobio.analytics.client.models;

import java.util.Arrays;

public class ScreenConfigObject {
    private String title;
    private String activityName;
    private int[] visitTime;
    private Class className;

    public ScreenConfigObject(String title, String activityName, int[] visitTime, Class className) {
        this.title = title;
        this.activityName = activityName;
        this.visitTime = visitTime;
        this.className = className;
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

    public int[] getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(int[] visitTime) {
        this.visitTime = visitTime;
    }

    public Class getClassName() {
        return className;
    }

    public void setClassName(Class className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "ScreenConfigObject{" +
                "title='" + title + '\'' +
                ", activityName='" + activityName + '\'' +
                ", visitTime=" + Arrays.toString(visitTime) +
                ", className=" + className +
                '}';
    }
}
