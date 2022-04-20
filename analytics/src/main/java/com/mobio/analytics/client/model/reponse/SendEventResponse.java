package com.mobio.analytics.client.model.reponse;

import com.google.gson.annotations.SerializedName;
import com.mobio.analytics.client.model.digienty.Device;

public class SendEventResponse {
    @SerializedName("device")
    Device device;

    public SendEventResponse(Device device) {
        this.device = device;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "SendEventResponse{" +
                "device=" + device +
                '}';
    }
}
