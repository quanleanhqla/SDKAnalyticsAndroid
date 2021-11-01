package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class OpenFirstAppTraitsObject extends TraitsObject{

    @SerializedName("version")
    private String version;

    @SerializedName("build")
    private String build;

    public OpenFirstAppTraitsObject(String version, String build) {
        super();
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
}
