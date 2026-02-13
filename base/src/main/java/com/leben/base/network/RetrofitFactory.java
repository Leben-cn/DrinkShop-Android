package com.leben.base.network;

import androidx.annotation.NonNull;
import com.leben.base.config.AppConfig;
import com.leben.base.util.LogUtils;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class RetrofitFactory {

    private static final int DEFAULT_TIMEOUT = 10;

    public static OkHttpClient.Builder getBaseOkHttpBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // 1. 基础配置
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        // 2. 【核心修改】直接读取 AppConfig.DEBUG_ENABLE
        if (AppConfig.DEBUG_ENABLE) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NonNull String message) {
                    // 只有开启了调试模式才会打印，且 TAG 为 "Network"
                    LogUtils.debug("Network", message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        return builder;
    }
}