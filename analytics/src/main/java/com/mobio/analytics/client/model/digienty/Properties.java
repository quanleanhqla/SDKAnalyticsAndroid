package com.mobio.analytics.client.model.digienty;

import com.mobio.analytics.client.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Properties extends ValueMap {

    public Properties() {}

    public Properties(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Properties(Map<String, Object> delegate) {
        super(delegate);
    }

    @Override
    public Properties putValue(String key, Object value) {
        super.putValue(key, value);
        return this;
    }

    public static Properties convertJsonStringtoProperties(String json) {
        if (json == null) return null;
        JSONObject jsonObject = null;
        Properties vm = null;
        try {
            jsonObject = new JSONObject(json);
            vm = toProperties(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vm;
    }

    public static Properties toProperties(JSONObject object) throws JSONException {
        Properties map = new Properties();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toProperties((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    protected static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toProperties((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
