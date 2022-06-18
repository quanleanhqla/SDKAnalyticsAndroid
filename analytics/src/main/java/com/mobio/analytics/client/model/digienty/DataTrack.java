package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class DataTrack extends Properties {
    private static final String KEY_TRACK = "track";

    public DataTrack(){}

    public DataTrack(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    DataTrack(Map<String, Object> delegate) {
        super(delegate);
    }

    public DataTrack putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public DataTrack putTrack(Track track){
        return putValue(KEY_TRACK, track);
    }

    public Track getTrack(){
        return getValueMap(KEY_TRACK, Track.class);
    }
}
