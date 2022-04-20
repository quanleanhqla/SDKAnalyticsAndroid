package com.mobio.analytics.client.model.old;

import com.google.gson.annotations.SerializedName;

public class PropertiesObject {
    @SerializedName("version")
    private String version;

    @SerializedName("build")
    private String build;

    public PropertiesObject(String version, String build) {
        this.version = version;
        this.build = build;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    @Override
    public String toString() {
        return "PropertyObject{" +
                "version='" + version + '\'' +
                ", build='" + build + '\'' +
                '}';
    }
}
