package com.leben.base;

import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

public class LifecycleManage<T extends Lifecycle> implements Lifecycle {

    private final Map<String,T> lifecycleMap;

    public LifecycleManage() {
        lifecycleMap = new HashMap<String,T>();
    }

    public Map<String, T> getIifecycleMap() {
        return lifecycleMap;
    }

    public void register(String key, T lifecycle) {
        lifecycleMap.put(key, lifecycle);
    }

    public void unregister(String key) {
        lifecycleMap.remove(key);
    }

    @Override
    public void onInit() {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().onInit();
        }
    }

    @Override
    public void initView() {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().initView();
        }
    }

    @Override
    public void initListener() {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().initListener();
        }
    }

    @Override
    public void initData() {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().initData();
        }
    }

    @Override
    public void onStart() {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().onStart();
        }
    }

    @Override
    public void onStop() {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().onStop();
        }
    }

    @Override
    public void onResume() {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().onResume();
        }
    }

    @Override
    public void onPause() {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().onPause();
        }
    }

    @Override
    public void onDestroy() {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().onDestroy();
        }

    }

    @Override
    public void onRetry() {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().onRetry();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Map.Entry<String, T> entry : lifecycleMap.entrySet()) {
            entry.getValue().onActivityResult(requestCode, resultCode, data);
        }
    }

    public T get(String key) {
        return lifecycleMap.get(key);
    }

}
