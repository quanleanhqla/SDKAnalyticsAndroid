package com.mobio.analytics.client.model.old;

import com.google.gson.annotations.SerializedName;

public class ScreenObject {
    @SerializedName("height")
    private int height;

    @SerializedName("width")
    private int width;

    public ScreenObject(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "ScreenObject{" +
                "height=" + height +
                ", width=" + width +
                '}';
    }
}
