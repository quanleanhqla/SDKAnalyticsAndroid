package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class LibraryObject {
    @SerializedName("version")
    private String version;

    @SerializedName("name")
    private String name;

    public LibraryObject(String version, String name) {
        this.version = version;
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LibraryObject{" +
                "version='" + version + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
