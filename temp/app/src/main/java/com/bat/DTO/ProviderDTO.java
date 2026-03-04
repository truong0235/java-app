package com.bat.DTO;

public class ProviderDTO {
    private int providerId;
    private String providerName;
    private String address;
    private String phone;
    private String email;

    public ProviderDTO() {}

    public ProviderDTO(int providerId, String providerName, String address, String phone, String email) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
