package com.mobio.analytics.client.models;

public class TrackObject {
    private String name;
    private String eventKey;

    public TrackObject(Builder b){
        name = b.mName;
        eventKey = b.mEventKey;
    }

    public String getName() {
        return name;
    }

    public String getEventKey() {
        return eventKey;
    }

    public static class Builder {
        private String mName;
        private String mEventKey;

        public Builder(){}

        public Builder withName(String name){
            this.mName = name;
            return this;
        }

        public Builder withEventKey(String eventKey){
            this.mEventKey = eventKey;
            return this;
        }

        public TrackObject build(){
            return new TrackObject(this);
        }
    }
}
