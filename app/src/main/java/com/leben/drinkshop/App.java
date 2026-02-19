package com.leben.drinkshop;

import android.content.Context;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.services.core.ServiceSettings;
import com.leben.base.BaseApplication;

public class App extends BaseApplication {

    // 【核心大招】使用 attachBaseContext
    // 这个方法比 onCreate 执行得更早！神仙也挡不住在这里设置隐私！
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        try {
            // 1. 地图合规
            MapsInitializer.updatePrivacyShow(base, true, true);
            MapsInitializer.updatePrivacyAgree(base, true);

            // 2. 定位合规 (解决 101 报错的关键)
            AMapLocationClient.updatePrivacyShow(base, true, true);
            AMapLocationClient.updatePrivacyAgree(base, true);

            // 3. 搜索合规
            ServiceSettings.updatePrivacyShow(base, true, true);
            ServiceSettings.updatePrivacyAgree(base, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 这里不需要再写了，attachBaseContext 里写过就行
    }
}