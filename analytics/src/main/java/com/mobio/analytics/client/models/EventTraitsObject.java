package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class EventTraitsObject extends TraitsObject{
    @SerializedName("type")
    private int eventType;

    @SerializedName("detail")
    private String detail;

    public EventTraitsObject(Builder builder) {
        super();
        this.eventType = builder.mEventType;
        this.detail = builder.mDetail;
    }

    public static class Builder{
        private int mEventType;
        private String mDetail;

        public Builder(){}

        public Builder withEventType(int eventType){
            this.mEventType = eventType;
            return this;
        }

        public Builder withDetail(String detail){
            this.mDetail = detail;
            return this;
        }

        public EventTraitsObject build(){
            return new EventTraitsObject(this);
        }
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
