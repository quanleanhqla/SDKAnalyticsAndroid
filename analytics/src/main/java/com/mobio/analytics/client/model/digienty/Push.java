package com.mobio.analytics.client.model.digienty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

public class Push extends Properties {
    private static final String KEY_DATA = "data";
    private static final String KEY_ALERT = "alert";
    private static final String KEY_BADGE = "badge";

    public Push() {}

    public Push(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Push(Map<String, Object> delegate) {
        super(delegate);
    }

    public Push putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public Push putData(Data data){
        return putValue(KEY_DATA, data);
    }

    public Data getData(){
        return getValueMap(KEY_DATA, Data.class);
    }

    public Push putAlert(Alert alert){
        return putValue(KEY_ALERT, alert);
    }

    public Alert getAlert(){
        return getValueMap(KEY_ALERT, Alert.class);
    }

    public Push putBadge(int badge){
        return putValue(KEY_BADGE, badge);
    }

    public static Push convertJsonStringtoPush(String json) {
        if (json == null) return null;
        JSONObject jsonObject = null;
        Push vm = null;
        try {
            jsonObject = new JSONObject(json);
            vm = toPush(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vm;
    }

    public static Push toPush(JSONObject object) throws JSONException {
        Push map = new Push();

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

    public static class Data extends Properties {
        private static final String KEY_POPUP_TYPE = "popup_type";
        private static final String KEY_POPUP_ID = "popup_id";
        private static final String KEY_POPUP_URL = "popup_url";
        private static final String KEY_MERCHANT_ID = "merchant_id";
        private static final String KEY_MASTER_CAMPAIGN_ID = "master_campaign_id";
        private static final String KEY_PROFILE_INFO = "profile_info";
        private static final String KEY_PROFILE_ID = "profile_id";
        private static final String KEY_SEND_ID = "send_id";
        private static final String KEY_CODE = "code";
        private static final String KEY_INSTANCE_ID = "instance_id";
        private static final String KEY_JOURNEY_ID = "journey_id";
        private static final String KEY_NODE_ID = "node_id";


        public Data() {}

        public Data(int initialCapacity) {
            super(initialCapacity);
        }

        // For deserialization
        Data(Map<String, Object> delegate) {
            super(delegate);
        }

        public Data putValue(String key, Object value){
            super.putValue(key, value);
            return this;
        }

        public Data putPopupType(String popup_type){
            return putValue(KEY_POPUP_TYPE, popup_type);
        }

        public Data putPopupId(String popup_id){
            return putValue(KEY_POPUP_ID, popup_id);
        }

        public String getPopupId(){
            return getString(KEY_POPUP_ID);
        }

        public Data putPopupUrl(String popup_url){
            return putValue(KEY_POPUP_URL, popup_url);
        }

        public String getPopupUrl(){
            return getString(KEY_POPUP_URL);
        }

        public Data putMerchantId(String merchant_id){
            return putValue(KEY_MERCHANT_ID, merchant_id);
        }

        public String getMerchantId(){
            return getString(KEY_MERCHANT_ID);
        }

        public Data putMasterCampaignId(String master_campaign_id){
            return putValue(KEY_MASTER_CAMPAIGN_ID, master_campaign_id);
        }

        public String getMasterCampaignId(){
            return getString(KEY_MASTER_CAMPAIGN_ID);
        }

        public Data putProfileInfo(Object profile_info){
            return putValue(KEY_PROFILE_INFO, profile_info);
        }

        public Data putProfileId(String profile_id){
            return putValue(KEY_PROFILE_ID, profile_id);
        }

        public String getProfileId(){
            return getString(KEY_PROFILE_ID);
        }

        public Data putJourneyId(String journey_id){
            return putValue(KEY_JOURNEY_ID, journey_id);
        }

        public String getJourneyId(){
            return getString(KEY_JOURNEY_ID);
        }

        public Data putNodeId(String node_id){
            return putValue(KEY_NODE_ID, node_id);
        }

        public String getNodeId(){
            return getString(KEY_NODE_ID);
        }

        public Data putSendId(String send_id){
            return putValue(KEY_SEND_ID, send_id);
        }

        public String getSendId(){
            return getString(KEY_SEND_ID);
        }

        public Data putCode(String code){
            return putValue(KEY_CODE, code);
        }

        public String getCode(){
            return getString(KEY_CODE);
        }

        public Data putInstanceId(String instance_id){
            return putValue(KEY_INSTANCE_ID, instance_id);
        }

        public String getInstanceId(){
            return getString(KEY_INSTANCE_ID);
        }
    }

    public static class Alert extends Properties {
        private static final String KEY_BACKGROUND_IMAGE = "background_image";
        private static final String KEY_POPUP_POSITION = "popup_position";
        private static final String KEY_CONTENT_TYPE = "content_type";
        private static final String KEY_POPUP_ID = "popup_id";
        private static final String KEY_URL_TARGET = "url_target";
        private static final String KEY_POPUP_URL = "popup_url";
        private static final String KEY_TITLE = "title";
        private static final String KEY_BODY = "body";
        private static final String KEY_BODY_HTML = "body_html";
        private static final String KEY_SCREEN_TO = "des_screen";
        private static final String KEY_SCREEN_FROM = "from_screen";

        public static final String TYPE_POPUP = "popup";
        public static final String TYPE_HTML = "html";
        public static final String TYPE_TEXT = "text";

        public Alert() {}

        public Alert(int initialCapacity) {
            super(initialCapacity);
        }

        // For deserialization
        Alert(Map<String, Object> delegate) {
            super(delegate);
        }

        public Alert putValue(String key, Object value){
            super.putValue(key, value);
            return this;
        }

        public Alert putBackgroundImage(String backgroundImage){
            return putValue(KEY_BACKGROUND_IMAGE, backgroundImage);
        }

        public Alert putPopupPosition(String popupPosition){
            return putValue(KEY_POPUP_POSITION, popupPosition);
        }

        public Alert putContentType(String contentType){
            return putValue(KEY_CONTENT_TYPE, contentType);
        }

        public Alert putPopupId(String popupId){
            return putValue(KEY_POPUP_ID, popupId);
        }

        public Alert putUrlTarget(String urlTarget){
            return putValue(KEY_URL_TARGET, urlTarget);
        }

        public Alert putPopupUrl(String popupUrl){
            return putValue(KEY_POPUP_URL, popupUrl);
        }

        public Alert putTitle(String title){
            return putValue(KEY_TITLE, title);
        }

        public Alert putBody(String body){
            return putValue(KEY_BODY, body);
        }

        public Alert putBodyHTML(String body_html){
            return putValue(KEY_BODY_HTML, body_html);
        }

        public String getBody(){
            return getString(KEY_BODY);
        }

        public String getBodyHTML(){
            return getString(KEY_BODY_HTML);
        }

        public String getTitle(){
            return getString(KEY_TITLE);
        }

        public String getPopupId(){
            return getString(KEY_POPUP_ID);
        }

        public String getContentType(){
            return getString(KEY_CONTENT_TYPE);
        }

        public Alert putDestinationScreen(String screen){
            return putValue(KEY_SCREEN_TO, screen);
        }

        public String getDesScreen(){
            return getString(KEY_SCREEN_TO);
        }

        public Alert putFromScreen(String screen){
            return putValue(KEY_SCREEN_FROM, screen);
        }

        public String getFromScreen(){
            return getString(KEY_SCREEN_FROM);
        }
    }
}
