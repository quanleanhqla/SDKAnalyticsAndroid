package com.mobio.analytics.client.model.digienty;

import java.util.List;
import java.util.Map;

public class Event extends ValueMap{
    private static final String KEY_SOURCE = "source";
    private static final String KEY_ACTION_TIME= "action_time";
    private static final String KEY_TYPE= "type";
    private static final String KEY_BASE= "base";
    private static final String KEY_DYNAMIC= "dynamics";
    private static final String KEY_INCLUEDED_REPORT= "included_report";

    public Event() {
    }

    public Event(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Event(Map<String, Object> delegate) {
        super(delegate);
    }

    public Event putValue(String key, Object value) {
        super.putValue(key, value);
        return this;
    }

    public Event putSource(String source){
        return putValue(KEY_SOURCE, source);
    }

    public Event putType(String type){
        return putValue(KEY_TYPE, type);
    }

    public Event putActionTime(long actionTime){
        return putValue(KEY_ACTION_TIME, actionTime);
    }

    public Event putBase(Base base){
        return putValue(KEY_BASE, base);
    }

    public Event putDynamic(List<Dynamic> dynamic){
        return putValue(KEY_DYNAMIC, dynamic);
    }

    public Event putIncludedReport(int includedReport){
        return putValue(KEY_INCLUEDED_REPORT, includedReport);
    }

    public static class Base extends Properties{
        public static final String KEY_OBJECT = "object";
        public static final String KEY_VALUE = "value";

        public Base() {
        }

        public Base putValue(String key, Object value) {
            super.putValue(key, value);
            return this;
        }

        public Base putObject(String object){
            return putValue(KEY_OBJECT, object);
        }

        public Base putValue(Object value){
            return putValue(KEY_VALUE, value);
        }
    }

    public static class Dynamic extends Properties{
        public static final String KEY_EVENT_KEY = "event_key";
        public static final String KEY_EVENT_DATA = "event_data";

        public Dynamic() {
        }

        public Dynamic(int initialCapacity) {
            super(initialCapacity);
        }

        // For deserialization
        Dynamic(Map<String, Object> delegate) {
            super(delegate);
        }

        public Dynamic putValue(String key, Object value) {
            super.putValue(key, value);
            return this;
        }

        public Dynamic putEventKey(String eventkey){
            return putValue(KEY_EVENT_KEY, eventkey);
        }

        public Dynamic putEventData(Properties eventData){
            return putValue(KEY_EVENT_DATA, eventData);
        }

        public Properties getEventData(){
            return getValueMap(KEY_EVENT_DATA, Properties.class);
        }
    }
}
