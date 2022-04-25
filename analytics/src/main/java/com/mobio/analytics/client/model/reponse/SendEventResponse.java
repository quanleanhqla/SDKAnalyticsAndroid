package com.mobio.analytics.client.model.reponse;

import com.google.gson.annotations.SerializedName;
import com.mobio.analytics.client.model.digienty.Device;

public class SendEventResponse {
    @SerializedName("action_time")
    String actionTime;

    @SerializedName("d_id")
    String dId;

    @SerializedName("t_id")
    String tId;

    @SerializedName("u_id")
    String uId;

    public SendEventResponse(String actionTime, String dId, String tId, String uId) {
        this.actionTime = actionTime;
        this.dId = dId;
        this.tId = tId;
        this.uId = uId;
    }

    public String getActionTime() {
        return actionTime;
    }

    public String getdId() {
        return dId;
    }

    public String gettId() {
        return tId;
    }

    public String getuId() {
        return uId;
    }

    @Override
    public String toString() {
        return "SendEventResponse{" +
                "actionTime='" + actionTime + '\'' +
                ", dId='" + dId + '\'' +
                ", tId='" + tId + '\'' +
                ", uId='" + uId + '\'' +
                '}';
    }
}
