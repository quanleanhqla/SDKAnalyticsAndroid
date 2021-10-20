package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class ContextObject {
    @SerializedName("device")
    private DeviceObject device;

    @SerializedName("app")
    private AppObject app;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("traits")
    private TraitsObject traits;

    @SerializedName("os")
    private OsObject os;

    @SerializedName("screen")
    private ScreenObject screen;

    @SerializedName("network")
    private NetworkObject network;

    @SerializedName("userAgent")
    private String userAgent;

    @SerializedName("locale")
    private String locale;

    @SerializedName("library")
    private LibraryObject library;

    public ContextObject(Builder builder) {
        this.device = builder.device;
        this.app = builder.app;
        this.timezone = builder.timezone;
        this.traits = builder.traits;
        this.os = builder.os;
        this.screen = builder.screen;
        this.network = builder.network;
        this.userAgent = builder.userAgent;
        this.locale = builder.locale;
        this.library = builder.library;
    }

    public static class Builder{
        private DeviceObject device;
        private AppObject app;
        private String timezone;
        private TraitsObject traits;
        private OsObject os;
        private ScreenObject screen;
        private NetworkObject network;
        private String userAgent;
        private String locale;
        private LibraryObject library;

        public Builder(){
        }

        public Builder withDevice(DeviceObject device){
            this.device = device;
            return this;
        }

        public Builder withApp(AppObject app){
            this.app = app;
            return this;
        }

        public Builder withTimezone(String timezone){
            this.timezone = timezone;
            return this;
        }

        public Builder withTraits(TraitsObject traits){
            this.traits = traits;
            return this;
        }

        public Builder withOs(OsObject os){
            this.os = os;
            return this;
        }

        public Builder withScreen(ScreenObject screen){
            this.screen = screen;
            return this;
        }

        public Builder withNetwork(NetworkObject network){
            this.network = network;
            return this;
        }

        public Builder withUserAgent(String userAgent){
            this.userAgent = userAgent;
            return this;
        }

        public Builder withLocale(String locale){
            this.locale = locale;
            return this;
        }

        public Builder withLibrary(LibraryObject library){
            this.library = library;
            return this;
        }

        public ContextObject build(){
            return new ContextObject(this);
        }
    }

    public DeviceObject getDevice() {
        return device;
    }

    public void setDevice(DeviceObject device) {
        this.device = device;
    }

    public AppObject getApp() {
        return app;
    }

    public void setApp(AppObject app) {
        this.app = app;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public TraitsObject getTraits() {
        return traits;
    }

    public void setTraits(TraitsObject traits) {
        this.traits = traits;
    }

    public OsObject getOs() {
        return os;
    }

    public void setOs(OsObject os) {
        this.os = os;
    }

    public ScreenObject getScreen() {
        return screen;
    }

    public void setScreen(ScreenObject screen) {
        this.screen = screen;
    }

    public NetworkObject getNetwork() {
        return network;
    }

    public void setNetwork(NetworkObject network) {
        this.network = network;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public LibraryObject getLibrary() {
        return library;
    }

    public void setLibrary(LibraryObject library) {
        this.library = library;
    }

    @Override
    public String toString() {
        return "ContextObject{" +
                "device=" + device +
                ", app=" + app +
                ", timezone='" + timezone + '\'' +
                ", traits=" + traits +
                ", os=" + os +
                ", screen=" + screen +
                ", network=" + network +
                ", userAgent='" + userAgent + '\'' +
                ", locale='" + locale + '\'' +
                ", library=" + library +
                '}';
    }
}
