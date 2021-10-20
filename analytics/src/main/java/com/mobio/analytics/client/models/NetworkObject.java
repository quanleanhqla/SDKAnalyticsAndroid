package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class NetworkObject {
    @SerializedName("cellular")
    private boolean cellular;

    @SerializedName("bluetooth")
    private boolean bluetooth;

    @SerializedName("wifi")
    private boolean wifi;

    public NetworkObject(boolean cellular, boolean bluetooth, boolean wifi) {
        this.cellular = cellular;
        this.bluetooth = bluetooth;
        this.wifi = wifi;
    }

    public boolean isCellular() {
        return cellular;
    }

    public void setCellular(boolean cellular) {
        this.cellular = cellular;
    }

    public boolean isBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(boolean bluetooth) {
        this.bluetooth = bluetooth;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    @Override
    public String toString() {
        return "NetworkObject{" +
                "cellular=" + cellular +
                ", bluetooth=" + bluetooth +
                ", wifi=" + wifi +
                '}';
    }
}
