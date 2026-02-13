package com.leben.common.model.bean;

import java.io.Serializable;

public class AddressEntity implements Serializable {
    // 如果是修改，需要传id；新增传null
    private Long id;

    private String contactName;
    private String contactPhone;
    private String addressPoi;
    private String addressDetail;
    private double latitude;
    private double longitude;
    private boolean isDefault;

    public AddressEntity(){

    }

    // 构造方法
    public AddressEntity(String contactName, String contactPhone, String addressPoi, String addressDetail, double latitude, double longitude) {
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.addressPoi = addressPoi;
        this.addressDetail = addressDetail;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDefault = false; // 默认为false
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddressPoi() {
        return addressPoi;
    }

    public void setAddressPoi(String addressPoi) {
        this.addressPoi = addressPoi;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
