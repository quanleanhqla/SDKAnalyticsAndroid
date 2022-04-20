package com.mobio.analytics.client.model.digienty;

import java.util.ArrayList;
import java.util.Map;

public class Track extends Properties {
    private static final String KEY_SDK = "sdk";
    private static final String KEY_DEVICE = "device";
    private static final String KEY_EVENTS = "events";

    public Track() {}

    public Track(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Track(Map<String, Object> delegate) {
        super(delegate);
    }

    public Track putValue(String key, Object value) {
        super.putValue(key, value);
        return this;
    }

    public Track putSdk(Sdk sdk){
        return putValue(KEY_SDK, sdk);
    }

    public Track putDevice(Device device){
        return putValue(KEY_DEVICE, device);
    }

    public Track putEvents(ArrayList<Event> eventArrayList){return putValue(KEY_EVENTS, eventArrayList);}
}
