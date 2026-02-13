package com.leben.drinkshop;

import com.leben.base.BaseApplication;

public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        // 这里的 super.onCreate() 会自动执行 Base 里的 initARouter()

        // 如果 App 模块有自己特有的初始化，写在这里
        // 比如：只在顾客端使用的推送服务
        // initPushService();
    }
}
