package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class TrackingCode extends Properties {
    private static final String KEY_STATUS = "status";

    public TrackingCode() {}

    public TrackingCode(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    TrackingCode(Map<String, Object> delegate) {
        super(delegate);
    }

    public TrackingCode putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public TrackingCode putStatus(int status){
        return putValue(KEY_STATUS, status);
    }

    public int getData(){
        return getInt(KEY_STATUS, 1);
    }
}
