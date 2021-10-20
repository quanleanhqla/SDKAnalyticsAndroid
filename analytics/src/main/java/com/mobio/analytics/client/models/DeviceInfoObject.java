package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class DeviceInfoObject {
    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("source")
    private String source;

    @SerializedName("device_name")
    private String deviceName;

    public DeviceInfoObject(Builder builder){
        this.deviceId = builder.deviceId;
        this.source = builder.source;
        this.deviceName = builder.deviceName;
    }

    public static class Builder{
        private String deviceId;
        private String source;
        private String deviceName;

        public Builder(){
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

        public DeviceInfoObject build(){
            return new DeviceInfoObject(this);
        }
    }
}
