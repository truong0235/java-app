package com.bat.DTO;

public class CustomerDTO {
    private int customerId;
    private String fullName;
    private String birthday;
    private String phone;
    private String address;

    public CustomerDTO() {
    }

    public CustomerDTO(int customerId, String fullName, String birthday, String phone, String address) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.birthday = birthday;
        this.phone = phone;
        this.address = address;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}