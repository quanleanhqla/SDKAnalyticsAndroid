package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class ProfileInfoObject extends ProfileBaseObject{
    @SerializedName("cif")
    private String cif;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("email")
    private String email;

    public ProfileInfoObject(Builder builder) {
        super();
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.cif = builder.cif;
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

    public static class Builder{
        private String cif;
        private String phoneNumber;
        private String email;

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

        public ProfileInfoObject build(){
            return new ProfileInfoObject(this);
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
