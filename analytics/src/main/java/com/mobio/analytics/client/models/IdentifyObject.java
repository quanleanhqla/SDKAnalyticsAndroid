package com.mobio.analytics.client.models;

public class IdentifyObject {
    private String cif;
    private String phoneNumber;
    private String customerId;
    private String email;

    public IdentifyObject(Builder builder) {
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.cif = builder.cif;
        this.customerId = builder.customerId;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCif() {
        return cif;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }

    public static class Builder{
        private String cif;
        private String phoneNumber;
        private String email;
        private String customerId;

        public Builder(){}

        public IdentifyObject.Builder withCif(String cif){
            this.cif = cif;
            return this;
        }

        public IdentifyObject.Builder withCustomerId(String customerId){
            this.customerId = customerId;
            return this;
        }

        public IdentifyObject.Builder withEmail(String email){
            this.email = email;
            return this;
        }

        public IdentifyObject.Builder withPhoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }

        public IdentifyObject build(){
            return new IdentifyObject(this);
        }

    }

    @Override
    public String toString() {
        return "ProfileInfoObject{" +
                "cif='" + cif + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
