package com.mobio.analytics.client.model.old;

import com.google.gson.annotations.SerializedName;
import com.mobio.analytics.client.utility.Utils;

public class TraitsObject {

    @SerializedName("action_time")
    protected String actionTime;

    public TraitsObject() {
        this.actionTime = Utils.getTimeUTC();
    }

    public String getActionTime() {
        return actionTime;
    }

    public void setActionTime(String actionTime) {
        this.actionTime = actionTime;
    }


    @Override
    public String toString() {
        return "TraitsObject{" +
                "actionTime='" + actionTime + '\'' +
                '}';
    }
}
