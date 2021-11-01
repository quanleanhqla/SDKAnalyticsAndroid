package com.mobio.analytics.client.models;

import java.util.HashMap;

public class ValueMap extends HashMap<String, Object> {

    public ValueMap(){}

    public ValueMap put(String key, Object value){
        super.put(key, value);
        return this;
    }


    public ValueMap remove(String key){
        super.remove(key);
        return this;
    }

    public ValueMap get(String key){
        return (ValueMap) super.get(key);
    }
}
