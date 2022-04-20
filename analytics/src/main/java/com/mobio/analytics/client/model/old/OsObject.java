package com.mobio.analytics.client.model.old;

import com.google.gson.annotations.SerializedName;

public class OsObject {
    @SerializedName("name")
    private String name;

    @SerializedName("version")
    private String version;

    public OsObject(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "OsObject{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
