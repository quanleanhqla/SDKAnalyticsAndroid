package com.mobio.analytics.client.models;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

public class ProfileInfoObject extends ProfileBaseObject{
    @SerializedName("cif")
    private String cif;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("email")
    private String email;

    @SerializedName("source")
    private String source;

    public ProfileInfoObject(Builder builder, Context context) {
        super(context);
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.cif = builder.cif;
        this.source = "APP";
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public static class Builder{
        private String cif;
        private String phoneNumber;
        private String email;
        private Context context;

        public Builder(){}

        public Builder withCif(String cif){
            this.cif = cif;
            return this;
        }

        public Builder withEmail(String email){
            this.email = email;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withContext(Context context){
            this.context = context;
            return this;
        }

        public ProfileInfoObject build(){
            return new ProfileInfoObject(this, context);
        }

    }

    @Override
    public String toString() {
        return "ProfileInfoObject{" +
                "cif='" + cif + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
