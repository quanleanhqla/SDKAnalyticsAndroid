package com.mobio.analytics.client.model.reponse;

import com.google.gson.annotations.SerializedName;
import com.mobio.analytics.client.model.digienty.Device;

public class SendEventResponse {

    @SerializedName("message")
    String message;

    @SerializedName("code")
    String code;

    @SerializedName("data")
    Data data;

    public SendEventResponse(String message, String code, Data data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SendEventResponse{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", data=" + data +
                '}';
    }

    public static class Data {
        @SerializedName("action_time")
        String actionTime;

        @SerializedName("d_id")
        String dId;

        @SerializedName("t_id")
        String tId;

        @SerializedName("u_id")
        String uId;

        public Data(String actionTime, String dId, String tId, String uId) {
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
            return "Data{" +
                    "actionTime='" + actionTime + '\'' +
                    ", dId='" + dId + '\'' +
                    ", tId='" + tId + '\'' +
                    ", uId='" + uId + '\'' +
                    '}';
        }
    }
}
