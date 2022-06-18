package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class Notification extends Properties {
    public static final String KEY_GRANTED = "granted";
    public static final String KEY_DENIED = "denied";

    private static final String KEY_SDK = "sdk";
    private static final String KEY_DEVICE = "device";
    private static final String KEY_DETAIL = "detail";
    private static final String KEY_ACTION_TIME = "action_time";

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

    public Notification putSdk(Sdk sdk){
        return putValue(KEY_SDK, sdk);
    }

    public Notification putDevice(IdentityDetail identityDetail){
        return putValue(KEY_DEVICE, identityDetail);
    }

    public IdentityDetail getDevice(){
        return getValueMap(KEY_DEVICE, IdentityDetail.class);
    }

    public Notification putDetail(Detail detail){
        return putValue(KEY_DETAIL, detail);
    }

    public Detail getDetail(){
        return getValueMap(KEY_DETAIL, Detail.class);
    }

    public Notification putActionTime(long actionTime){
        return putValue(KEY_ACTION_TIME, actionTime);
    }

    public static class Detail extends Properties {
        private static final String KEY_PERMISSON = "permission";
        private static final String KEY_TOKEN = "token";

        public Detail() {}

        public Detail(int initialCapacity) {
            super(initialCapacity);
        }

        // For deserialization
        Detail(Map<String, Object> delegate) {
            super(delegate);
        }

        public Detail putValue(String key, Object value) {
            super.putValue(key, value);
            return this;
        }

        public Detail putPermission(String permission){
            return putValue(KEY_PERMISSON, permission);
        }

        public String getPermission(){
            return getString(KEY_PERMISSON);
        }

        public Detail putToken(String token){
            return putValue(KEY_TOKEN, token);
        }
    }
}
