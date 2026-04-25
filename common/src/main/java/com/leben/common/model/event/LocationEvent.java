package com.leben.common.model.event;

public class LocationEvent {
    public double latitude;
    public double longitude;
    public String address; // 简短地址

    public LocationEvent(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
