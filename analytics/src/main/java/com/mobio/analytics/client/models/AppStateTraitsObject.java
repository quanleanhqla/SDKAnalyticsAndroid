package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class AppStateTraitsObject extends TraitsObject{

    @SerializedName("state")
    private String state;

    public AppStateTraitsObject() {
        super();
    }

    public AppStateTraitsObject(String state){
        super();
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "AppStateTraitsObject{" +
                "state='" + state + '\'' +
                '}';
    }
}
