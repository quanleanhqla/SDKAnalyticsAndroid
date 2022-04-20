package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class Notification extends Properties {
    public static final String KEY_GRANTED = "granted";
    public static final String KEY_DENIED = "denied";

    private static final String KEY_PERMISSON = "permission";
    private static final String KEY_TOKEN = "token";

    public Notification() {}

    public Notification(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Notification(Map<String, Object> delegate) {
        super(delegate);
    }

    public Notification putValue(String key, Object value) {
        super.putValue(key, value);
        return this;
    }

    public Notification putPermisson(String permisson){
        return putValue(KEY_PERMISSON, permisson);
    }

    public Notification putToken(String token){
        return putValue(KEY_TOKEN, token);
    }
}
