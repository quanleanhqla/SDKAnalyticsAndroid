package com.mobio.analytics.client.model.old;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class DataObject {
    @SerializedName("context")
    private ContextObject context;

    @SerializedName("profile_info")
    private ProfileBaseObject profileInfo;

    @SerializedName("event_key")
    private String eventKey;

    @SerializedName("anonymousId")
    private String anonymousId;

    @SerializedName("properties")
    private PropertiesObject properties;

    @SerializedName("type")
    private String type;

    @SerializedName("event_data")
    private TraitsObject traits;

    public DataObject(Builder b) {
        this.context = b.mContext;
        this.profileInfo = b.mProfileInfo;
        this.eventKey = b.mEventKey;
        this.anonymousId = b.mAnonymousId;
        this.properties = b.mProperties;
        this.type = b.mType;
    }

    public static class Builder{
        private ContextObject mContext;
        private ProfileBaseObject mProfileInfo;
        private String mEventKey;
        private String mAnonymousId;
        private PropertiesObject mProperties;
        private String mType;
        private TraitsObject mTraits;

        public Builder(){
        }

        public Builder withContext(ContextObject context){
            this.mContext = context;
            return this;
        }

        public Builder withProfileInfoObject(ProfileBaseObject profileInfoObject){
            this.mProfileInfo = profileInfoObject;
            return this;
        }

        public Builder withEvent(String eventKey){
            this.mEventKey = eventKey;
            return this;
        }

        public Builder withAnonymousId(String anonymousId){
            this.mAnonymousId = anonymousId;
            return this;
        }

        public Builder withProperties(PropertiesObject properties){
            this.mProperties = properties;
            return this;
        }

        public Builder withType(String type){
            this.mType = type;
            return this;
        }

        public Builder withTraits(TraitsObject traits){
            this.mTraits = traits;
            return this;
        }

        public DataObject build(){
            return new DataObject(this);
        }
    }

    public ContextObject getContext() {
        return context;
    }

    public void setContext(ContextObject context) {
        this.context = context;
    }

    public ProfileBaseObject getProfileInfo() {
        return profileInfo;
    }

    public void setProfileInfo(ProfileBaseObject profileInfo) {
        this.profileInfo = profileInfo;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getAnonymousId() {
        return anonymousId;
    }

    public void setAnonymousId(String anonymousId) {
        this.anonymousId = anonymousId;
    }

    public PropertiesObject getProperties() {
        return properties;
    }

    public void setProperties(PropertiesObject properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this).toString();
    }
}
