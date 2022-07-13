package com.mobio.analytics.client.model.old;

import java.util.Arrays;

public class ScreenConfigObject {
    private String title;
    private String name;
    private int[] visitTime;
    private Class<?> className;
    private boolean isInitialScreen;

    public ScreenConfigObject(String title, String name, int[] visitTime, Class<?> className, boolean isInitialScreen) {
        this.title = title;
        this.name = name;
        this.visitTime = visitTime;
        this.className = className;
        this.isInitialScreen = isInitialScreen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActivityName() {
        return name;
    }

    public void setActivityName(String activityName) {
        this.name = activityName;
    }

    public int[] getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(int[] visitTime) {
        this.visitTime = visitTime;
    }

    public Class<?> getClassName() {
        return className;
    }

    public void setClassName(Class<?> className) {
        this.className = className;
    }

    public boolean isInitialScreen() {
        return isInitialScreen;
    }

    public void setInitialScreen(boolean initialScreen) {
        isInitialScreen = initialScreen;
    }

    @Override
    public String toString() {
        return "ScreenConfigObject{" +
                "title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", visitTime=" + Arrays.toString(visitTime) +
                ", className=" + className +
                ", isInitialScreen=" + isInitialScreen +
                '}';
    }
}
