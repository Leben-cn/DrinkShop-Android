package com.leben.shop.model.network;

import android.text.TextUtils;
import com.leben.base.BaseApplication;
import com.leben.base.util.LogUtils;
import com.leben.base.util.SharedPreferencesUtils;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {
    private static NetworkManager instance;
    private final Retrofit retrofit;

    private NetworkManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // 添加日志拦截器
        // 只有加了这个，Logcat 才会打印请求头、参数、返回结果
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtils.debug("OkHttp: " + message);
            }
        });

        // 设置打印级别为 BODY (这是最详细的，包含参数和返回json)
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // 将日志拦截器添加到 builder
        builder.addInterceptor(loggingInterceptor);
        // 添加 Token 拦截器
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                String token = SharedPreferencesUtils.getParam(BaseApplication.getAppContext(), "token", "");

                if (TextUtils.isEmpty(token)) {
                    return chain.proceed(originalRequest);
                }
                Request newRequest = originalRequest.newBuilder()
                        .addHeader("Authorization", token)
                        .method(originalRequest.method(), originalRequest.body())
                        .build();
                return chain.proceed(newRequest);
            }
        });

        // 3. 生成 Client
        OkHttpClient client = builder.build();

        // 4. 配置 Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.94:8080")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static NetworkManager getInstance() {
        if (instance == null) {
            synchronized (NetworkManager.class) {
                if (instance == null) {
                    instance = new NetworkManager();
                }
            }
        }
        return instance;
    }

    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}