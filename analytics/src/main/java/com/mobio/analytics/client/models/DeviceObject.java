package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class DeviceObject {
    @SerializedName("name")
    private String name;

    @SerializedName("manufacturer")
    private String manufacturer;

    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private String type;

    @SerializedName("model")
    private String model;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("source")
    private String source;

    @SerializedName("device_name")
    private String deviceName;

    public DeviceObject(Builder builder) {
        this.name = builder.name;
        this.manufacturer = builder.manufacturer;
        this.id = builder.id;
        this.type = builder.type;
        this.model = builder.model;
        this.deviceId = builder.deviceId;
        this.source = builder.source;
        this.deviceName = builder.deviceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public static class Builder{
        private String deviceId;
        private String source;
        private String deviceName;
        private String name;
        private String manufacturer;
        private String id;
        private String type;
        private String model;

        public Builder(){
        }

        public Builder withName(String name){
            this.name = name;
            return this;
        }

        public Builder withManufacturer(String manufacturer){
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder withId(String id){
            this.id = id;
            return this;
        }

        public Builder withType(String type){
            this.type = type;
            return this;
        }

        public Builder withModel(String model){
            this.model = model;
            return this;
        }

        public Builder withDeviceId(String deviceId){
            this.deviceId = deviceId;
            return this;
        }

        public Builder withSource(String source){
            this.source = source;
            return this;
        }

        public Builder withDeviceName(String deviceName){
            this.deviceName = deviceName;
            return this;
        }

        public DeviceObject build(){
            return new DeviceObject(this);
        }
    }

    @Override
    public String toString() {
        return "DeviceObject{" +
                "name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
