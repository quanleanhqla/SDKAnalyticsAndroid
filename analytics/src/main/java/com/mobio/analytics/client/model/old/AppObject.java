package com.mobio.analytics.client.model.old;

import com.google.gson.annotations.SerializedName;

public class AppObject {
    @SerializedName("namespace")
    private String namespace;

    @SerializedName("name")
    private String name;

    @SerializedName("build")
    private String build;

    @SerializedName("version")
    private String version;

    public AppObject(String namespace, String name, String build, String version) {
        this.namespace = namespace;
        this.name = name;
        this.build = build;
        this.version = version;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "AppObject{" +
                "namespace='" + namespace + '\'' +
                ", name='" + name + '\'' +
                ", build='" + build + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
