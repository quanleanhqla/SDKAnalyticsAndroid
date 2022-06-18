package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class App extends Properties{
    private static final String KEY_MANUFACTURER = "manufacturer";
    private static final String KEY_NAME = "name";
    private static final String KEY_MODEL = "model";
    private static final String KEY_TYPE = "type";

    public App() {}

    public App(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    App(Map<String, Object> delegate) {
        super(delegate);
    }

    public App putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public App putManufacturer(String manufacturer){
        return putValue(KEY_MANUFACTURER, manufacturer);
    }

    public App putName(String name){
        return putValue(KEY_NAME, name);
    }

    public App putModel(String model){
        return putValue(KEY_MODEL, model);
    }

    public App putType(String type){
        return putValue(KEY_TYPE, type);
    }
}
