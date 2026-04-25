// common 模块中的 LocationManager.java
package com.leben.common;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.leben.base.util.LogUtils;
import com.leben.common.model.event.LocationEvent;

import org.greenrobot.eventbus.EventBus;

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

    public void startSingleLocation(Context context) {
        try {
            AMapLocationClient client = new AMapLocationClient(context.getApplicationContext());
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setOnceLocation(true);
            option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            client.setLocationOption(option);

            client.setLocationListener(location -> {
                if (location != null && location.getErrorCode() == 0) {
                    // 1. 直接拿 POI 名字 (例如: "中移(杭州)信息技术有限公司")
                    // getPoiName() 返回的是 String，直接用就好
                    String addressName = location.getPoiName();

                    // 2. 如果 POI 名字为空，保底用详细地址描述
                    if (addressName == null || addressName.isEmpty()) {
                        addressName = location.getAddress();
                    }

                    // 3. 发送跨模块事件
                    EventBus.getDefault().post(new LocationEvent(
                            location.getLatitude(),
                            location.getLongitude(),
                            addressName
                    ));

                    // 4. 更新单例缓存
                    updateLocation(location.getLatitude(), location.getLongitude());
                }

                // 5. 必须调用 onDestroy() 释放资源
                client.onDestroy();
            });
            client.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}