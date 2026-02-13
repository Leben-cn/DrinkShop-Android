package com.leben.common.util;

import com.leben.common.model.bean.CommonEntity;

import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxUtils {

    /**
     * 1. 线程调度：IO 线程请求，主线程回调
     * 2. 剥壳：把 CommonEntity<T> 剥掉，直接变成 T
     * 3. 错误处理：如果 code!=200，直接抛错
     */
    public static <T> FlowableTransformer<CommonEntity<T>, T> rxSchedulerHelper() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    // 在这里统一判断业务逻辑
                    if (response.isSuccess()) {
                        return response.getData(); // 成功：只返回 data (即 PageData)
                    } else {
                        throw new RuntimeException(response.getMessage());
                    }
                });
    }
}