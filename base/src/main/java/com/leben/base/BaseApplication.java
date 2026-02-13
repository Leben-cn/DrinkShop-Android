package com.leben.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.config.AppConfig;

public class BaseApplication extends Application {
    // 提供一个静态的 Context，方便在类似 Utils 里使用
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        initARouter();

        // 2. 初始化其他基础库 (比如数据库、日志库、CrashHandler)
        // initDatabase();
    }

    private void initARouter() {
        if (AppConfig.DEBUG_ENABLE) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    public static Context getAppContext() {
        return context;
    }

}
