package com.mobio.analytics.client.model.digienty;

import java.util.Map;

public class Journey extends Properties{
    private static final String ID = "id";
    private static final String NODE_ID = "node_id";
    private static final String INSTANCE_ID = "instance_id";
    private static final String MASTER_CAMPAIGN_ID = "master_campaign_id";
    private static final String MERCHANT_ID = "merchant_id";
    private static final String POPUP_ID = "popup_id";
    private static final String PROFILE_ID = "profile_id";
    private static final String KEY_SEND_ID = "send_id";
    private static final String KEY_CODE = "code";
    private static final String KEY_INTERACTIVE_TIME = "interactive_time";

    public Journey() {}

    public Journey(int initialCapacity) {
        super(initialCapacity);
    }

    // For deserialization
    Journey(Map<String, Object> delegate) {
        super(delegate);
    }

    public Journey putValue(String key, Object value){
        super.putValue(key, value);
        return this;
    }

    public Journey putId(String id){
        return putValue(ID, id);
    }

    public Journey putNodeId(String nodeId){
        return putValue(NODE_ID, nodeId);
    }

    public Journey putInstanceId(String instanceId){
        return putValue(INSTANCE_ID, instanceId);
    }

    public Journey putMasterCampaignId(String masterCampaignId){
        return putValue(MASTER_CAMPAIGN_ID, masterCampaignId);
    }

    public Journey putMerchantId(String merchantId){
        return putValue(MERCHANT_ID, merchantId);
    }

    public Journey putPopupId(String popupId){
        return putValue(POPUP_ID, popupId);
    }

    public Journey putProfileId(String profileId){
        return putValue(PROFILE_ID, profileId);
    }

    public Journey putSendId(String send_id){
        return putValue(KEY_SEND_ID, send_id);
    }

    public Journey putCode(String code){
        return putValue(KEY_CODE, code);
    }

    public Journey putInteractiveTime(long time){
        return putValue(KEY_INTERACTIVE_TIME, time);
    }
}
