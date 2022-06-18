package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class Network extends Properties {
    private static final String KEY_CELLULAR = "cellular";
    private static final String KEY_BLUTOOTH = "bluetooth";
    private static final String KEY_WIFI = "wifi";
    private static final String KEY_ADDRESS = "address";

    public Network() {}

    public Network(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Network(Map<String, Object> delegate) {
        super(delegate);
    }

    public Network putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public Network putCellular(boolean cellular){
        return putValue(KEY_CELLULAR, cellular);
    }

    public Network putBluetooth(boolean bluetooth){
        return putValue(KEY_BLUTOOTH, bluetooth);
    }

    public Network putWifi(boolean wifi){
        return putValue(KEY_WIFI, wifi);
    }

    public Network putAddress(String address){
        return putValue(KEY_ADDRESS, address);
    }
}
