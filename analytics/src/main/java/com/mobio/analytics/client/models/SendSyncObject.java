package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class SendSyncObject {
    @SerializedName("data")
    private DataObject data;

    public SendSyncObject(DataObject dataObject) {
        this.data = dataObject;
    }

    public DataObject getDataObject() {
        return data;
    }

    public void setDataObject(DataObject dataObject) {
        this.data = dataObject;
    }

    @Override
    public String toString() {
        return "SendSyncObject{" +
                "data=" + data +
                '}';
    }
}
