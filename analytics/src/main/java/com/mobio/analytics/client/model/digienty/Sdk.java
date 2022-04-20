package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class Sdk extends Properties {
    private static final String KEY_CODE = "code";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_BUILD = "build";
    private static final String KEY_NAME = "name";
    private static final String KEY_VERSION = "version";

    public Sdk() {}

    public Sdk(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Sdk(Map<String, Object> delegate) {
        super(delegate);
    }

    public Sdk putValue(String key, Object value) {
        super.putValue(key, value);
        return this;
    }

    public Sdk putCode(String code){
        return putValue(KEY_CODE, code);
    }

    public Sdk putSource(String source){
        return putValue(KEY_SOURCE, source);
    }

    public Sdk putBuild(String build){
        return putValue(KEY_BUILD, build);
    }

    public Sdk putName(String name){
        return putValue(KEY_NAME, name);
    }

    public Sdk putVersion(String version){
        return putValue(KEY_VERSION, version);
    }
}
