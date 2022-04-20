package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class Os extends Properties{
    private static final String KEY_NAME = "name";
    private static final String KEY_VERSION = "version";

    public Os() {}

    public Os(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Os(Map<String, Object> delegate) {
        super(delegate);
    }

    public Os putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public Os putName(String name){
        return putValue(KEY_NAME, name);
    }

    public Os putVersion(String version){
        return putValue(KEY_VERSION, version);
    }
}
