package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class Screen extends Properties {
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";

    public Screen() {}

    public Screen(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Screen(Map<String, Object> delegate) {
        super(delegate);
    }

    public Screen putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public Screen putWidth(int width){
        return putValue(KEY_WIDTH, width);
    }

    public Screen putHeight(int height){
        return putValue(KEY_HEIGHT, height);
    }
}
