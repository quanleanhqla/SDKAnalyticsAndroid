package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class SendSyncObject {
    @SerializedName("data")
    private DataObject dataObject;

    public SendSyncObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public String toString() {
        return "SendSyncObject{" +
                "dataObject=" + dataObject +
                '}';
    }
}
