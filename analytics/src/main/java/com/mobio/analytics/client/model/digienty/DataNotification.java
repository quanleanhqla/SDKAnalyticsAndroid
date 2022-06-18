package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class DataNotification extends Properties{
    private static final String KEY_NOTIFICATION = "notification";

    public DataNotification(){}

    public DataNotification(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    DataNotification(Map<String, Object> delegate) {
        super(delegate);
    }

    public DataNotification putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public DataNotification putNotification(Notification notification){
        return putValue(KEY_NOTIFICATION, notification);
    }

    public Notification getNotification(){
        return getValueMap(KEY_NOTIFICATION, Notification.class);
    }
}
