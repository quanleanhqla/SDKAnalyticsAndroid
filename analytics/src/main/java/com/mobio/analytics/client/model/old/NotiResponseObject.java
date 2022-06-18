package com.mobio.analytics.client.model.old;

import com.google.gson.annotations.SerializedName;

public class NotiResponseObject {
    @SerializedName("type")
    private int type;

    @SerializedName("source_screen")
    private String source_screen;

    @SerializedName("des_screen")
    private String des_screen;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("data")
    private String data;//1: html content, 2:native, 3:url
    private int width;
    private int height;
    private String pushId;

    public static final int TYPE_NATIVE = 0;
    public static final int TYPE_HTML = 1;
    public static final int TYPE_HTML_URL = 2;

    public NotiResponseObject(Builder builder) {
        type = builder.mType;
        source_screen = builder.mSource_screen;
        des_screen = builder.mDes_screen;
        title = builder.mTitle;
        content = builder.mContent;
        data = builder.mData;
        width = builder.mWidth;
        height = builder.mHeight;
        pushId = builder.mPushId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSource_screen() {
        return source_screen;
    }

    public void setSource_screen(String source_screen) {
        this.source_screen = source_screen;
    }

    public String getDes_screen() {
        return des_screen;
    }

    public void setDes_screen(String des_screen) {
        this.des_screen = des_screen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public static class Builder{
        private int mType;
        private String mSource_screen;
        private String mDes_screen;
        private String mTitle;
        private String mContent;
        private String mData;//1: html content, 2:native, 3:url
        private int mWidth;
        private int mHeight;
        private String mPushId;

        public Builder(){
        }

        public Builder withType(int type){
            mType = type;
            return this;
        }

        public Builder withSource_screen(String source_screen){
            mSource_screen = source_screen;
            return this;
        }

        public Builder withDes_screen(String des_screen){
            mDes_screen = des_screen;
            return this;
        }

        public Builder withTitle(String title){
            mTitle = title;
            return this;
        }

        public Builder withContent(String content){
            mContent = content;
            return this;
        }

        public Builder withData(String data){
            mData = data;
            return this;
        }

        public Builder withWidth(int width){
            mWidth = width;
            return this;
        }

        public Builder withHeight(int height){
            mHeight = height;
            return this;
        }

        public Builder withPushId(String pushId){
            mPushId = pushId;
            return this;
        }

        public NotiResponseObject build(){
            return new NotiResponseObject(this);
        }
    }
}
