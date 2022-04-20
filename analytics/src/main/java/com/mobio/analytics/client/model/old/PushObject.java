package com.mobio.analytics.client.model.old;

import com.google.gson.annotations.SerializedName;

public class PushObject {
    @SerializedName("push_id")
    private String pushId;

    @SerializedName("app_id")
    private String appId;

    @SerializedName("is_logged")
    private boolean isLogged;

    @SerializedName("last_access")
    private String lastAccess;

    @SerializedName("os_type")
    private int osType;

    @SerializedName("lang")
    private String language;


    public PushObject(String pushId,  boolean isLogged, String lastAccess, String language) {
        this.pushId = pushId;
        this.appId = "ANDROID";
        this.isLogged = isLogged;
        this.lastAccess = lastAccess;
        this.osType = 2;
        this.language = language;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    public String getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(String lastAccess) {
        this.lastAccess = lastAccess;
    }

    public int getOsType() {
        return osType;
    }

    public void setOsType(int osType) {
        this.osType = osType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "PushObject{" +
                "pushId='" + pushId + '\'' +
                ", appId='" + appId + '\'' +
                ", isLogged=" + isLogged +
                ", lastAccess='" + lastAccess + '\'' +
                ", osType=" + osType +
                ", language='" + language + '\'' +
                '}';
    }
}
