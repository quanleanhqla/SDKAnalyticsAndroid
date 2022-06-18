package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class DataIdentity extends Properties {
    private static final String KEY_IDENTITY = "identity";

    public DataIdentity(){}

    public DataIdentity(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    DataIdentity(Map<String, Object> delegate) {
        super(delegate);
    }

    public DataIdentity putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public DataIdentity putIdentity(Identity identity){
        return putValue(KEY_IDENTITY, identity);
    }

    public Identity getIdentity(){
        return getValueMap(KEY_IDENTITY, Identity.class);
    }
}
