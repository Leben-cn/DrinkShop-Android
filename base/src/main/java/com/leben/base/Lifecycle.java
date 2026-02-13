package com.leben.base;

import android.content.Intent;

public interface Lifecycle {
    /**
     * 初始化
     */
    void onInit();
    /**
     * 初始化 View
     */
    void initView();

    /**
     * 初始化事件监听
     */
    void initListener();

    /**
     * 初始化数据
     */
    void initData();
    void onStart();
    void onStop();
    void onResume() ;
    void onPause();
    /**
     * 撤消
     */
    void onDestroy();

    void onRetry();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
