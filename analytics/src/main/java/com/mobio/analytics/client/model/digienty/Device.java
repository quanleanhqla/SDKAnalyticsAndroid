package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class Device extends Properties {
    private static final String KEY_TYPE = "type";
    private static final String KEY_CHANNEL = "channel";
    private static final String KEY_DID = "d_id";
    private static final String KEY_TID = "t_id";
    private static final String KEY_UID = "u_id";

    public Device() {}

    public Device(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Device(Map<String, Object> delegate) {
        super(delegate);
    }

    public Device putValue(String key, Object value) {
        super.putValue(key, value);
        return this;
    }

    public Device putType(String type){
        return putValue(KEY_TYPE, type);
    }

    public Device putChannel(String channel){
        return putValue(KEY_CHANNEL, channel);
    }

    public Device putDId(String d_id){
        return putValue(KEY_DID, d_id);
    }

    public Device putTId(String t_id){
        return putValue(KEY_TID, t_id);
    }

    public Device putUId(String u_id){
        return putValue(KEY_UID, u_id);
    }
}
