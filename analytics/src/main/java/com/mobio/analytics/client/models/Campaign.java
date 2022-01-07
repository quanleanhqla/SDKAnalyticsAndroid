package com.mobio.analytics.client.models;

public class Campaign {
    private String name;
    private String content;
    private String campaignId;

    public Campaign(String name, String content, String campaignId) {
        this.name = name;
        this.content = content;
        this.campaignId = campaignId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }
}
