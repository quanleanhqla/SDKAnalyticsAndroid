package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class Identity extends Properties {
    private static final String KEY_SDK = "sdk";
    private static final String KEY_IDENTITY_DETAIL = "device";
    private static final String KEY_ACTION_TIME = "action_time";

    public Identity(){}

    public Identity(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Identity(Map<String, Object> delegate) {
        super(delegate);
    }

    public Identity putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public Identity putSdk(Sdk sdk){
        return putValue(KEY_SDK, sdk);
    }

    public Identity putIdentityDetail(IdentityDetail identity_detail){
        return putValue(KEY_IDENTITY_DETAIL, identity_detail);
    }

    public IdentityDetail getDetail(){
        return getValueMap(KEY_IDENTITY_DETAIL, IdentityDetail.class);
    }

    public Identity putActionTime(long actionTime){
        return putValue(KEY_ACTION_TIME, actionTime);
    }
}
