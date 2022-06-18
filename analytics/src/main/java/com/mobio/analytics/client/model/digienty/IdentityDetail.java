package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class IdentityDetail extends Properties {
    private static final String KEY_TYPE = "type";
    private static final String KEY_CHANNEL = "channel";
    private static final String KEY_D_ID = "d_id";
    private static final String KEY_T_ID = "t_id";
    private static final String KEY_U_ID = "u_id";
    private static final String KEY_OS = "os";
    private static final String KEY_NETWORK = "network";
    private static final String KEY_SCREEN = "screen";
    private static final String KEY_LOCALE = "locale";
    private static final String KEY_TIMEZONE = "timezone";
    private static final String KEY_APP = "app";

    public IdentityDetail() {}

    public IdentityDetail(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    IdentityDetail(Map<String, Object> delegate) {
        super(delegate);
    }

    public IdentityDetail putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public IdentityDetail putType(String type){
        return putValue(KEY_TYPE, type);
    }

    public IdentityDetail putChannel(String channel){
        return putValue(KEY_CHANNEL, channel);
    }

    public IdentityDetail putDId(String d_id){
        return putValue(KEY_D_ID, d_id);
    }

    public IdentityDetail putTId(String t_id){
        return putValue(KEY_T_ID, t_id);
    }

    public IdentityDetail putUId(String u_id){
        return putValue(KEY_U_ID, u_id);
    }

    public IdentityDetail putOs(Os os){
        return putValue(KEY_OS, os);
    }

    public IdentityDetail putNetwork(Network network){
        return putValue(KEY_NETWORK, network);
    }

    public IdentityDetail putScreen(Screen screen){
        return putValue(KEY_SCREEN, screen);
    }

    public IdentityDetail putLocale(String locale){
        return putValue(KEY_LOCALE, locale);
    }

    public IdentityDetail putTimezone(String timezone){
        return putValue(KEY_TIMEZONE, timezone);
    }

    public IdentityDetail putApp(App app){
        return putValue(KEY_APP, app);
    }
}
