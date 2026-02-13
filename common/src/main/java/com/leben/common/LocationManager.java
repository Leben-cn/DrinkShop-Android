// common 模块中的 LocationManager.java
package com.leben.common;

import java.util.ArrayList;
import java.util.List;

public class LocationManager {
    private static volatile LocationManager instance;
    private Double latitude;
    private Double longitude;
    private List<LocationListener> listeners = new ArrayList<>();

    public interface LocationListener {
        void onLocationChanged(Double latitude, Double longitude);
    }

    private LocationManager() {}

    public static LocationManager getInstance() {
        if (instance == null) {
            synchronized (LocationManager.class) {
                if (instance == null) {
                    instance = new LocationManager();
                }
            }
        }
        return instance;
    }

    public void updateLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        notifyListeners();
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void registerListener(LocationListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(LocationListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (LocationListener listener : listeners) {
            if (listener != null) {
                listener.onLocationChanged(latitude, longitude);
            }
        }
    }

    public void clear() {
        listeners.clear();
    }
}