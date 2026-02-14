package com.leben.merchant.model.bean;


public class MerchantRegisterEntity {
    public String account;
    public String password;
    public String phone;
    public String name;
    public String img;
    public String description;
    public Double minOrder;
    public Double deliveryFee;
    public Double longitude;
    public Double latitude;

    public MerchantRegisterEntity(){

    }

    public MerchantRegisterEntity(String account, String password, String phone, String name, String img, String description, Double minOrder, Double deliveryFee, Double longitude, Double latitude) {
        this.account = account;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.img = img;
        this.description = description;
        this.minOrder = minOrder;
        this.deliveryFee = deliveryFee;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(Double minOrder) {
        this.minOrder = minOrder;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
